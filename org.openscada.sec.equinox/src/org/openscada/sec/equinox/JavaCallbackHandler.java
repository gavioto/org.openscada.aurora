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

package org.openscada.sec.equinox;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.TextInputCallback;

import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.sec.callback.PasswordCallback;
import org.openscada.sec.callback.TextCallback;
import org.openscada.sec.callback.UserNameCallback;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;

public class JavaCallbackHandler implements CallbackHandler
{

    private final javax.security.auth.callback.CallbackHandler callbackHandler;

    public JavaCallbackHandler ( final javax.security.auth.callback.CallbackHandler callbackHandler )
    {
        this.callbackHandler = callbackHandler;
    }

    @Override
    public NotifyFuture<Callback[]> performCallback ( final Callback[] callbacks )
    {
        final List<javax.security.auth.callback.Callback> javaCallbacks = new LinkedList<javax.security.auth.callback.Callback> ();

        final Map<javax.security.auth.callback.Callback, Callback> cbMap = new HashMap<javax.security.auth.callback.Callback, Callback> ();

        for ( final Callback cb : callbacks )
        {
            final javax.security.auth.callback.Callback jcb = convert ( cb );
            if ( jcb != null )
            {
                cbMap.put ( jcb, cb );
            }
        }

        try
        {
            this.callbackHandler.handle ( javaCallbacks.toArray ( new javax.security.auth.callback.Callback[javaCallbacks.size ()] ) );
            for ( final javax.security.auth.callback.Callback jcb : javaCallbacks )
            {
                final Callback cb = cbMap.get ( jcb );
                if ( cb == null )
                {
                    continue;
                }

                fillResultFromCallback ( cb, jcb );
            }
            return new InstantFuture<Callback[]> ( callbacks );
        }
        catch ( final Exception e )
        {
            for ( final Callback cb : callbacks )
            {
                cb.cancel ();
            }
            return new InstantFuture<Callback[]> ( callbacks );
        }
    }

    private void fillResultFromCallback ( final Callback cb, final javax.security.auth.callback.Callback jcb )
    {
        if ( cb instanceof TextCallback && jcb instanceof TextInputCallback )
        {
            ( (TextCallback)cb ).setValue ( ( (TextInputCallback)jcb ).getText () );
        }
        else if ( cb instanceof UserNameCallback && jcb instanceof NameCallback )
        {
            ( (UserNameCallback)cb ).setValue ( ( (NameCallback)jcb ).getName () );
        }
        else if ( cb instanceof PasswordCallback && jcb instanceof javax.security.auth.callback.PasswordCallback )
        {
            ( (PasswordCallback)cb ).setPassword ( String.valueOf ( ( (javax.security.auth.callback.PasswordCallback)jcb ).getPassword () ) );
        }
        else
        {
            cb.cancel ();
        }
    }

    private javax.security.auth.callback.Callback convert ( final Callback cb )
    {
        if ( cb instanceof UserNameCallback )
        {
            return new NameCallback ( ( (UserNameCallback)cb ).getLabel () );
        }
        else if ( cb instanceof TextCallback )
        {
            return new TextInputCallback ( ( (TextCallback)cb ).getLabel () );
        }
        if ( cb instanceof PasswordCallback )
        {
            return new javax.security.auth.callback.PasswordCallback ( ( (PasswordCallback)cb ).getLabel (), false );
        }
        return null;
    }
}
