package org.openscada.utils.osgi.pool;

import java.util.Dictionary;

public interface ObjectPoolListener
{
    public void serviceAdded ( Object service, Dictionary<?, ?> properties );

    public void serviceRemoved ( Object service, Dictionary<?, ?> properties );

    public void serviceModified ( Object service, Dictionary<?, ?> properties );
}
