/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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
 * Base interface for operations (either sync or async based).
 * 
 * @param <R>
 *            The result type
 * @param <T>
 *            The argument type
 *            <p>
 *            The idea behind operations is that you have:
 *            <ul>
 *            <li>Synchronous operations</li>
 *            <li>Asynchronous operations</li>
 *            <li>Synchronous callers</li>
 *            <li>Asynchronous callers</li>
 *            <li>Callback callers</li>
 *            </ul>
 *            If you wish to:
 *            <ul>
 *            <li>Implement an operation that is synchronous you need to derive
 *            from {@link SyncBasedOperation}</li>
 *            <li>Implement an operation that s asynchronous you need to derive
 *            from {@link AsyncBasedOperation}</li>
 *            <li>Call an operation synchronously see {@link #execute}</li>
 *            <li>Call an operation asynchronously see
 *            {@link #startExecute(Object)}</li>
 *            <li>Call an operation an get notified by callback
 *            {@link #startExecute(OperationResultHandler handler, Object arg0)}</li>
 *            </ul>
 * @author jens
 */
public interface Operation<R, T>
{
    public R execute ( T arg0 ) throws Exception;

    public OperationResult<R> startExecute ( T arg0 );

    public OperationResult<R> startExecute ( OperationResultHandler<R> handler, T arg0 );
}
