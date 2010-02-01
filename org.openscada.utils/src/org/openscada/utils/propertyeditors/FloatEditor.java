package org.openscada.utils.propertyeditors;

public class FloatEditor extends NumberEditor
{
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( Float.valueOf ( text ) );
    }

}
