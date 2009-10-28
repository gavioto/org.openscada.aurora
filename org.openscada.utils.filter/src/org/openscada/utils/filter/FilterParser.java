package org.openscada.utils.filter;

import java.io.IOException;
import java.text.ParseException;

import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.FilterParserImpl;
import org.openscada.utils.filter.internal.FilterVisitorImpl;

/**
 * @author jrose
 *
 */
public class FilterParser
{

    private final org.apache.directory.shared.ldap.filter.FilterParser filterParser;

    private final Filter filter;

    public FilterParser ( String filter ) throws FilterParseException
    {
        filterParser = new FilterParserImpl ();

        ExprNode exprNode = null;
        try
        {
            exprNode = filterParser.parse ( filter );
        }
        catch ( IOException e )
        {
            throw new FilterParseException ( e );
        }
        catch ( ParseException e )
        {
            throw new FilterParseException ( e );
        }
        FilterVisitorImpl visitor = new FilterVisitorImpl ();
        if ( exprNode != null )
        {
            exprNode.accept ( visitor );
            this.filter = visitor.getFilter ();
        }
        else
        {
            this.filter = new FilterExpression ();
        }
    }

    public Filter getFilter ()
    {
        return filter;
    }
}
