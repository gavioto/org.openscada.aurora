/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.utils.password;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlainValidator implements PasswordValidator
{

    private static final List<PasswordEncoding> ENCODINGS = Arrays.asList ( PasswordEncoding.SHA1, PasswordEncoding.MD5, PasswordEncoding.PLAIN );

    private final boolean ignoreCase;

    public PlainValidator ( final boolean ignoreCase )
    {
        this.ignoreCase = ignoreCase;
    }

    @Override
    public List<PasswordEncoding> getSupportedInputEncodings ()
    {
        return ENCODINGS;
    }

    @Override
    public boolean validatePassword ( final Map<PasswordEncoding, String> passwords, final String storedPassword ) throws Exception
    {
        for ( final PasswordEncoding encoding : ENCODINGS )
        {
            final String providedPassword = passwords.get ( encoding );
            if ( providedPassword == null )
            {
                continue;
            }

            final String storedEncoded = encoding.encodeToHexString ( storedPassword );

            return comparePassword ( providedPassword, storedEncoded );
        }

        return false;
    }

    private boolean comparePassword ( final String providedPassword, final String storedPassword )
    {
        if ( this.ignoreCase )
        {
            return providedPassword.equalsIgnoreCase ( storedPassword );
        }
        else
        {
            return providedPassword.equals ( storedPassword );
        }
    }

}
