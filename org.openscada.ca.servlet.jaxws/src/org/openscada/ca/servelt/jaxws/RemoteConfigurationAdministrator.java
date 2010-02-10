package org.openscada.ca.servelt.jaxws;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.jws.WebService;

@WebService
public interface RemoteConfigurationAdministrator
{
    public abstract boolean hasService ();

    public abstract Factory[] getFactories ();

    public abstract Factory[] getCompleteConfiguration ();

    public abstract void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void update ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void create ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

}