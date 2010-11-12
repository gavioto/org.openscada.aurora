/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.concurrent.task;

import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A task handler which aids in creating task IDs for Future tasks
 * <p>
 * The task handler will hold a unique 64bit integer id for each registered task.
 * The task handle is the reference to this id. When it is finalized the ID will be
 * handed back to the pool and can be reassigned. This finalization must be transparent
 * to the user.
 * </p>
 * @author Jens Reimann
 *
 */
public interface TaskHandler
{
    /**
     * A task handle which will hold a reference to the assigned task id 
     * @author Jens Reimann
     *
     */
    public interface Handle
    {
        /**
         * Get the Id unless the instance is disposed.
         * @return The task id or <code>null</code> if the instance is already disposed.
         */
        public Long getId ();

        /**
         * Will deallocate the ID immediately.
         * Calling {@link #dispose()} on an already disposed instance has no effect.
         */
        public void dispose ();
    }

    public Handle addTask ( NotifyFuture<?> future );

    public boolean removeTask ( Long id );

    public boolean cancelTask ( Long id );

    /**
     * Dispose and cancel all tasks
     */
    public void dispose ();
}
