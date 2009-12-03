package org.openscada.utils.filter.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.filter.BranchNode;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.FilterVisitor;
import org.apache.directory.shared.ldap.filter.LeafNode;
import org.apache.directory.shared.ldap.filter.PresenceNode;
import org.apache.directory.shared.ldap.filter.SimpleNode;
import org.apache.directory.shared.ldap.filter.SubstringNode;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.FilterParseException;
import org.openscada.utils.filter.Operator;

public class FilterVisitorImpl implements FilterVisitor
{

    private Filter filter = null;

    public Filter getFilter ()
    {
        return filter;
    }

    public boolean canVisit ( ExprNode node )
    {
        return true;
    }

    @SuppressWarnings ( "unchecked" )
    public ArrayList getOrder ( BranchNode node, ArrayList children )
    {
        return children;
    }

    public boolean isPrefix ()
    {
        return false;
    }

    public void visit ( ExprNode node )
    {
        filter = toFilter ( node );
    }

    @SuppressWarnings ( "unchecked" )
    private Filter toFilter ( ExprNode node )
    {
        if ( node instanceof BranchNode )
        {
            BranchNode branchNode = (BranchNode)node;
            FilterExpression f = new FilterExpression ();
            f.setOperator ( Operator.fromValue ( branchNode.getOperator () ) );
            for ( ExprNode child : (ArrayList<ExprNode>)branchNode.getChildren () )
            {
                f.getFilterSet ().add ( toFilter ( child ) );
            }
            return f;
        }
        else if ( node instanceof SimpleNode )
        {
            SimpleNode simpleNode = (SimpleNode)node;
            return new FilterAssertion ( simpleNode.getAttribute (), Assertion.fromValue ( simpleNode.getAssertionType () ), simpleNode.getValue () );
        }
        else if ( node instanceof SubstringNode )
        {
            SubstringNode substringNode = (SubstringNode)node;
            List<String> values = new ArrayList<String> ();
            values.add(substringNode.getInitial () == null ? "" : substringNode.getInitial ());
            for ( String string : (List<String>) substringNode.getAny () )
            {
                values.add ( string );
            }
            values.add ( substringNode.getFinal () == null ? "" : substringNode.getFinal () );
            return new FilterAssertion ( substringNode.getAttribute (), Assertion.fromValue ( substringNode.getAssertionType () ), values);
        }
        else if ( node instanceof PresenceNode )
        {
            PresenceNode presenceNode = (PresenceNode)node;
            return new FilterAssertion ( presenceNode.getAttribute (), Assertion.fromValue ( presenceNode.getAssertionType () ),  "");
        }
        else
        {
            throw new FilterParseException ( "not able to handle Node: " + node );
        }
    }
}
