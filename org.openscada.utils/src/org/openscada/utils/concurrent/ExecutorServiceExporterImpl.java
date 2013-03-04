/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.concurrent;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorServiceExporterImpl implements ExecutorServiceExporterMXBean
{

    private final static Logger logger = LoggerFactory.getLogger ( ExecutorServiceExporterImpl.class );

    private final MBeanServer mbs;

    private ObjectName name;

    private final ExecutorService executorService;

    public ExecutorServiceExporterImpl ( final ExecutorService executorService, final String key )
    {
        this.executorService = executorService;
        this.mbs = ManagementFactory.getPlatformMBeanServer ();

        try
        {
            this.name = new ObjectName ( "org.openscada.utils.concurrent", "executorService", key );
            this.mbs.registerMBean ( this, this.name );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to export", e );
        }
    }

    public void dispose ()
    {
        try
        {
            this.mbs.unregisterMBean ( this.name );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to unregister: " + this.name, e );
        }

    }

    @Override
    public Integer getActiveCount ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getActiveCount ();
        }
        return null;
    }

    @Override
    public Long getCompletedTaskCount ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getCompletedTaskCount ();
        }
        return null;
    }

    @Override
    public Integer getCorePoolSize ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getCorePoolSize ();
        }
        return null;
    }

    @Override
    public Integer getLargestPoolSize ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getLargestPoolSize ();
        }
        return null;
    }

    @Override
    public Integer getMaximumPoolSize ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getMaximumPoolSize ();
        }
        return null;
    }

    @Override
    public Integer getPoolSize ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getPoolSize ();
        }
        return null;
    }

    @Override
    public Long getTaskCount ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getTaskCount ();
        }
        return null;
    }

    @Override
    public Integer getQueueSize ()
    {
        if ( this.executorService instanceof ThreadPoolExecutor )
        {
            return ( (ThreadPoolExecutor)this.executorService ).getQueue ().size ();
        }
        return null;
    }

    @Override
    public String getStatusString ()
    {
        return this.executorService.toString ();
    }

}
