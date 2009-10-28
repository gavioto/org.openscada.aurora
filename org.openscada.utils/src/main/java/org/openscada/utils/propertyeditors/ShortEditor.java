package org.openscada.utils.propertyeditors;

public class ShortEditor extends NumberEditor
{
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( Short.valueOf ( text ) );
    }
}
