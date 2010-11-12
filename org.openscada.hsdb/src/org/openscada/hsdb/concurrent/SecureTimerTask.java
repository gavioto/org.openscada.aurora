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

package org.openscada.hsdb.concurrent;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to call a runnable within a timer task.
 * @author Ludwig Straub
 */
public class SecureTimerTask extends TimerTask
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( SecureTimerTask.class );

    /** Runnable that has to be executed. */
    private final Runnable runnable;

    /**
     * Constructor.
     * @param runnable runnable that has to be executed
     */
    public SecureTimerTask ( final Runnable runnable )
    {
        this.runnable = runnable;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            this.runnable.run ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
