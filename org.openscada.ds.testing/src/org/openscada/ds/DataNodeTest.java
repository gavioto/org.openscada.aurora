/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
