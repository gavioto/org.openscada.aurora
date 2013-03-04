/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) Jens Reimann (ctron@dentrassi.de)
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

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.UserInformation;
import org.openscada.sec.osgi.AuthenticationHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private static Activator instance;

    private AuthenticationHelper authentication;

    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        this.authentication = new AuthenticationHelper ( context );
        this.authentication.open ();

        instance = this;
    }

    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        instance = null;

        this.authentication.close ();
        this.authentication = null;
    }

    public static Activator getInstance ()
    {
        return instance;
    }

    public UserInformation authenticate ( final String username, final char[] password ) throws AuthenticationException
    {
        return this.authentication.authenticate ( username, String.valueOf ( password ) );
    }
}
