/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006, 2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.exec;

class OperationHandlerTestImpl<R> implements OperationResultHandler<R>
{
    R _result = null;

    Exception _exception = null;

    boolean _failure = false;

    boolean _success = false;

    @Override
    public void failure ( final Exception e )
    {
        this._result = null;
        this._exception = e;
        this._success = false;
        this._failure = true;
    }

    @Override
    public void success ( final R result )
    {
        this._result = result;
        this._exception = null;
        this._success = true;
        this._failure = false;
    }

    public Exception getException ()
    {
        return this._exception;
    }

    public boolean isFailure ()
    {
        return this._failure;
    }

    public R getResult ()
    {
        return this._result;
    }

    public boolean isSuccess ()
    {
        return this._success;
    }

}