/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import java.util.concurrent.Future;

public abstract class TransformResultFuture<From, To> extends AbstractFuture<To>
{

    private final NotifyFuture<From> fromFuture;

    public TransformResultFuture ( final NotifyFuture<From> fromFuture )
    {
        this.fromFuture = fromFuture;
        this.fromFuture.addListener ( new FutureListener<From> () {

            @Override
            public void complete ( final Future<From> future )
            {
                process ( future );
            }
        } );
    }

    private void process ( final Future<From> future )
    {
        try
        {
            setResult ( transform ( future.get () ) );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    protected abstract To transform ( From from ) throws Exception;

    @Override
    public boolean cancel ( final boolean mayInterruptIfRunning )
    {
        this.fromFuture.cancel ( mayInterruptIfRunning );
        return super.cancel ( mayInterruptIfRunning );
    }

}
