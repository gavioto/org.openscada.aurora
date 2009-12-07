package org.openscada.utils.filter;


/**
 * @author jrose
 *
 */
public enum Operator
{
    OR ( 9 ),
    AND ( 10 ),
    NOT ( 11 );

    private final int op;

    Operator ( int operator )
    {
        this.op = operator;
    }

    public static Operator fromValue ( int op )
    {
        switch ( op )
        {
        case 9:
            return Operator.OR;
        case 10:
            return Operator.AND;
        case 11:
            return Operator.NOT;
        }
        return null;
    }
    
    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case 9:
            return "|";
        case 10:
            return "&";
        case 11:
            return "!";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}
