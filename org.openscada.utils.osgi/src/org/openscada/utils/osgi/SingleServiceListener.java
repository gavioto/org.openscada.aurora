package org.openscada.utils.osgi;

import org.osgi.framework.ServiceReference;

public interface SingleServiceListener
{
    public void serviceChange ( ServiceReference reference, Object service );
}
