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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Quantizer
{

    private final static Logger logger = LoggerFactory.getLogger ( Quantizer.class );

    /**
     * the millisTime in milliseconds
     */
    private final long millisTime;

    private final int count;

    private final long time;

    private final TimeUnit unit;

    public Quantizer ( final long time, final TimeUnit unit )
    {
        this ( time, unit, Integer.MAX_VALUE );
    }

    public Quantizer ( final long time, final TimeUnit unit, final int count )
    {
        this.millisTime = TimeUnit.MILLISECONDS.convert ( time, unit );
        this.time = time;
        this.unit = unit;
        this.count = count;
    }

    public Date getValidStart ( final Date timestamp, final Date now )
    {
        final Date start = getStart ( timestamp );

        final Date end = new Date ( now.getTime () - this.millisTime * this.count );

        if ( start.before ( end ) )
        {
            return null;
        }
        else
        {
            return start;
        }
    }

    public Date getValidStart ( final Date timestamp )
    {
        return getValidStart ( timestamp, new Date () );
    }

    public Date getStart ( final Date timestamp )
    {
        final long tix = timestamp.getTime () / this.millisTime * this.millisTime;
        logger.trace ( "Timestamp {} -> {}", timestamp.getTime (), tix );
        return new Date ( tix );
    }

    public Date getNext ( final Date start )
    {
        if ( start == null )
        {
            return null;
        }
        else
        {
            return new Date ( start.getTime () + this.millisTime );
        }
    }

    public Date getPrevious ( final Date start )
    {
        if ( start == null )
        {
            return null;
        }
        else
        {
            return new Date ( start.getTime () - this.millisTime );
        }
    }

    public Date getEnd ( final Date timestamp )
    {
        return getNext ( getStart ( timestamp ) );
    }

    @Override
    public String toString ()
    {
        return String.format ( "[%s|%s|%s]", this.time, this.unit, this.count );
    }
}
