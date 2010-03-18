/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

public class ResultFutureHandler<T> implements FutureListener<T>
{
    private final ResultHandler<T> resultHandler;

    public ResultFutureHandler ( final ResultHandler<T> resultHandler )
    {
        this.resultHandler = resultHandler;
    }

    public void complete ( final Future<T> future )
    {
        try
        {
            this.resultHandler.completed ( future.get () );
        }
        catch ( final Throwable e )
        {
            this.resultHandler.failed ( e );
        }
    }
}