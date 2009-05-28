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