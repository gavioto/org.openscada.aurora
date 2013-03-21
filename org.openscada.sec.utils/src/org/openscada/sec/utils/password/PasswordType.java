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

import java.util.List;

public enum PasswordType
{
    PLAIN
    {
        @Override
        public PasswordEncoding getEncoding () throws Exception
        {
            return PasswordEncoding.PLAIN;
        }

        @Override
        public PasswordValidator createValdiator ()
        {
            return new PlainValidator ( false );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return getEncoding ().getEncoder ( "UTF-8", new HexCodec () );
        }
    },
    PLAIN_IGNORE_CASE
    {
        @Override
        public PasswordEncoding getEncoding () throws Exception
        {
            return PasswordEncoding.PLAIN;
        }

        @Override
        public PasswordValidator createValdiator ()
        {
            return new PlainValidator ( true );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return getEncoding ().getEncoder ( "UTF-8", new HexCodec () );
        }
    },
    MD5_HEX
    {
        @Override
        public PasswordEncoding getEncoding () throws Exception
        {
            return PasswordEncoding.MD5;
        }

        @Override
        public PasswordValidator createValdiator ()
        {
            return new DigestValidator ( PasswordEncoding.MD5, "UTF-8", new HexCodec () );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return getEncoding ().getEncoder ( "UTF-8", new HexCodec () );
        }
    },
    SHA1_HEX
    {
        @Override
        public PasswordEncoding getEncoding () throws Exception
        {
            return PasswordEncoding.SHA1;
        }

        @Override
        public PasswordValidator createValdiator ()
        {
            return new DigestValidator ( PasswordEncoding.SHA1, "UTF-8", new HexCodec () );
        }

        @Override
        public PasswordEncoder createEncoder () throws Exception
        {
            return getEncoding ().getEncoder ( "UTF-8", new HexCodec () );
        }
    };

    public abstract PasswordEncoding getEncoding () throws Exception;

    public abstract PasswordValidator createValdiator ();

    public abstract PasswordEncoder createEncoder () throws Exception;

    /**
     * @see PasswordValidator#getSupportedInputEncodings()
     */
    public List<PasswordEncoding> getSupportedInputEncodings ()
    {
        return createValdiator ().getSupportedInputEncodings ();
    }

}