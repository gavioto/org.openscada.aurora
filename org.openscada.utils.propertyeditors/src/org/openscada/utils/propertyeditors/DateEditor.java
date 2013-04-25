/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport
{
    private final SimpleDateFormat dfDateTimeS = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.S" ); // 23 

    private final SimpleDateFormat dfDateTime = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ); // 19

    private final SimpleDateFormat dfDate = new SimpleDateFormat ( "yyyy-MM-dd" ); // 10 

    private final SimpleDateFormat dfTimeS = new SimpleDateFormat ( "HH:mm:ss.S" ); // 12

    private final SimpleDateFormat dfTime = new SimpleDateFormat ( "HH:mm:ss" ); // 8

    @Override
    public void setAsText ( final String text ) throws IllegalArgumentException
    {
        Date d = null;
        if ( text == null )
        {
            setValue ( null );
            return;
        }
        try
        {
            if ( text.length () == 23 )
            {
                d = this.dfDateTimeS.parse ( text );
            }
            else if ( text.length () == 19 )
            {
                d = this.dfDateTime.parse ( text );
            }
            else if ( text.length () == 10 )
            {
                d = this.dfDate.parse ( text );
            }
            else if ( text.length () == 12 )
            {
                d = this.dfTimeS.parse ( text );
            }
            else if ( text.length () == 8 )
            {
                d = this.dfTime.parse ( text );
            }
        }
        catch ( final ParseException e )
        {
            d = null;
        }
        setValue ( d );
    }

    @Override
    public void setValue ( final Object value )
    {
        Date v = null;
        if ( value instanceof Date )
        {
            v = new Date ( ( (Date)value ).getTime () );
        }
        if ( value instanceof Calendar )
        {
            v = ( (Calendar)value ).getTime ();
        }
        if ( value instanceof Integer || value instanceof Long )
        {
            v = new Date ( (Long)value );
        }
        super.setValue ( v );
    }
}
