/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec;

import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.utils.lang.Immutable;

/**
 * @since 1.1
 */
@Immutable
public class AuthorizationReply
{
    private final AuthorizationResult result;

    private final AuthorizationContext context;

    public AuthorizationReply ( final AuthorizationResult result, final AuthorizationContext context )
    {
        if ( result == null )
        {
            throw new NullPointerException ( "'result' must not be null" );
        }
        if ( context == null )
        {
            throw new NullPointerException ( "'context' must not be null" );
        }

        this.result = result;
        this.context = context;
    }

    public boolean isGranted ()
    {
        return this.result.isGranted ();
    }

    public AuthorizationResult getResult ()
    {
        return this.result;
    }

    public AuthorizationContext getContext ()
    {
        return this.context;
    }

    public UserInformation getUserInformation ()
    {
        return this.context.getRequest ().getUserInformation ();
    }

    @Override
    public String toString ()
    {
        return String.format ( "[AuthorizationReply - result: %s, request: %s]", this.result, this.context.getRequest () );
    }

    public static AuthorizationReply createGranted ( final AuthorizationContext context )
    {
        return new AuthorizationReply ( AuthorizationResult.GRANTED, context );
    }

    public static AuthorizationReply create ( final AuthorizationResult result, final AuthorizationContext context )
    {
        return new AuthorizationReply ( result, context );
    }

}
