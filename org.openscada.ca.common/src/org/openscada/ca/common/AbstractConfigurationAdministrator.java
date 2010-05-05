/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ca.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAlreadyExistsException;
import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.DiffEntry;
import org.openscada.ca.FactoryEvent;
import org.openscada.ca.FactoryNotFoundException;
import org.openscada.ca.FactoryState;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.utils.concurrent.AbstractFuture;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigurationAdministrator implements FreezableConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractConfigurationAdministrator.class );

    private final BundleContext context;

    private final ExecutorService executor;

    private final ListenerTracker listenerTracker;

    private final Map<String, FactoryImpl> factories = new HashMap<String, FactoryImpl> ();

    private final Map<ServiceReference, ConfigurationFactory> services = new HashMap<ServiceReference, ConfigurationFactory> ();

    private final Map<ServiceReference, SelfManagedConfigurationFactory> selfServices = new HashMap<ServiceReference, SelfManagedConfigurationFactory> ();

    private final ServiceTracker serviceListener;

    private final ServiceTracker selfServiceListener;

    public AbstractConfigurationAdministrator ( final BundleContext context )
    {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "Configuration Administrator" ) );

        this.listenerTracker = new ListenerTracker ( context );
        this.serviceListener = new ServiceTracker ( context, ConfigurationFactory.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                AbstractConfigurationAdministrator.this.removedService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
            }

            public Object addingService ( final ServiceReference reference )
            {
                return AbstractConfigurationAdministrator.this.addingService ( reference );
            }
        } );
        this.selfServiceListener = new ServiceTracker ( context, SelfManagedConfigurationFactory.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                AbstractConfigurationAdministrator.this.removedSelfService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
            }

            public Object addingService ( final ServiceReference reference )
            {
                return AbstractConfigurationAdministrator.this.addingSelfService ( reference );
            }
        } );
    }

    public synchronized void start () throws Exception
    {
        this.listenerTracker.open ();
        this.serviceListener.open ();
        this.selfServiceListener.open ();
    }

    public synchronized void stop () throws Exception
    {
        this.serviceListener.close ();
        this.selfServiceListener.close ();
        this.listenerTracker.close ();
    }

    public synchronized void freeze () throws Exception
    {
        stop ();
    }

    public synchronized void thaw () throws Exception
    {
        start ();
    }

    protected synchronized void addStoredFactory ( final String factoryId, final ConfigurationImpl[] configurations )
    {
        logger.info ( "Adding stored factory: {} ({})", new Object[] { factoryId, configurations.length } );

        FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            factory = new FactoryImpl ( factoryId );
            setFactoryState ( factory, FactoryState.LOADED );
            this.factories.put ( factoryId, factory );
            // FIXME: announce new factory
        }
        factory.setConfigurations ( configurations );

        final ConfigurationFactory factoryService = factory.getConfigurationFactoryService ();
        if ( factoryService != null )
        {
            scheduleBind ( configurations, factoryService, factory );
        }
    }

    private synchronized void setConfigurationStatus ( final ConfigurationImpl configuration, final ConfigurationState configurationState, final Throwable error )
    {
        configuration.setState ( configurationState, error );
        this.listenerTracker.fireEvent ( new ConfigurationEvent ( ConfigurationEvent.Type.STATE, configuration, configurationState, error ) );
    }

    protected synchronized void setFactoryState ( final FactoryImpl factory, final FactoryState state )
    {
        factory.setState ( state );
        this.listenerTracker.fireEvent ( new FactoryEvent ( FactoryEvent.Type.STATE, factory, state ) );
    }

    private synchronized void scheduleBind ( final ConfigurationImpl[] configurations, final ConfigurationFactory factoryService, final FactoryImpl factory )
    {
        setFactoryState ( factory, FactoryState.BINDING );

        // set all to "APPLYING" now
        for ( final ConfigurationImpl cfg : configurations )
        {
            setConfigurationStatus ( cfg, ConfigurationState.APPLYING, null );
        }

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ConfigurationImpl cfg : configurations )
                {
                    applyConfiguration ( null, factoryService, factory, cfg );
                }
                setFactoryState ( factory, FactoryState.BOUND );
            }
        } );
    }

    protected synchronized void addFactoryService ( final String factoryId, final ConfigurationFactory service, final String description )
    {
        FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            factory = new FactoryImpl ( factoryId );
            this.factories.put ( factoryId, factory );

            // FIXME: announce new factory
            setFactoryState ( factory, FactoryState.BINDING );
        }

        if ( factory.getService () == null )
        {
            factory.setDescription ( description );
            factory.setService ( service );

            scheduleBind ( factory.getConfigurations (), service, factory );
        }
    }

    protected synchronized void removeFactoryService ( final String factoryId, final ConfigurationFactory service )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return;
        }

        if ( factory.getService () == service )
        {
            // remove service
            for ( final ConfigurationImpl configuration : factory.getConfigurations () )
            {
                configuration.setState ( ConfigurationState.AVAILABLE, null );
            }

            factory.setService ( null );
            setFactoryState ( factory, FactoryState.LOADED );
        }
    }

    private synchronized void addFactorySelfService ( final String factoryId, final SelfManagedConfigurationFactory factoryService, final String description )
    {
        logger.info ( "Adding self service: {}", factoryId );
        // TODO Auto-generated method stub

        FactoryImpl factory = this.factories.get ( factoryId );
        if ( factory == null )
        {
            factory = new FactoryImpl ( factoryId );
            this.factories.put ( factoryId, factory );
        }

        if ( factory.getService () == null )
        {
            factory.setService ( factoryService );
            factory.setDescription ( description );

            // remove existing configurations
            factory.setConfigurations ( new ConfigurationImpl[0] );

            final ConfigurationListener listener = new ConfigurationListener () {

                public void configurationUpdate ( final Configuration[] addedOrChanged, final String[] deleted )
                {
                    AbstractConfigurationAdministrator.this.handleSelfChange ( factoryId, addedOrChanged, deleted );
                }
            };
            factory.setListener ( listener );
            factoryService.addConfigurationListener ( listener );
            setFactoryState ( factory, FactoryState.BOUND );
        }
    }

    protected synchronized void handleSelfChange ( final String factoryId, final Configuration[] addedOrChanged, final String[] deleted )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            logger.warn ( "Change for unknown factory: {}", factoryId );
            return;
        }

        if ( addedOrChanged != null )
        {
            for ( final Configuration cfg : addedOrChanged )
            {
                final ConfigurationImpl newCfg = new ConfigurationImpl ( cfg.getId (), factoryId, cfg.getData () );
                factory.addConfiguration ( newCfg );
                setConfigurationStatus ( newCfg, cfg.getState (), cfg.getErrorInformation () );
            }
        }
        if ( deleted != null )
        {
            for ( final String configurationId : deleted )
            {
                logger.info ( "Removing {} from self managed factory {}", new Object[] { configurationId, factoryId } );
                factory.removeConfigration ( configurationId );
            }
        }
    }

    private synchronized void removeSelfFactoryService ( final String factoryId, final SelfManagedConfigurationFactory factoryService )
    {
        logger.info ( "Removed self service: {}", factoryId );

        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return;
        }

        if ( factory.getService () == factoryService )
        {
            // remove service

            final ConfigurationListener listener = factory.getListener ();
            factoryService.removeConfigurationListener ( listener );

            factory.setService ( null );
            this.factories.remove ( factoryId );
        }

    }

    protected abstract void performPurge ( String factoryId, PurgeFuture future ) throws Exception;

    protected abstract void performStoreConfiguration ( String factoryId, String configurationId, Map<String, String> properties, boolean fullSet, ConfigurationFuture future ) throws Exception;

    protected abstract void performDeleteConfiguration ( String factoryId, String configurationId, ConfigurationFuture future ) throws Exception;

    /**
     * Request a change of the configuration
     * @param factoryId
     * @param configurationId
     * @param properties
     * @param future
     */
    protected synchronized void changeConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties, final ConfigurationFuture future )
    {
        logger.info ( "Request to change configuration: {}/{} -> {}", new Object[] { factoryId, configurationId, properties } );

        final FactoryImpl factory = getFactory ( factoryId );

        if ( factory == null )
        {
            logger.warn ( "Factory not found: {}", new Object[] { factoryId } );
            if ( future != null )
            {
                future.setError ( new FactoryNotFoundException ( factoryId ) );
            }
            return;
        }

        ConfigurationImpl configuration = factory.getConfiguration ( configurationId );
        if ( configuration != null )
        {
            // update data
            configuration.setData ( properties );

            if ( properties == null )
            {
                // delete
                factory.removeConfigration ( configurationId );
            }
        }
        else
        {
            if ( properties != null )
            {
                configuration = new ConfigurationImpl ( configurationId, factoryId, properties );
                factory.addConfiguration ( configuration );
            }
        }

        final ConfigurationFactory factoryService = factory.getConfigurationFactoryService ();

        if ( factoryService != null && configuration != null )
        {
            final ConfigurationImpl applyConfiguration = configuration;

            setConfigurationStatus ( configuration, ConfigurationState.APPLYING, null );

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    AbstractConfigurationAdministrator.this.applyConfiguration ( future, factoryService, factory, applyConfiguration );
                }
            } );
        }
        else
        {
            future.setResult ( configuration );
        }
    }

    /**
     * Apply the configuration to the assigned service
     * <p>
     * This method can block for some while
     * </p>
     * @param future
     * @param factory 
     * @param factoryService 
     * @param configuration
     */
    protected void applyConfiguration ( final ConfigurationFuture future, final ConfigurationFactory factoryService, final FactoryImpl factory, final ConfigurationImpl configuration )
    {
        logger.info ( "Apply configuration: {}/{} -> {}", new Object[] { factory.getId (), configuration.getId (), configuration.getData () } );

        try
        {
            final Map<String, String> properties = configuration.getData ();
            if ( properties != null )
            {
                logger.debug ( "Update configuration" );
                factoryService.update ( configuration.getId (), properties );
            }
            else
            {
                logger.debug ( "Delete configuration: {}", configuration.getId () );
                factoryService.delete ( configuration.getId () );
                // FIXME: notify remove                
            }
            synchronized ( this )
            {
                setConfigurationStatus ( configuration, ConfigurationState.APPLIED, null );
            }

            logger.info ( "Applied configuration: {}/{} -> {}", new Object[] { factory.getId (), configuration.getId (), configuration.getData () } );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Apply failed configuration: {}/{} -> {}", new Object[] { factory.getId (), configuration.getId (), configuration.getData () } );
            logger.warn ( "Apply failed configuration:", e );

            synchronized ( this )
            {
                setConfigurationStatus ( configuration, ConfigurationState.ERROR, e );
            }
        }

        // if the apply operation fails the configuration is still "ok"
        if ( future != null )
        {
            future.setResult ( configuration );
        }
    }

    public synchronized NotifyFuture<Void> purgeFactory ( final String factoryId )
    {
        logger.info ( "Request to purge: {}", factoryId );

        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return new InstantErrorFuture<Void> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        if ( !factory.isSelfManaged () )
        {
            return invokePurge ( factoryId );
        }
        else
        {
            return factory.getSelfService ().purge ();
        }
    }

    public synchronized NotifyFuture<Configuration> createConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        if ( factory.getConfiguration ( configurationId ) != null )
        {
            // this is not a create operation
            return new InstantErrorFuture<Configuration> ( new ConfigurationAlreadyExistsException ( factoryId, configurationId ).fillInStackTrace () );
        }

        if ( !factory.isSelfManaged () )
        {
            return invokeStore ( factoryId, configurationId, properties, true );
        }
        else
        {
            return factory.getSelfService ().update ( configurationId, properties, true );
        }
    }

    public synchronized NotifyFuture<Configuration> updateConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        if ( factory.getConfiguration ( configurationId ) == null )
        {
            // this is not a create operation
            return new InstantErrorFuture<Configuration> ( new ConfigurationNotFoundException ( factoryId, configurationId ).fillInStackTrace () );
        }

        if ( !factory.isSelfManaged () )
        {
            return invokeStore ( factoryId, configurationId, properties, fullSet );
        }
        else
        {
            return factory.getSelfService ().update ( configurationId, properties, fullSet );
        }
    }

    public synchronized NotifyFuture<Configuration> deleteConfiguration ( final String factoryId, final String configurationId )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        if ( factory == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        if ( factory.getConfiguration ( configurationId ) == null )
        {
            // this is not a create operation
            return new InstantErrorFuture<Configuration> ( new ConfigurationNotFoundException ( factoryId, configurationId ).fillInStackTrace () );
        }

        if ( !factory.isSelfManaged () )
        {
            return invokeDelete ( factoryId, configurationId );
        }
        else
        {
            return factory.getSelfService ().delete ( configurationId );
        }
    }

    protected static final class ConfigurationFuture extends AbstractFuture<Configuration>
    {
        public ConfigurationFuture ()
        {
        }

        @Override
        public void setError ( final Throwable error )
        {
            super.setError ( error );
        }

        @Override
        public void setResult ( final Configuration result )
        {
            super.setResult ( result );
        }
    }

    protected static class PatchFuture extends CompositeFuture<Configuration>
    {

    }

    protected static class PurgeFuture extends CompositeFuture<Configuration>
    {
    }

    protected static class CompositeFuture<T> extends AbstractFuture<Void>
    {
        private final static Logger logger = LoggerFactory.getLogger ( AbstractConfigurationAdministrator.PurgeFuture.class );

        private final Set<NotifyFuture<T>> futures = new HashSet<NotifyFuture<T>> ();

        private boolean complete = false;

        public synchronized void setComplete ()
        {
            this.complete = true;
            checkComplete ();
        }

        public synchronized void addChild ( final NotifyFuture<T> future )
        {
            this.futures.add ( future );

            logger.debug ( "Added future: {} - {} entries", new Object[] { future, this.futures.size () } );

            future.addListener ( new FutureListener<T> () {

                public void complete ( final Future<T> xfuture )
                {
                    CompositeFuture.this.removed ( future );
                }
            } );
        }

        protected synchronized void removed ( final NotifyFuture<T> future )
        {
            this.futures.remove ( future );
            logger.debug ( "Removed future: {} - {} entries remain", new Object[] { future, CompositeFuture.this.futures.size () } );
            checkComplete ();
        }

        protected synchronized void checkComplete ()
        {
            if ( this.complete && this.futures.isEmpty () )
            {
                logger.debug ( "Apply complete state" );
                super.setResult ( null );
            }
        }
    }

    private NotifyFuture<Void> invokePurge ( final String factoryId )
    {
        final PurgeFuture future = new PurgeFuture ();
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    performPurge ( factoryId, future );
                }
                catch ( final Throwable e )
                {
                    logger.warn ( "Failed to complete request", e );
                    future.setComplete ();
                }
            }
        } );
        return future;
    }

    private NotifyFuture<Configuration> invokeStore ( final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    performStoreConfiguration ( factoryId, configurationId, properties, fullSet, future );
                }
                catch ( final Throwable e )
                {
                    future.setError ( e );
                }
            }
        } );
        return future;
    }

    private NotifyFuture<Configuration> invokeDelete ( final String factoryId, final String configurationId )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    performDeleteConfiguration ( factoryId, configurationId, future );
                }
                catch ( final Throwable e )
                {
                    future.setError ( e );
                }
            }
        } );
        return future;
    }

    /* readers */

    public synchronized Configuration getConfiguration ( final String factoryId, final String configurationId )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        return factory.getConfiguration ( configurationId );
    }

    public synchronized Configuration[] getConfigurations ( final String factoryId )
    {
        final FactoryImpl factory = getFactory ( factoryId );
        return factory.getConfigurations ();
    }

    public synchronized FactoryImpl getFactory ( final String factoryId )
    {
        return this.factories.get ( factoryId );
    }

    public synchronized FactoryImpl[] getKnownFactories ()
    {
        return this.factories.values ().toArray ( new FactoryImpl[0] );
    }

    protected Object addingService ( final ServiceReference reference )
    {
        final String factoryId = checkAndGetFactoryId ( reference );
        final String description = getDescription ( reference );

        if ( factoryId == null )
        {
            // no factory id ... no service
            return null;
        }

        Object service = null;
        try
        {
            service = this.context.getService ( reference );
            final ConfigurationFactory factory = (ConfigurationFactory)service;

            addFactoryService ( factoryId, factory, description );

            this.services.put ( reference, factory );

            return factory;
        }
        catch ( final ClassCastException e )
        {
            if ( service != null )
            {
                this.context.ungetService ( reference );
            }
            return null;
        }
    }

    protected synchronized void removedService ( final ServiceReference reference, final Object service )
    {
        final ConfigurationFactory factoryService = this.services.remove ( reference );
        if ( factoryService != null )
        {
            this.context.ungetService ( reference );
            removeFactoryService ( (String)reference.getProperty ( FACTORY_ID ), factoryService );
        }
    }

    protected Object addingSelfService ( final ServiceReference reference )
    {
        final String factoryId = checkAndGetFactoryId ( reference );

        if ( factoryId == null )
        {
            return null;
        }

        final String description = getDescription ( reference );

        Object service = null;
        try
        {
            service = this.context.getService ( reference );
            final SelfManagedConfigurationFactory factory = (SelfManagedConfigurationFactory)service;

            addFactorySelfService ( factoryId, factory, description );

            this.selfServices.put ( reference, factory );

            return factory;
        }
        catch ( final ClassCastException e )
        {
            if ( service != null )
            {
                this.context.ungetService ( reference );
            }
            return null;
        }
    }

    private String getDescription ( final ServiceReference reference )
    {
        String description;
        if ( reference.getProperty ( Constants.SERVICE_DESCRIPTION ) instanceof String )
        {
            description = (String)reference.getProperty ( Constants.SERVICE_DESCRIPTION );
        }
        else
        {
            description = null;
        }
        return description;
    }

    protected void removedSelfService ( final ServiceReference reference, final Object service )
    {
        final SelfManagedConfigurationFactory factoryService = this.selfServices.remove ( reference );
        if ( factoryService != null )
        {
            this.context.ungetService ( reference );
            removeSelfFactoryService ( (String)reference.getProperty ( FACTORY_ID ), factoryService );
        }
    }

    private String checkAndGetFactoryId ( final ServiceReference reference )
    {
        if ( ! ( reference.getProperty ( FACTORY_ID ) instanceof String ) )
        {
            logger.warn ( "Found new service {} but it is missing 'factoryId' in its properties", reference );
            return null;
        }

        final String factoryId = (String)reference.getProperty ( FACTORY_ID );
        return factoryId;
    }

    public synchronized NotifyFuture<Void> applyDiff ( final Collection<DiffEntry> changeSet )
    {
        final PatchFuture future = new PatchFuture ();

        for ( final DiffEntry entry : changeSet )
        {
            switch ( entry.getOperation () )
            {
            case ADD:
                future.addChild ( createConfiguration ( entry.getFactoryId (), entry.getConfigurationId (), entry.getData () ) );
                break;
            case DELETE:
                future.addChild ( deleteConfiguration ( entry.getFactoryId (), entry.getConfigurationId () ) );
                break;
            case UPDATE:
                future.addChild ( updateConfiguration ( entry.getFactoryId (), entry.getConfigurationId (), entry.getData (), true ) );
                break;
            }
        }

        future.setComplete ();
        return future;
    }
}
