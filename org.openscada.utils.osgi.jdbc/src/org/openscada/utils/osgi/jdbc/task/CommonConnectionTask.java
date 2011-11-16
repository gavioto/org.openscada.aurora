package org.openscada.utils.osgi.jdbc.task;

import java.sql.Connection;

public abstract class CommonConnectionTask<R> implements ConnectionTask<R>
{

    public static class ConnectionContextImplementation extends CommonConnectionContext
    {
        private final Connection connection;

        private ConnectionContextImplementation ( final Connection connection )
        {
            this.connection = connection;
        }

        @Override
        public Connection getConnection ()
        {
            return this.connection;
        }
    }

    @Override
    public R performTask ( final Connection connection ) throws Exception
    {
        return performTask ( new ConnectionContextImplementation ( connection ) );
    }

    protected abstract R performTask ( ConnectionContext connectionContext ) throws Exception;

}
