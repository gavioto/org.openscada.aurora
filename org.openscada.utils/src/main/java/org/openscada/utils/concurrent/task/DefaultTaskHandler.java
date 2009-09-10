/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.utils.concurrent.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTaskHandler implements TaskHandler
{

    private final static Logger logger = LoggerFactory.getLogger ( DefaultTaskHandler.class );

    private class HandleImpl implements Handle
    {
        private volatile Long id;

        private final DefaultTaskHandler handler;

        public HandleImpl ( final long id, final DefaultTaskHandler handler )
        {
            this.id = id;
            this.handler = handler;
        }

        public Long getId ()
        {
            return this.id;
        }

        @Override
        protected void finalize () throws Throwable
        {
            this.handler.removeTask ( this.id );
            super.finalize ();
        }

        public void dispose ()
        {
            this.handler.removeTask ( this.id );
            this.id = null;
        }
    }

    private final Map<Long, NotifyFuture<?>> taskMap = new HashMap<Long, NotifyFuture<?>> ();

    private final Random random = new Random ();

    protected long allocateId ( final NotifyFuture<?> task )
    {
        synchronized ( this.taskMap )
        {
            Long value = this.random.nextLong ();
            while ( this.taskMap.containsKey ( value ) )
            {
                value = this.random.nextLong ();
            }
            this.taskMap.put ( value, task );
            logger.debug ( "{} items are in the map", this.taskMap.size () );
            return value;
        }
    }

    public Handle addTask ( final NotifyFuture<?> task )
    {
        return new HandleImpl ( allocateId ( task ), this );
    }

    public boolean cancelTask ( final Long id )
    {
        if ( id == null )
        {
            return false;
        }

        final NotifyFuture<?> task;
        synchronized ( this )
        {
            task = this.taskMap.get ( id );
        }
        if ( task != null )
        {
            task.cancel ( true );
            return true;
        }
        return false;
    }

    public void dispose ()
    {
        synchronized ( this.taskMap )
        {
            for ( final Map.Entry<Long, NotifyFuture<?>> entry : this.taskMap.entrySet () )
            {
                entry.getValue ().cancel ( true );
            }
            this.taskMap.clear ();
        }
    }

    public boolean removeTask ( final Long id )
    {
        if ( id == null )
        {
            return false;
        }

        synchronized ( this.taskMap )
        {
            logger.debug ( "{} items are in the map", this.taskMap.size () );
            return this.taskMap.remove ( id ) != null;
        }
    }

}
