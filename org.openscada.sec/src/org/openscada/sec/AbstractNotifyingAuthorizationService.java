/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public abstract class AbstractNotifyingAuthorizationService implements NotifyingAuthorizationService
{

    private final Set<Listener> listeners = new HashSet<NotifyingAuthorizationService.Listener> ();

    private final ReadLock readLock;

    private final WriteLock writeLock;

    public AbstractNotifyingAuthorizationService ()
    {
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock ();
        this.readLock = lock.readLock ();
        this.writeLock = lock.writeLock ();
    }

    protected void fireChange ( final Executor executor )
    {
        this.readLock.lock ();
        try
        {
            for ( final Listener listener : this.listeners )
            {
                executor.execute ( new Runnable () {
                    @Override
                    public void run ()
                    {
                        listener.serviceChanged ();
                    }
                } );
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public void addListener ( final Listener listener )
    {
        this.writeLock.lock ();
        try
        {
            this.listeners.add ( listener );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void removeListener ( final Listener listener )
    {
        this.writeLock.lock ();
        try
        {
            this.listeners.remove ( listener );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

}
