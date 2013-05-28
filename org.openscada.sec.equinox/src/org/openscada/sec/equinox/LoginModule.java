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

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.UserInformation;
import org.openscada.sec.UserInformationPrincipal;

public class LoginModule implements javax.security.auth.spi.LoginModule
{
    private CallbackHandler callbackHandler;

    private boolean loggedIn;

    private Subject subject;

    private UserInformation userInformation;

    public LoginModule ()
    {
    }

    @SuppressWarnings ( "rawtypes" )
    @Override
    public void initialize ( final Subject subject, final CallbackHandler callbackHandler, final Map sharedState, final Map options )
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public boolean login () throws LoginException
    {
        this.userInformation = null;

        final AuthenticationImplementation auth = Activator.getInstance ().getAuthentication ();

        try
        {
            this.userInformation = auth.authenticate ( new JavaCallbackHandler ( this.callbackHandler ) ).get ();
        }
        catch ( final Exception e )
        {
            final LoginException loginException = new LoginException ();
            loginException.initCause ( e );
            throw loginException;
        }

        this.loggedIn = this.userInformation != null;

        return this.loggedIn;
    }

    @Override
    public boolean commit () throws LoginException
    {
        this.subject.getPrincipals ().add ( new UserInformationPrincipal ( this.userInformation ) );
        this.subject.getPublicCredentials ().add ( this.userInformation );
        return this.loggedIn;
    }

    @Override
    public boolean abort () throws LoginException
    {
        this.loggedIn = false;
        return true;
    }

    @Override
    public boolean logout () throws LoginException
    {
        this.loggedIn = false;
        return true;
    }

}
