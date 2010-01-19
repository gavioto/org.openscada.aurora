package org.openscada.utils.osgi.pool;

import java.util.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolServiceTracker extends AbstractObjectPoolServiceTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolServiceTracker.class );

    private final ObjectPoolListener clientListener;

    public ObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId, final ObjectPoolListener listener )
    {
        super ( poolTracker, serviceId );
        this.clientListener = listener;
        logger.debug ( "new pool service tracker for {}", serviceId );
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
