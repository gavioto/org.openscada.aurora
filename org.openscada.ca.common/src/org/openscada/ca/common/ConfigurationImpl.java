package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.ConfigurationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationImpl implements Configuration
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationImpl.class );

    private final String id;

    private final AbstractConfigurationAdminImpl admin;

    private ConfigurationState state;

    private Map<String, String> data;

    private final FactoryImpl factory;

    private Throwable error;

    private boolean deleted;

    public ConfigurationImpl ( final String id, final AbstractConfigurationAdminImpl admin, final FactoryImpl factory, final ConfigurationState state, final Map<String, String> data )
    {
        this.id = id;
        this.factory = factory;
        this.admin = admin;
        this.state = state;
        if ( data != null )
        {
            this.data = data;
        }
        else
        {
            this.data = new HashMap<String, String> ();
        }
    }

    public void delete ()
    {
        logger.debug ( "Request delete" );
        synchronized ( this )
        {
            checkDeleted ();
            update ( null );
            this.deleted = true;
        }
    }

    private synchronized void checkDeleted ()
    {
        if ( this.deleted )
        {
            throw new IllegalStateException ( "Configuration is already deleted" );
        }
    }

    public Throwable getErrorInformation ()
    {
        return this.error;
    }

    public FactoryImpl getFactory ()
    {
        return this.factory;
    }

    public String getId ()
    {
        return this.id;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void update ( final Map<String, String> properties )
    {
        logger.debug ( "Request update: " + properties );

        synchronized ( this )
        {
            checkDeleted ();
            this.data = properties;
            if ( this.data != null )
            {
                this.data.put ( "id", this.id );
            }
            this.admin.performUpdateConfiguration ( this );
        }
    }

    public synchronized void setApplied ()
    {
        this.state = ConfigurationState.APPLIED;
        this.error = null;
        fireStateChange ();
    }

    private void fireStateChange ()
    {
        this.admin.getListenerTracker ().fireEvent ( new ConfigurationEvent ( ConfigurationEvent.Type.STATE, this ) );
    }

    public synchronized void setApplyError ( final Throwable e )
    {
        this.state = ConfigurationState.ERROR;
        this.error = e;
        fireStateChange ();
    }

    public Map<String, String> getData ()
    {
        synchronized ( this )
        {
            if ( this.deleted )
            {
                return null;
            }
            return new HashMap<String, String> ( this.data );
        }
    }

    public synchronized boolean isDeleted ()
    {
        return this.deleted;
    }

    public synchronized void setAvailable ()
    {
        this.state = ConfigurationState.AVAILABLE;
        fireStateChange ();
    }

}
