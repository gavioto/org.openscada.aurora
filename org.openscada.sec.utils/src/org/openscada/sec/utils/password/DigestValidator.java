/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestValidator implements PasswordValidator
{

    private final MessageDigest digest;

    private final Charset passwordCharset;

    private final PasswordDigestCodec passwordDigestCodec;

    public DigestValidator ( final String algorithm, final String passwordCharsetEncoder, final PasswordDigestCodec passwordDigestCodec ) throws NoSuchAlgorithmException
    {
        this.digest = MessageDigest.getInstance ( algorithm );
        this.passwordCharset = Charset.forName ( passwordCharsetEncoder );
        this.passwordDigestCodec = passwordDigestCodec;
    }

    @Override
    public boolean validatePassword ( final String providedPassword, final String storedPassword ) throws Exception
    {
        return compare ( makeDigest ( providedPassword ), storedPassword );
    }

    private byte[] makeDigest ( final String providedPassword )
    {
        return this.digest.digest ( this.passwordCharset.encode ( providedPassword ).array () );
    }

    protected boolean compare ( final byte[] data, final String storedPassword )
    {
        return MessageDigest.isEqual ( data, this.passwordDigestCodec.decode ( storedPassword ) );
    }

}
