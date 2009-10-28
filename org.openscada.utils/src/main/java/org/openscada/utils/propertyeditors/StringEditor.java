package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditorSupport;

public class StringEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText ()
    {
        return getValue ().toString ();
    }

    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( text );
    }
}
