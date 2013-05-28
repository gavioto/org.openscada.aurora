/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.sec.callback;

import java.util.Comparator;
import java.util.Map;

/**
 * @since 1.1
 */
public interface Callback
{
    public static Comparator<Callback> ORDER_COMPARATOR = new Comparator<Callback> () {
        @Override
        public int compare ( final Callback o1, final Callback o2 )
        {
            if ( o1 == o2 )
            {
                return 0;
            }

            if ( o2 == null )
            {
                return -1;
            }

            // both are non-null now

            return Integer.valueOf ( o1.getOrder () ).compareTo ( o2.getOrder () );
        };
    };

    public static long DEFAULT_TIMEOUT = Integer.getInteger ( "org.openscada.sec.callback.defaultTimeout", 20 * 1000 );

    public void cancel ();

    boolean isCanceled ();

    public int getOrder ();

    public Map<String, String> buildRequestAttributes ();

    public void parseResponseAttributes ( Map<String, String> attributes );

    public Map<String, String> buildResponseAttributes ();

    public void parseRequestAttributes ( Map<String, String> attributes );

    public String getType ();
}
