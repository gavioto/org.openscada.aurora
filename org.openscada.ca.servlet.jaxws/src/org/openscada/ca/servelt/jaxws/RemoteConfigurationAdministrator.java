package org.openscada.ca.servelt.jaxws;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.jws.WebService;

@WebService
public interface RemoteConfigurationAdministrator
{
    public abstract boolean hasService ();

    /**
     * Get factory information without content
     * @return the factories without a content
     */
    public abstract Factory[] getFactories ();

    public abstract Factory[] getCompleteConfiguration ();

    public abstract Factory getConfiguration ( String factoryId );

    public abstract void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void update ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void create ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

}