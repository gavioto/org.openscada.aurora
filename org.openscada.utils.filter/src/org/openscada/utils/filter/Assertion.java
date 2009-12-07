package org.openscada.utils.filter;


/**
 * @author jrose
 *
 */
public enum Assertion
{

    EQUALITY ( 0 ),
    PRESENCE ( 1 ),
    SUBSTRING ( 2 ),
    GREATEREQ ( 3 ),
    LESSEQ ( 4 ),
    APPROXIMATE ( 6 ),

    LESSTHAN ( 7 ),
    GREATERTHAN ( 8 );

    private final int op;

    Assertion ( int operator )
    {
        this.op = operator;
    }

    public static Assertion fromValue ( int op )
    {
        switch ( op )
        {
        case 0:
            return Assertion.EQUALITY;
        case 1:
            return Assertion.PRESENCE;
        case 2:
            return Assertion.SUBSTRING;
        case 3:
            return Assertion.GREATEREQ;
        case 4:
            return Assertion.LESSEQ;
        case 6:
            return Assertion.APPROXIMATE;
        case 7:
            return Assertion.LESSTHAN;
        case 8:
            return Assertion.GREATERTHAN;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case 0:
            return "=";
        case 1:
            return "=*";
        case 2:
            return "=";
        case 3:
            return ">=";
        case 4:
            return "<=";
        case 6:
            return "~=";
        case 7:
            return "<";
        case 8:
            return ">";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}