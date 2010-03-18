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

package org.openscada.utils.factory;

/**
 * The bean helper helps you to create beans ;-)
 * @author Jens Reimann
 *
 */
public class BeanHelper
{
    public static Object create ( final BeanFactory factory, final String reference ) throws BeanCreationException
    {
        try
        {
            return factory.create ( reference );
        }
        catch ( final Exception e )
        {
            throw new BeanCreationException ( e );
        }
    }

    public static Object create ( final Class<? extends BeanFactory> factoryClass, final String reference ) throws BeanCreationException
    {
        try
        {
            return factoryClass.newInstance ().create ( reference );
        }
        catch ( final Exception e )
        {
            throw new BeanCreationException ( e );
        }
    }

    @SuppressWarnings ( "unchecked" )
    public static Object create ( final String factoryClassName, final String reference ) throws BeanCreationException
    {
        try
        {
            return create ( (Class<BeanFactory>)Class.forName ( factoryClassName ), reference );
        }
        catch ( final ClassNotFoundException e )
        {
            throw new BeanCreationException ( e );
        }
    }

    /**
     * Create a new bean based with a required class
     * @param factoryClassName
     * @param reference
     * @param requiredClass the class that is required
     * @return the new instance which can be casted to requiredClass
     * @throws BeanCreationException in the case anything goes wrong or the bean created was not the required type
     */
    public static Object create ( final String factoryClassName, final String reference, final Class<?> requiredClass ) throws BeanCreationException
    {
        final Object o = create ( factoryClassName, reference );
        try
        {
            return requiredClass.cast ( o );
        }
        catch ( final ClassCastException e )
        {
            throw new BeanCreationException ( e );
        }
    }
}
