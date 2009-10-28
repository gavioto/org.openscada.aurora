package org.openscada.utils.filter;

import org.openscada.utils.filter.internal.Encoder;

public class FilterAssertion implements Filter
{

    private String attribute;

    private Assertion assertion;

    private Object value;

    public FilterAssertion ( String attribute, Assertion assertion, Object value )
    {
        this.attribute = attribute;
        this.assertion = assertion;
        this.value = value;
    }

    public String getAttribute ()
    {
        return attribute;
    }

    public void setAttribute ( String attribute )
    {
        this.attribute = attribute;
    }

    public Assertion getAssertion ()
    {
        return assertion;
    }

    public void setAssertion ( Assertion assertion )
    {
        this.assertion = assertion;
    }

    public Object getValue ()
    {
        return value;
    }

    public void setValue ( Object value )
    {
        this.value = value;
    }

    @Override
    public String toString ()
    {
        return "(" + attribute + assertion.toString () + Encoder.encode ( value == null ? "" : value.toString () ) + ")";
    }

    public boolean isAssertion ()
    {
        return true;
    }

    public boolean isExpression ()
    {
        return false;
    }

    public boolean isEmpty ()
    {
        return assertion == null;
    }
}
