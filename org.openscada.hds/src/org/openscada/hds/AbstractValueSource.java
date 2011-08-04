/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
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
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hds;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractValueSource implements ValueSource
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractValueSource.class );

    private final Set<DataStoreListener> listeners = new CopyOnWriteArraySet<DataStoreListener> ();

    @Override
    public void addListener ( final DataStoreListener listener )
    {
        this.listeners.add ( listener );
    }

    @Override
    public void removeListener ( final DataStoreListener listener )
    {
        this.listeners.remove ( listener );
    }

    protected void notifyChange ( final Date start, final Date end )
    {
        logger.debug ( "Notify change - start: {}, end: {}", start, end );
        for ( final DataStoreListener listener : this.listeners )
        {
            try
            {
                listener.storeChanged ( start, end );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to handler listener", e );
            }
        }
    }
}
