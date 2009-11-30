/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.utils.osgi.ca.factory;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.openscada.utils.lang.Disposable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration factory that creates simple beans and applies the configuration using
 * setters.
 * <p>
 * If the created bean supports {@link Disposable} then the {@link Disposable#dispose()}
 * method will be called when the object is being removed from the factory. 
 * </p> 
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public class BeanConfigurationFactory extends AbstractServiceConfigurationFactory<BeanConfigurationFactory.BeanServiceInstance>
{
    private final static Logger logger = LoggerFactory.getLogger ( BeanConfigurationFactory.class );

    private final Class<?> beanClazz;

    public BeanConfigurationFactory ( final BundleContext context, final Class<?> beanClazz )
    {
        super ( context );
        this.beanClazz = beanClazz;
    }

    protected static class BeanServiceInstance
    {
        private final Object targetBean;

        public Object getTargetBean ()
        {
            return this.targetBean;
        }

        public Dictionary<?, ?> getProperties ()
        {
            try
            {
                final Dictionary<Object, Object> result = new Hashtable<Object, Object> ();

                final Map<?, ?> properties = new BeanUtilsBean2 ().describe ( this.targetBean );
                for ( final Map.Entry<?, ?> entry : properties.entrySet () )
                {
                    if ( entry.getValue () != null )
                    {
                        result.put ( entry.getKey (), entry.getValue () );
                    }
                }
                return result;
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to get dictionary", e );
                return new Hashtable<Object, Object> ();
            }
        }

        public BeanServiceInstance ( final Object targetBean )
        {
            this.targetBean = targetBean;
        }

        public void update ( final Map<String, String> parameters ) throws Exception
        {
            new BeanUtilsBean2 ().populate ( this.targetBean, parameters );
        }
    }

    @Override
    protected Entry<BeanServiceInstance> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final BeanServiceInstance bean = new BeanServiceInstance ( this.beanClazz.newInstance () );
        bean.update ( parameters );

        final ServiceRegistration reg = context.registerService ( this.beanClazz.getName (), bean.getTargetBean (), bean.getProperties () );

        return new Entry<BeanServiceInstance> ( bean, reg );
    }

    @Override
    protected void disposeService ( final BeanServiceInstance service )
    {
        if ( service instanceof Disposable )
        {
            ( (Disposable)service ).dispose ();
        }
    }

    @Override
    protected void updateService ( final Entry<BeanConfigurationFactory.BeanServiceInstance> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        entry.getHandle ().setProperties ( entry.getService ().getProperties () );
    }

}
