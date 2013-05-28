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

package org.openscada.sec.utils.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @since 1.1
 */
public enum PasswordEncoding
{
    PLAIN
    {
        private final PasswordEncoder encoder = new PasswordEncoder () {

            @Override
            public String encodePassword ( final String password )
            {
                return password;
            }
        };

        @Override
        public PasswordEncoder getEncoder ( final String characterEncoding, final PasswordDigestCodec codec ) throws Exception
        {
            return this.encoder;
        }

        @Override
        public MessageDigest getDigest () throws NoSuchAlgorithmException
        {
            return null;
        }
    },
    MD5
    {
        @Override
        public MessageDigest getDigest () throws NoSuchAlgorithmException
        {
            return MessageDigest.getInstance ( "MD5" );
        }

        @Override
        public PasswordEncoder getEncoder ( final String characterEncoding, final PasswordDigestCodec codec ) throws Exception
        {
            return new DigestEncoder ( "MD5", characterEncoding, codec );
        }
    },
    SHA1
    {
        @Override
        public PasswordEncoder getEncoder ( final String characterEncoding, final PasswordDigestCodec codec ) throws Exception
        {
            return new DigestEncoder ( "SHA1", characterEncoding, codec );
        }

        @Override
        public MessageDigest getDigest () throws NoSuchAlgorithmException
        {
            return MessageDigest.getInstance ( "SHA1" );
        }
    };

    public abstract PasswordEncoder getEncoder ( String characterEncoding, PasswordDigestCodec codec ) throws Exception;

    public String encodeToString ( final String password, final String characterEncoding, final PasswordDigestCodec codec ) throws Exception
    {
        return getEncoder ( characterEncoding, codec ).encodePassword ( password );
    }

    public String encodeToHexString ( final String password ) throws Exception
    {
        return encodeToString ( password, "UTF-8", new HexCodec () );
    }

    /**
     * @return the message digest or <code>null</code> if none is used
     * @throws NoSuchAlgorithmException
     */
    public abstract MessageDigest getDigest () throws NoSuchAlgorithmException;

}
