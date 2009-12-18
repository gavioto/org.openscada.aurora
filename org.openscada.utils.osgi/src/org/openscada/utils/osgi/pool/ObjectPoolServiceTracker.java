package org.openscada.utils.osgi.pool;

import java.util.Dictionary;

public class ObjectPoolServiceTracker extends AbstractObjectPoolServiceTracker
{
    private final ObjectPoolListener clientListener;

    public ObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId, final ObjectPoolListener listener )
    {
        super ( poolTracker, serviceId );
        this.clientListener = listener;
    }

    @Override
    protected void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceAdded ( service, properties );
    }

    private void fireServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceAdded ( service, properties );
    }

    @Override
    protected void handleServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceModified ( service, properties );
    }

    private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceModified ( service, properties );
    }

    @Override
    protected void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceRemoved ( service, properties );
    }

    private void fireServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceRemoved ( service, properties );
    }

}
