package org.openscada.utils.filter;

public class FilterEmpty implements Filter
{

    public boolean isAssertion ()
    {
        return false;
    }

    public boolean isEmpty ()
    {
        return true;
    }

    public boolean isExpression ()
    {
        return false;
    }
}
