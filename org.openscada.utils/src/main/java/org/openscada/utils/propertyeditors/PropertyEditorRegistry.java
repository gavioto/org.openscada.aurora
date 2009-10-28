package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jrose
 * 
 */
public class PropertyEditorRegistry
{

    private ConcurrentMap<String, PropertyEditor> propertyEditors = new ConcurrentHashMap<String, PropertyEditor> ();

    public Map<String, PropertyEditor> getPropertyEditors ()
    {
        return Collections.unmodifiableMap ( propertyEditors );
    }

    /**
     * @param requiredType
     * @param propertyPath
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public PropertyEditor findCustomEditor ( Class requiredType, String propertyPath )
    {
        // first try to find exact match
        String key = requiredType.getCanonicalName () + ":" + propertyPath;
        PropertyEditor pe = propertyEditors.get ( key );
        // 2nd: try to find for class only
        if ( pe == null )
        {
            key = requiredType.getCanonicalName () + ":";
            pe = propertyEditors.get ( key );
        }
        // 3rd: try to get internal
        if ( pe == null )
        {
            pe = PropertyEditorManager.findEditor ( requiredType );
        }
        return pe;
    }

    /**
     * @param requiredType
     * @return
     */
    @SuppressWarnings ( "unchecked" )
    public PropertyEditor findCustomEditor ( Class requiredType )
    {
        return findCustomEditor ( requiredType, "" );
    }

    /**
     * @param requiredType
     * @param propertyEditor
     */
    @SuppressWarnings ( "unchecked" )
    public void registerCustomEditor ( Class requiredType, PropertyEditor propertyEditor )
    {
        registerCustomEditor ( requiredType, "", propertyEditor );
    }

    /**
     * @param requiredType
     * @param propertyPath
     * @param propertyEditor
     */
    @SuppressWarnings ( "unchecked" )
    public void registerCustomEditor ( Class requiredType, String propertyPath, PropertyEditor propertyEditor )
    {
        String key = requiredType.getCanonicalName () + ":" + propertyPath;
        propertyEditors.put ( key, propertyEditor );
    }
}
