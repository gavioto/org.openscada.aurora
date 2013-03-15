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

package org.openscada.sec.authn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.PasswordCallback;
import org.openscada.sec.callback.UserNameCallback;

/**
 * @since 1.1
 * @author Jens Reimann
 */
public class CredentialsRequest
{

    private static final int ORDER_USERNAME = 100;

    private static final int ORDER_PASSWORD = 200;

    private final Object TAG_USERNAME = new Object ();

    private final Object TAG_PASSWORD = new Object ();

    private final Map<Object, Callback> callbackMap = new HashMap<Object, Callback> ();

    private final Locale locale;

    private final ResourceBundle bundle;

    public CredentialsRequest ()
    {
        this ( null );
    }

    public CredentialsRequest ( final Locale locale )
    {
        this.locale = locale == null ? Locale.getDefault () : locale;
        this.bundle = ResourceBundle.getBundle ( "org/openscada/sec/authn/request", this.locale );
    }

    protected String getText ( final String textId, final Locale locale )
    {
        return this.bundle.getString ( textId );
    }

    public void askUsername ()
    {
        if ( !this.callbackMap.containsKey ( this.TAG_USERNAME ) )
        {
            this.callbackMap.put ( this.TAG_USERNAME, new UserNameCallback ( getText ( "username", this.locale ), ORDER_USERNAME ) );
        }
    }

    public void askPassword ()
    {
        if ( !this.callbackMap.containsKey ( this.TAG_PASSWORD ) )
        {
            this.callbackMap.put ( this.TAG_PASSWORD, new PasswordCallback ( getText ( "password", this.locale ), ORDER_PASSWORD ) );
        }
    }

    public Callback[] buildCallbacks ()
    {
        final Callback[] result = this.callbackMap.values ().toArray ( new Callback[this.callbackMap.size ()] );
        Arrays.sort ( result, Callback.ORDER_COMPARATOR );
        return result;
    }

    public Callback getCallback ( final Object tag )
    {
        return this.callbackMap.get ( tag );
    }

    @SuppressWarnings ( "unchecked" )
    public <T> T getTypedCallback ( final Object tag, final Class<T> clazz )
    {
        final Callback cb = getCallback ( tag );
        if ( cb != null && !clazz.isInstance ( cb ) )
        {
            return null;
        }
        else
        {
            return (T)cb;
        }
    }

    public UserNameCallback getUserNameCallback ()
    {
        return getTypedCallback ( this.TAG_USERNAME, UserNameCallback.class );
    }

    public PasswordCallback getPasswordCallback ()
    {
        return getTypedCallback ( this.TAG_PASSWORD, PasswordCallback.class );
    }

    public String getUserName ()
    {
        final UserNameCallback cb = getUserNameCallback ();
        if ( cb == null )
        {
            return null;
        }
        else
        {
            return cb.getValue ();
        }
    }

    public String getPassword ()
    {
        final PasswordCallback cb = getPasswordCallback ();
        if ( cb == null )
        {
            return null;
        }
        else
        {
            return cb.getPassword ();
        }
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "[CredentialsRequest - " );
        for ( final Map.Entry<Object, Callback> entry : this.callbackMap.entrySet () )
        {
            sb.append ( "\n" );
            sb.append ( "[" );
            sb.append ( entry.getKey () );
            sb.append ( " = " );
            sb.append ( entry.getValue () );
            sb.append ( "]" );
        }
        sb.append ( "]" );

        return sb.toString ();
    }
}
