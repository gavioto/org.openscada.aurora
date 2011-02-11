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

import java.util.concurrent.Future;

import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultFutureHandler<T> implements FutureListener<T>
{

    private final static Logger logger = LoggerFactory.getLogger ( ResultFutureHandler.class );

    private final ResultHandler<T> resultHandler;

    public ResultFutureHandler ( final ResultHandler<T> resultHandler )
    {
        this.resultHandler = resultHandler;
    }

    @Override
    public void complete ( final Future<T> future )
    {
        try
        {
            this.resultHandler.completed ( future.get () );
        }
        catch ( final Throwable e )
        {
            logger.debug ( "Failed to complete", e );
            this.resultHandler.failed ( e );
        }
    }
}