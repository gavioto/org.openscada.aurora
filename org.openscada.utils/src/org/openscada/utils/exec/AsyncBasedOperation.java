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

package org.openscada.utils.exec;

/**
 * Implements an operation that is based on a asynchronous operation.
 * 
 * @author jens
 * @param <R>
 *            The result type
 * @param <T>
 *            The argument type
 */

public abstract class AsyncBasedOperation<R, T> implements Operation<R, T>
{

    public R execute ( final T arg0 ) throws Exception
    {
        final OperationResult<R> result = startExecute ( arg0 );

        result.complete ();

        if ( result.isSuccess () )
        {
            return result.get ();
        }
        else
        {
            throw result.getException ();
        }
    }

    protected abstract void startExecute ( OperationResult<R> or, T arg0 );

    public OperationResult<R> startExecute ( final T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ();

        startExecute ( or, arg0 );

        return or;
    }

    public OperationResult<R> startExecute ( final OperationResultHandler<R> handler, final T arg0 )
    {
        final OperationResult<R> or = new OperationResult<R> ( handler );

        startExecute ( or, arg0 );

        return or;
    }

}
