/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.interner;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

public final class InternerHelper
{
    private InternerHelper ()
    {
    }

    private static class NullSafeInterner implements Interner<String>
    {
        private final Interner<String> interner;

        private NullSafeInterner ( final Interner<String> interner )
        {
            this.interner = interner;
        }

        @Override
        public String intern ( final String string )
        {
            if ( string == null )
            {
                return null;
            }
            return this.interner.intern ( string );
        };
    }

    public static Interner<String> makeInterner ( final String specificPropertyName, final String defaultType )
    {
        final String type = System.getProperty ( specificPropertyName, System.getProperty ( "org.openscada.defaultStringInterner", defaultType ) );
        if ( "weak".equals ( type ) )
        {
            return new NullSafeInterner ( Interners.<String> newWeakInterner () );
        }
        else if ( "strong".equals ( type ) )
        {
            return new NullSafeInterner ( Interners.<String> newStrongInterner () );
        }
        else
        {
            return makeNoOpInterner ();
        }
    }

    public static Interner<String> makeNoOpInterner ()
    {
        return new Interner<String> () {

            @Override
            public String intern ( final String string )
            {
                return string;
            }
        };
    }
}
