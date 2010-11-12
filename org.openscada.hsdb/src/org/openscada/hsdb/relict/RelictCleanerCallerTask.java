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

package org.openscada.hsdb.relict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to periodically delete old data.
 * @author Ludwig Straub
 */
public class RelictCleanerCallerTask implements Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( RelictCleanerCallerTask.class );

    /** Object of which old data has to be deleted. */
    private final RelictCleaner relictCleaner;

    /**
     * Constructor.
     * @param relictCleaner Object of which old data has to be deleted
     */
    public RelictCleanerCallerTask ( final RelictCleaner relictCleaner )
    {
        this.relictCleaner = relictCleaner;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            this.relictCleaner.cleanupRelicts ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
