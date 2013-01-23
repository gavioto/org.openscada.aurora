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

import java.security.NoSuchAlgorithmException;

public enum PasswordType
{
    PLAIN
    {
        @Override
        public PasswordValidator createValdiator ()
        {
            return new PlainValidator ( false );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return new PasswordEncoder () {

                @Override
                public String encodePassword ( final String password )
                {
                    return password;
                }
            };
        }
    },
    PLAIN_IGNORE_CASE
    {
        @Override
        public PasswordValidator createValdiator ()
        {
            return new PlainValidator ( true );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return new PasswordEncoder () {

                @Override
                public String encodePassword ( final String password )
                {
                    return password;
                }
            };
        }
    },
    MD5_HEX
    {
        @Override
        public PasswordValidator createValdiator () throws NoSuchAlgorithmException
        {
            return new DigestValidator ( "MD5", "UTF-8", new HexCodec () );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return new DigestEncoder ( "MD5", "UTF-8", new HexCodec () );
        }
    },
    SHA1_HEX
    {
        @Override
        public PasswordValidator createValdiator () throws NoSuchAlgorithmException
        {
            return new DigestValidator ( "SHA1", "UTF-8", new HexCodec () );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return new DigestEncoder ( "SHA1", "UTF-8", new HexCodec () );
        }
    };

    public abstract PasswordValidator createValdiator () throws Exception;

    public abstract PasswordEncoder createEncoder () throws Exception;

}