/**
 * 
 */
package org.openscada.ca.common;

import org.openscada.ca.Configuration;
import org.openscada.utils.concurrent.AbstractFuture;

public class ConfigurationFuture extends AbstractFuture<Configuration>
{
    @Override
    public void setError ( final Throwable error )
    {
        super.setError ( error );
    }

    @Override
    public void setResult ( final Configuration result )
    {
        super.setResult ( result );
    }
}