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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ValidationTest
{
    private PasswordValidator plainValidator;

    @Before
    public void setup ()
    {
        this.plainValidator = PasswordType.PLAIN.createValdiator ();
    }

    @Test
    public void testPlain1 () throws Exception
    {
        final HashMap<PasswordEncoding, String> passwords = new HashMap<PasswordEncoding, String> ();
        Assert.assertFalse ( "Empty result map", this.plainValidator.validatePassword ( passwords, "test12" ) );
    }

    @Test
    public void testPlain2 () throws Exception
    {
        final String password = "test12";
        Assert.assertTrue ( "Provided PLAIN", this.plainValidator.validatePassword ( makeMap ( password, PasswordEncoding.PLAIN ), password ) );
        Assert.assertTrue ( "Provided MD5", this.plainValidator.validatePassword ( makeMap ( password, PasswordEncoding.MD5 ), password ) );
        Assert.assertTrue ( "Provided SHA1", this.plainValidator.validatePassword ( makeMap ( password, PasswordEncoding.SHA1 ), password ) );
    }

    @Test
    public void testPlain3 () throws Exception
    {
        final String password = "test12";
        Assert.assertTrue ( "Provided PLAIN, MD5 and SHA1", this.plainValidator.validatePassword ( makeMap ( password, PasswordEncoding.PLAIN, PasswordEncoding.MD5, PasswordEncoding.SHA1 ), password ) );
    }

    private Map<PasswordEncoding, String> makeMap ( final String password, final PasswordEncoding... encodings ) throws Exception
    {
        final Map<PasswordEncoding, String> result = new HashMap<PasswordEncoding, String> ( 1 );

        for ( final PasswordEncoding encoding : encodings )
        {
            result.put ( encoding, encoding.encodeToHexString ( password ) );
        }

        return result;
    }

}
