package org.openscada.utils.propertyeditors;

public class DoubleEditor extends NumberEditor
{
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( Double.valueOf ( text ) );
    }
}
