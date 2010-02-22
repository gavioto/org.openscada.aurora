/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ca.jdbc.internal;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * This derived {@link LocalSessionFactoryBean} is needed because hibernate treats
 * a schema which contains only of an empty space just like any other name. So this
 * implementation removes the property <code>hibernate.default_schema</code> if
 * it is empty
 * 
 * @author Jürgen Rose
 */
public class CustomSessionFactoryBean extends LocalSessionFactoryBean
{
    @Override
    protected void postProcessConfiguration ( final Configuration config ) throws HibernateException
    {
        super.postProcessConfiguration ( config );
        if ( config.getProperty ( "hibernate.default_schema" ) != null && "".equals ( config.getProperty ( "hibernate.default_schema" ).trim () ) )
        {
            config.getProperties ().remove ( "hibernate.default_schema" );
        }
    }
}
