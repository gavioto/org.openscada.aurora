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

package org.openscada.sec.callback;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.openscada.utils.concurrent.NotifyFuture;

/**
 * Translates java {@link CallbackHandler} calls to openSCADA
 * {@link org.openscada.sec.callback.CallbackHandler} calls.
 * 
 * @since 1.1
 */
public class CallbackHandlerTranslator implements CallbackHandler
{

    private interface CallbackAdapter
    {
        public void complete ();

        public org.openscada.sec.callback.Callback getAdapter ();
    }

    private static class UserNameCallbackAdapter implements CallbackAdapter
    {
        private final NameCallback callback;

        private final UserNameCallback adapter;

        public UserNameCallbackAdapter ( final NameCallback callback, final int order )
        {
            this.callback = callback;
            this.adapter = new UserNameCallback ( callback.getPrompt (), order );
        }

        @Override
        public void complete ()
        {
            this.callback.setName ( this.adapter.getValue () );
        }

        @Override
        public org.openscada.sec.callback.Callback getAdapter ()
        {
            return this.adapter;
        }
    }

    private static class PasswordCallbackAdapter implements CallbackAdapter
    {
        private final PasswordCallback callback;

        private final org.openscada.sec.callback.PasswordCallback adapter;

        public PasswordCallbackAdapter ( final PasswordCallback callback, final int order )
        {
            this.callback = callback;
            this.adapter = new org.openscada.sec.callback.PasswordCallback ( callback.getPrompt (), order );
        }

        @Override
        public void complete ()
        {
            this.callback.setPassword ( this.adapter.getPlainPassword ().toCharArray () );
        }

        @Override
        public org.openscada.sec.callback.Callback getAdapter ()
        {
            return this.adapter;
        }
    }

    private final org.openscada.sec.callback.CallbackHandler callbackHandler;

    public CallbackHandlerTranslator ( final org.openscada.sec.callback.CallbackHandler callbackHandler )
    {
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void handle ( final Callback[] callbacks ) throws IOException, UnsupportedCallbackException
    {
        final org.openscada.sec.callback.Callback[] cbs = new org.openscada.sec.callback.Callback[callbacks.length];

        final CallbackAdapter[] adapters = new CallbackAdapter[callbacks.length];

        for ( int i = 0; i < callbacks.length; i++ )
        {
            final Callback c = callbacks[i];
            if ( c instanceof NameCallback )
            {
                adapters[i] = new UserNameCallbackAdapter ( (NameCallback)c, i );
            }
            else if ( c instanceof PasswordCallback )
            {
                adapters[i] = new PasswordCallbackAdapter ( (PasswordCallback)c, i );
            }
            else
            {
                throw new UnsupportedCallbackException ( c );
            }

            cbs[i] = adapters[i].getAdapter ();
        }

        final NotifyFuture<org.openscada.sec.callback.Callback[]> future = Callbacks.callback ( this.callbackHandler, cbs );
        try
        {
            future.get ();
        }
        catch ( final Exception e )
        {
            throw new IOException ( e );
        }

        for ( final CallbackAdapter adapter : adapters )
        {
            adapter.complete ();
        }
    }

}
