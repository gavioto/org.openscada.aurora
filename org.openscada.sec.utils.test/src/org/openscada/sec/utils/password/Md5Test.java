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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class Md5Test
{
    private Map<PasswordEncoding, String> makeResult ( final String string )
    {
        final Map<PasswordEncoding, String> result = new HashMap<PasswordEncoding, String> ( 1 );
        result.put ( PasswordEncoding.PLAIN, string );
        return result;
    }

    @Test
    public void test1 () throws Exception
    {
        final DigestValidator digest = new DigestValidator ( PasswordEncoding.MD5, "UTF-8", new HexCodec () );
        Assert.assertEquals ( "Passwords do not match", true, digest.validatePassword ( makeResult ( "test12" ), "60474c9c10d7142b7508ce7a50acf414" ) );
    }

    @Test
    public void test2 () throws Exception
    {
        final DigestValidator digest = new DigestValidator ( PasswordEncoding.MD5, "UTF-8", new HexCodec () );
        Assert.assertEquals ( "Passwords do match", false, digest.validatePassword ( makeResult ( "test123" ), "60474c9c10d7142b7508ce7a50acf414" ) );
    }

    @Test
    public void test3 () throws Exception
    {
        final DigestValidator digest = new DigestValidator ( PasswordEncoding.MD5, "UTF-8", new HexCodec () );
        Assert.assertEquals ( "Passwords do not match", true, digest.validatePassword ( makeResult ( "öäü" ), "7448211b4951bf618d8b7688144295ba" ) );
    }
}
