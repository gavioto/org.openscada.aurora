package org.openscada.utils.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterExpression implements Filter
{

    private Operator operator;

    private List<Filter> filterSet = new ArrayList<Filter> ();

    @Override
    public String toString ()
    {
        StringBuilder result = new StringBuilder ();
        if ( operator == null )
        {
            for ( Filter filter : filterSet )
            {
                result.append ( filter.toString () );
            }
        }
        else
        {
            result.append ( "(" );
            result.append ( operator.toString () );
            for ( Filter filter : filterSet )
            {
                result.append ( filter.toString () );
            }
            result.append ( ")" );
        }
        return result.toString ();
    }

    public List<Filter> getFilterSet ()
    {
        return filterSet;
    }

    public Operator getOperator ()
    {
        return operator;
    }

    public void setOperator ( Operator operator )
    {
        this.operator = operator;
    }

    public boolean isAssertion ()
    {
        return false;
    }

    public boolean isExpression ()
    {
        return true;
    }

    public boolean isEmpty ()
    {
        return operator == null;
    }
}
