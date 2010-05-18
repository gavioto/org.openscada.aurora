/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ds;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class DataNodeTest
{

    protected void testSerialize ( final Serializable data ) throws IOException, ClassNotFoundException
    {
        final DataNode node = new DataNode ( "id", data );

        final Object result = node.getDataAsObject ();

        Assert.assertEquals ( data, result );
        Assert.assertNotSame ( data, result );
    }

    @Test
    public void test1 () throws IOException, ClassNotFoundException
    {
        testSerialize ( "Hello World" );
    }

    @Test
    public void test2 () throws IOException, ClassNotFoundException
    {
        final HashMap<String, String> data = new HashMap<String, String> ();

        data.put ( "foo", "bar" );
        data.put ( "question", "42" );

        testSerialize ( data );
    }
}
