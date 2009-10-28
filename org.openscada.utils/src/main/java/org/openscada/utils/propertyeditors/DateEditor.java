package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport
{
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        Date d = null;
        SimpleDateFormat dfDateTime = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );
        SimpleDateFormat dfDate = new SimpleDateFormat ( "yyyy-MM-dd" );
        try
        {
            if ( text.length () > 10 )
            {
                d = dfDateTime.parse ( text );
            }
            else
            {
                d = dfDate.parse ( text );
            }
        }
        catch ( ParseException e )
        {
            d = null;
        }
        setValue ( d );
    }

    @Override
    public void setValue ( Object value )
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
