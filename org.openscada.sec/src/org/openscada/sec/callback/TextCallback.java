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

import java.util.Map;

/**
 * @since 1.1
 */
public class TextCallback extends LabelCallback
{
    public static final String TYPE = "text";

    public static final String PROP_VALUE = "value";

    private String value;

    public TextCallback ()
    {
    }

    public TextCallback ( final String label, final int order )
    {
        super ( label, order );
    }

    @Override
    public String getType ()
    {
        return TYPE;
    }

    public void setValue ( final String value )
    {
        this.value = value;
    }

    public String getValue ()
    {
        return this.value;
    }

    @Override
    public void parseResponseAttributes ( final Map<String, String> attributes )
    {
        this.value = attributes.get ( PROP_VALUE );
    }

    @Override
    protected void injectResponseAttributes ( final Map<String, String> result )
    {
        super.injectResponseAttributes ( result );
        if ( this.value != null )
        {
            result.put ( PROP_VALUE, this.value );
        }
    }
}
