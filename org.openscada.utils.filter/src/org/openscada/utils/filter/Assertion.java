package org.openscada.utils.filter;

import org.apache.directory.shared.ldap.filter.AbstractExprNode;

/**
 * @author jrose
 *
 */
public enum Assertion
{

    EQUALITY ( AbstractExprNode.EQUALITY ),
    PRESENCE ( AbstractExprNode.PRESENCE ),
    SUBSTRING ( AbstractExprNode.SUBSTRING ),
    GREATEREQ ( AbstractExprNode.GREATEREQ ),
    LESSEQ ( AbstractExprNode.LESSEQ ),
    APPROXIMATE ( AbstractExprNode.APPROXIMATE );

    private final int op;

    Assertion ( int operator )
    {
        this.op = operator;
    }

    public static Assertion fromValue ( int op )
    {
        switch ( op )
        {
        case AbstractExprNode.EQUALITY:
            return Assertion.EQUALITY;
        case AbstractExprNode.PRESENCE:
            return Assertion.PRESENCE;
        case AbstractExprNode.SUBSTRING:
            return Assertion.SUBSTRING;
        case AbstractExprNode.GREATEREQ:
            return Assertion.GREATEREQ;
        case AbstractExprNode.LESSEQ:
            return Assertion.LESSEQ;
        case AbstractExprNode.APPROXIMATE:
            return Assertion.APPROXIMATE;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case AbstractExprNode.APPROXIMATE:
            return "~=";
        case AbstractExprNode.EQUALITY:
            return "=";
        case AbstractExprNode.GREATEREQ:
            return ">=";
        case AbstractExprNode.LESSEQ:
            return "<=";
        case AbstractExprNode.PRESENCE:
            return "=*";
        case AbstractExprNode.SUBSTRING:
            return "=";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}