package org.openscada.utils.filter;

import org.apache.directory.shared.ldap.filter.AbstractExprNode;

/**
 * @author jrose
 *
 */
public enum Operator
{

    OR ( AbstractExprNode.OR ),
    AND ( AbstractExprNode.AND ),
    NOT ( AbstractExprNode.NOT );

    private final int op;

    Operator ( int operator )
    {
        this.op = operator;
    }

    public static Operator fromValue ( int op )
    {
        switch ( op )
        {
        case AbstractExprNode.OR:
            return Operator.OR;
        case AbstractExprNode.AND:
            return Operator.AND;
        case AbstractExprNode.NOT:
            return Operator.NOT;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case AbstractExprNode.OR:
            return "|";
        case AbstractExprNode.AND:
            return "&";
        case AbstractExprNode.NOT:
            return "!";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}
