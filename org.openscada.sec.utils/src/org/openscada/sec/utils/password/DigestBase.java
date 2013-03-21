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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestBase
{
    private final MessageDigest digest;

    private final Charset passwordCharset;

    public DigestBase ( final String algorithm, final String passwordCharsetEncoder )
    {
        this.passwordCharset = Charset.forName ( passwordCharsetEncoder );

        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance ( algorithm );
        }
        catch ( final NoSuchAlgorithmException e )
        {
        }
        this.digest = digest;
    }

    protected byte[] makeDigest ( final String providedPassword ) throws NoSuchAlgorithmException
    {
        if ( this.digest == null )
        {
            throw new NoSuchAlgorithmException ();
        }

        final ByteBuffer data = this.passwordCharset.encode ( providedPassword );
        this.digest.update ( data.array (), 0, data.remaining () );
        return this.digest.digest ();
    }
}
