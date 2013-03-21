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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DigestValidator implements PasswordValidator
{

    private final PasswordDigestCodec passwordDigestCodec;

    private final PasswordEncoding storedEncoding;

    private final List<PasswordEncoding> supportedInputEncodings;

    private final Charset passwordCharset;

    public DigestValidator ( final PasswordEncoding storedEncoding, final String passwordCharsetEncoder, final PasswordDigestCodec passwordDigestCodec )
    {
        this.storedEncoding = storedEncoding;
        this.passwordDigestCodec = passwordDigestCodec;
        this.supportedInputEncodings = Collections.unmodifiableList ( Arrays.asList ( storedEncoding, PasswordEncoding.PLAIN ) );
        this.passwordCharset = Charset.forName ( passwordCharsetEncoder );
    }

    @Override
    public List<PasswordEncoding> getSupportedInputEncodings ()
    {
        return this.supportedInputEncodings;
    }

    @Override
    public boolean validatePassword ( final Map<PasswordEncoding, String> passwords, final String storedPassword ) throws Exception
    {
        final byte[] storedDigest = this.passwordDigestCodec.decode ( storedPassword );

        final String encodedPassword = passwords.get ( this.storedEncoding );
        if ( encodedPassword != null )
        {
            final byte[] providedDigest = new HexCodec ().decode ( encodedPassword );
            return MessageDigest.isEqual ( providedDigest, storedDigest );
        }

        final String plainPassword = passwords.get ( PasswordEncoding.PLAIN );
        if ( plainPassword != null )
        {
            final byte[] providedDigest = makeDigest ( plainPassword );
            return MessageDigest.isEqual ( providedDigest, storedDigest );
        }

        return false;
    }

    private byte[] makeDigest ( final String plainPassword ) throws NoSuchAlgorithmException
    {
        final MessageDigest digest = this.storedEncoding.getDigest ();

        final ByteBuffer data = this.passwordCharset.encode ( plainPassword );
        digest.update ( data.array (), 0, data.remaining () );
        return digest.digest ();
    }
}
