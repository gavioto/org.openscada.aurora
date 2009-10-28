package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditorSupport;

class NumberEditor extends PropertyEditorSupport
{
    @Override
    public String getAsText ()
    {
        return getValue ().toString ();
    }
}
