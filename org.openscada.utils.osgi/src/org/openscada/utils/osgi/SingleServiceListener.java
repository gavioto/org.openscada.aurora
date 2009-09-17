package org.openscada.utils.osgi;

import org.osgi.framework.ServiceReference;

public interface SingleServiceListener
{
    /**
     * The method is called when the service instance has changed
     * @param reference the new service reference or <code>null</code> if no matching service is availabe
     * @param service the new service instance or <code>null</code> if no matching service is availabe
     */
    public void serviceChange ( ServiceReference reference, Object service );
}
