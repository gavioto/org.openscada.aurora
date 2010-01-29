package org.openscada.utils.filter.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.propertyeditors.PropertyEditorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanMatcher
{

    private final static PropertyEditorRegistry defaultRegistry = new PropertyEditorRegistry ( true );

    private final static Logger logger = LoggerFactory.getLogger ( BeanMatcher.class );

    public static boolean matches ( final Filter filter, final Object object, final boolean ifEmpty, final PropertyEditorRegistry registry ) throws Exception
    {
        if ( filter == null )
        {
            return ifEmpty;
        }

        if ( filter.isEmpty () )
        {
            return ifEmpty;
        }
        else if ( filter.isAssertion () )
        {
            return matchAssertion ( (FilterAssertion)filter, object, ifEmpty, registry );
        }
        else if ( filter.isExpression () )
        {
            return matchExpression ( (FilterExpression)filter, object, ifEmpty, registry );
        }
        else
        {
            logger.warn ( "Filter no none of: empty, assertion or expression" );
            return ifEmpty;
        }
    }

    private static boolean matchExpression ( final FilterExpression filterExpression, final Object object, final boolean ifEmpty, final PropertyEditorRegistry registry ) throws Exception
    {
        switch ( filterExpression.getOperator () )
        {
        case AND:
            for ( final Filter filter : filterExpression.getFilterSet () )
            {
                if ( !matches ( filter, object, ifEmpty, registry ) )
                {
                    return false;
                }
            }
            return true;
        case OR:
            for ( final Filter filter : filterExpression.getFilterSet () )
            {
                if ( matches ( filter, object, ifEmpty, registry ) )
                {
                    return true;
                }
            }
            return false;
        case NOT:
            if ( !filterExpression.getFilterSet ().isEmpty () )
            {
                return !matches ( filterExpression.getFilterSet ().get ( 0 ), object, ifEmpty, registry );
            }
            break;
        }
        return ifEmpty;
    }

    private static boolean matchAssertion ( final FilterAssertion filterAssertion, final Object object, final boolean ifEmpty, final PropertyEditorRegistry registry ) throws Exception
    {
        final PropertyDescriptor pd = getProperyDescriptor ( object.getClass (), filterAssertion.getAttribute () );
        if ( pd == null )
        {
            return false;
        }
        final Method m = pd.getReadMethod ();
        if ( m == null )
        {
            return false;
        }

        switch ( filterAssertion.getAssertion () )
        {
        case EQUALITY:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            final int i = compare ( value, (String)filterAssertion.getValue (), registry );
            return i == 0;
        }
        case GREATERTHAN:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            final int i = compare ( value, (String)filterAssertion.getValue (), registry );
            return i > 0;
        }
        case LESSTHAN:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            final int i = compare ( value, (String)filterAssertion.getValue (), registry );
            return i < 0;

        }
        case GREATEREQ:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            final int i = compare ( value, (String)filterAssertion.getValue (), registry );
            return i >= 0;
        }
        case LESSEQ:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            final int i = compare ( value, (String)filterAssertion.getValue (), registry );
            return i <= 0;
        }
        case SUBSTRING:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            else
            {
                return matchSubString ( value.toString (), (Collection<String>)filterAssertion.getValue () );
            }
        }
        case PRESENCE:
        {
            final Object value = getValue ( m, object );
            return value != null;
        }
        case APPROXIMATE:
        {
            final Object value = getValue ( m, object );
            if ( value == null )
            {
                return false;
            }
            else
            {
                return matchRegexString ( value.toString (), (String)filterAssertion.getValue () );
            }
        }
        }
        return false;
    }

    private static boolean matchRegexString ( final String string, final String value )
    {
        final Pattern p = Pattern.compile ( value );
        final Matcher m = p.matcher ( string );
        return m.find ();
    }

    @SuppressWarnings ( "unchecked" )
    private static int compare ( final Object value, final String stringValue, final PropertyEditorRegistry registry )
    {
        final Object cvtValue = convert ( stringValue, value.getClass (), registry );

        if ( value instanceof Comparable<?> )
        {
            return ( (Comparable)value ).compareTo ( cvtValue );
        }

        // FIXME: add comparator registry

        // try string compare last
        return value.toString ().compareTo ( cvtValue.toString () );
    }

    @SuppressWarnings ( "unchecked" )
    private static Object convert ( final String stringValue, final Class<? extends Object> clazz, final PropertyEditorRegistry registry )
    {
        if ( clazz == String.class )
        {
            return stringValue;
        }

        PropertyEditor editor = null;

        if ( registry != null )
        {
            editor = registry.findCustomEditor ( clazz );
        }
        else
        {
            editor = defaultRegistry.findCustomEditor ( clazz );
        }

        if ( editor == null )
        {
            try
            {
                final Class propertyEditorClass = Class.forName ( clazz.getName () + "Editor" );
                if ( propertyEditorClass != null )
                {
                    editor = (PropertyEditor)propertyEditorClass.newInstance ();
                }
            }
            catch ( final Exception e )
            {
                throw new IllegalArgumentException ( String.format ( "Failed to convert '%s' to %s", stringValue, clazz ), e );
            }
        }

        if ( editor == null )
        {
            throw new IllegalArgumentException ( String.format ( "Unable to convert '%s' to %s", stringValue, clazz ) );
        }

        editor.setAsText ( stringValue );
        return editor.getValue ();
    }

    private static boolean matchSubString ( final String string, final Collection<String> collection )
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "^" );

        for ( final String token : collection )
        {
            if ( token.length () == 0 )
            {
                sb.append ( ".*" );
            }
            else
            {
                sb.append ( Pattern.quote ( token ) );
            }
        }

        sb.append ( "$" );

        return matchRegexString ( string, sb.toString () );
    }

    protected static Object getValue ( final Method readMethod, final Object object ) throws Exception
    {
        return readMethod.invoke ( object );
    }

    protected static PropertyDescriptor getProperyDescriptor ( final Class<?> clazz, final String name ) throws Exception
    {
        final BeanInfo bi = Introspector.getBeanInfo ( clazz );
        for ( final PropertyDescriptor pd : bi.getPropertyDescriptors () )
        {
            if ( pd.getName ().equals ( name ) )
            {
                return pd;
            }
        }
        return null;
    }
}
