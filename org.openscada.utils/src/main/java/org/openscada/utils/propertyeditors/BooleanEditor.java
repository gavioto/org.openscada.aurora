package org.openscada.utils.propertyeditors;

public class BooleanEditor extends NumberEditor
{
    @Override
    public void setAsText ( String text ) throws IllegalArgumentException
    {
        setValue ( Boolean.valueOf ( text ) );
    }
}
