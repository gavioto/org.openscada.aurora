package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport
{
    private static final SimpleDateFormat dfDateTimeS = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss.S" ); // 23 

    private static final SimpleDateFormat dfDateTime = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" ); // 20

    private static final SimpleDateFormat dfDate = new SimpleDateFormat ( "yyyy-MM-dd" ); // 10 

    private static final SimpleDateFormat dfTimeS = new SimpleDateFormat ( "HH:mm:ss.S" ); // 12

    private static final SimpleDateFormat dfTime = new SimpleDateFormat ( "HH:mm:ss" ); // 8

    @Override
    public void setAsText ( final String text ) throws IllegalArgumentException
    {
        Date d = null;
        try
        {
            if ( text.length () == 23 )
            {
                d = dfDateTimeS.parse ( text );
            }
            else if ( text.length () == 20 )
            {
                d = dfDateTime.parse ( text );
            }
            else if ( text.length () == 10 )
            {
                d = dfDate.parse ( text );
            }
            else if ( text.length () == 12 )
            {
                d = dfTimeS.parse ( text );
            }
            else if ( text.length () == 8 )
            {
                d = dfTime.parse ( text );
            }
        }
        catch ( ParseException e )
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
        if ( ( value instanceof Integer ) || ( value instanceof Long ) )
        {
            v = new Date ( (Long)value );
        }
        super.setValue ( v );
    }
}
