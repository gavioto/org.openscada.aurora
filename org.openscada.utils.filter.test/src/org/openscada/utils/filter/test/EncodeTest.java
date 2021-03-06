package org.openscada.utils.filter.test;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.Operator;

public class EncodeTest
{
    @Test
    public void test1 ()
    {
        checkEncode ( "(value=*)" );
        checkEncode ( "(value=*a)" );
        checkEncode ( "(value=*a*)" );
        checkEncode ( "(&(value=*))" );
        checkEncode ( "(&(value=*a))" );
        checkEncode ( "(&(value=*a*))" );
    }

    @Test
    public void test2 ()
    {
        checkEncode ( "(value=A*B)", new FilterAssertion ( "value", Assertion.SUBSTRING, Arrays.asList ( "A", "B" ) ) );
        checkEncode ( "(value=*A*)", new FilterAssertion ( "value", Assertion.SUBSTRING, Arrays.asList ( "", "A", "" ) ) );

        checkEncode ( "(&(value=A*B))", and ( new FilterAssertion ( "value", Assertion.SUBSTRING, Arrays.asList ( "A", "B" ) ) ) );
        checkEncode ( "(&(value=*A*))", and ( new FilterAssertion ( "value", Assertion.SUBSTRING, Arrays.asList ( "", "A", "" ) ) ) );
    }

    private Filter and ( final FilterAssertion filterAssertion )
    {
        final FilterExpression exp = new FilterExpression ();
        exp.setOperator ( Operator.AND );
        exp.getFilterSet ().add ( filterAssertion );
        return exp;
    }

    private void checkEncode ( final String string, final Filter filterAssertion )
    {
        Assert.assertEquals ( string, filterAssertion.toString () );
    }

    private void checkEncode ( final String string )
    {
        Assert.assertEquals ( string, new FilterParser ( string ).getFilter ().toString () );
    }
}
