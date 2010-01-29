package org.openscada.utils.filter.test;

import junit.framework.Assert;

import org.junit.Test;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterParseException;
import org.openscada.utils.filter.FilterParser;

public class FilterParserTest
{

    private void checkCorrect ( final String s ) throws Exception
    {
        final Filter f = new FilterParser ( s ).getFilter ();
        Assert.assertEquals ( s, f.toString () );
    }

    private void checkFail ( final String s ) throws Exception
    {
        try
        {
            new FilterParser ( s ).getFilter ();
            Assert.fail ( s + " should give a parse error" );
        }
        catch ( final FilterParseException e )
        {
        }
    }

    @Test
    public void testParse () throws Exception
    {
        checkCorrect ( "(field=value)" );
        checkCorrect ( "(&(field=value))" );
        checkCorrect ( "(!(field=value))" );
        checkCorrect ( "(|(field=value))" );
        checkCorrect ( "(|(field=value)(foo=bar))" );
        checkCorrect ( "(&(field=value)(!(foo=bar)))" );
        checkCorrect ( "(field>=value)" );
        checkCorrect ( "(field<=value)" );
        checkCorrect ( "(field=*value)" );
        checkCorrect ( "(field=*value*)" );
        checkCorrect ( "(field=*va*ue*)" );
        checkCorrect ( "(field=value*)" );
        checkCorrect ( "(field~=value)" );
        checkCorrect ( "(field=*)" );
        checkCorrect ( "(field.id=value)" );
        checkCorrect ( "(field.id.bar=value)" );
        checkCorrect ( "(field=value\\2a)" );
        
        checkFail ( "()" );
        checkFail ( "(&)" );
        checkFail ( "(" );
        checkFail ( ")" );
        checkFail ( "&" );
        checkFail ( "(!field=value)" );
        checkFail ( "-" );
        checkFail ( "xxx" );
        checkFail ( "(&field=value)" );
    }
}
