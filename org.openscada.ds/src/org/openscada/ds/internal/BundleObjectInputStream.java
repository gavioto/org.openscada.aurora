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

package org.openscada.ds.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

import org.osgi.framework.Bundle;

/**
 * An object input stream which loads its classes from the provided bundle.
 * @author Jens Reimann
 *
 */
public class BundleObjectInputStream extends ObjectInputStream
{
    private final Bundle bundle;

    public BundleObjectInputStream ( final InputStream in, final Bundle bundle ) throws IOException
    {
        super ( in );
        this.bundle = bundle;
    }

    @Override
    protected Class<?> resolveClass ( final ObjectStreamClass desc ) throws IOException, ClassNotFoundException
    {
        return this.bundle.loadClass ( desc.getName () );
    }

}
