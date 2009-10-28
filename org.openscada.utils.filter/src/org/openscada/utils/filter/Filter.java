package org.openscada.utils.filter;

public interface Filter
{

    public boolean isAssertion ();

    public boolean isExpression ();

    public boolean isEmpty ();
}
