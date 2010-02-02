package org.openscada.utils.filter.test;

import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterParser;
import org.openscada.utils.filter.bean.BeanMatcher;

public class BeanMatcherTest
{

    private TestBean1 bean1;

    private TestBean1 bean2;

    @Before
    public void setup ()
    {
        this.bean1 = new TestBean1 ();
        this.bean1.setStringValue1 ( "Hello World" );
        this.bean1.setDateValue1 ( new GregorianCalendar ( 2000, 1, 1, 0, 0, 0 ).getTime () );

        this.bean2 = new TestBean1 ();
        this.bean2.setStringValue1 ( "Hello World" );
        this.bean2.setDoubleValue1 ( 1.234 );
        this.bean2.setIntegerValue1 ( 42 );
        this.bean2.setDateValue1 ( new GregorianCalendar ( 2000, 1, 1, 0, 0, 0 ).getTime () );
        this.bean2.setTestEnum1 ( TestEnum.B );
    }

    @Test
    public void test1 () throws Exception
    {
        testFilter ( "(stringValue1=Hello World)", this.bean1, true );
        testFilter ( "(integerValue1=*)", this.bean1, false );
        testFilter ( "(booleanValue1=false)", this.bean1, true );
        testFilter ( "(booleanValue1=true)", this.bean1, false );
    }

    @Test
    public void test2 () throws Exception
    {
        testFilter ( "(integerValue1=42)", this.bean2, true );
        testFilter ( "(doubleValue1=1.234)", this.bean2, true );
        testFilter ( "(testEnum1=B)", this.bean2, true );
        testFilter ( "(integerValue1=43)", this.bean2, false );
        testFilter ( "(doubleValue1=1.235)", this.bean2, false );
        testFilter ( "(testEnum1=A)", this.bean2, false );
    }

    @Test
    public void test3 () throws Exception
    {
        testFilter ( "(&(integerValue1=42)(doubleValue1=1.234))", this.bean2, true );
        testFilter ( "(|(integerValue1=43)(doubleValue1=1.234))", this.bean2, true );

        testFilter ( "(&(integerValue1=43)(doubleValue1=1.234))", this.bean2, false );
        testFilter ( "(|(integerValue1=43)(doubleValue1=1.235))", this.bean2, false );
    }

    @Test
    public void test4 () throws Exception
    {
        testFilter ( "(!(integerValue1=42))", this.bean2, false );
        testFilter ( "(!(integerValue1=43))", this.bean2, true );
    }

    @Test
    public void test5 () throws Exception
    {
        testFilter ( "(integerValue1>41)", this.bean2, true );
        testFilter ( "(integerValue1>=41)", this.bean2, true );
        testFilter ( "(integerValue1<=41)", this.bean2, false );
        testFilter ( "(integerValue1<41)", this.bean2, false );

        testFilter ( "(integerValue1>42)", this.bean2, false );
        testFilter ( "(integerValue1>=42)", this.bean2, true );
        testFilter ( "(integerValue1<=42)", this.bean2, true );
        testFilter ( "(integerValue1<42)", this.bean2, false );

        testFilter ( "(integerValue1>43)", this.bean2, false );
        testFilter ( "(integerValue1>=43)", this.bean2, false );
        testFilter ( "(integerValue1<=43)", this.bean2, true );
        testFilter ( "(integerValue1<43)", this.bean2, true );
    }

    @Test
    public void test6 () throws Exception
    {
        testFilter ( "(stringValue1=*Hello*)", this.bean2, true );
        testFilter ( "(stringValue1=*ll*)", this.bean2, true );
        testFilter ( "(stringValue1=Hell*)", this.bean2, true );
        testFilter ( "(stringValue1=*orld)", this.bean2, true );

        testFilter ( "(stringValue1=*XHello*)", this.bean2, false );
        testFilter ( "(stringValue1=*Xll*)", this.bean2, false );
        testFilter ( "(stringValue1=XHell*)", this.bean2, false );
        testFilter ( "(stringValue1=*Xorld)", this.bean2, false );
    }

    @Test
    public void test7 () throws Exception
    {
        testFilter ( "(stringValue1~=.*)", this.bean2, true );
        testFilter ( "(stringValue1~=Hello.*)", this.bean2, true );
        testFilter ( "(stringValue1~=.*llo.*)", this.bean2, true );
        testFilter ( "(stringValue1~=.*llo.*)", this.bean2, true );
        testFilter ( "(stringValue1~=ll)", this.bean2, true );
        testFilter ( "(stringValue1~=^ll$)", this.bean2, false );
    }

    private void testFilter ( final String filterString, final Object bean, final boolean expected ) throws Exception
    {
        final Filter filter = new FilterParser ( filterString ).getFilter ();
        final boolean result = BeanMatcher.matches ( filter, bean, false, null );
        Assert.assertEquals ( "Must be equal", expected, result );
    }
}
