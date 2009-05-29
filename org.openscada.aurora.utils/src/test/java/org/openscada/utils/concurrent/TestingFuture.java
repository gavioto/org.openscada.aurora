package org.openscada.utils.concurrent;

public class TestingFuture<T> extends AbstractFuture<T>
{
    @Override
    protected void setError ( final Throwable error )
    {
        super.setError ( error );
    }

    @Override
    protected void setResult ( final T result )
    {
        super.setResult ( result );
    }
}
