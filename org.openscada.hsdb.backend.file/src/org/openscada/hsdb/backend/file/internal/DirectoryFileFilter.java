/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.hsdb.backend.file.internal;

import java.io.File;
import java.io.FileFilter;

/**
 * This file filter searches for sub directories with the specified name.
 * @author Ludwig Straub
 */
public class DirectoryFileFilter implements FileFilter
{
    /** Case insensitive name of the sub directories that have to be searched. If null is set then all directories are accepted. */
    private final String name;

    /**
     * Constructor
     * @param name case insensitive name of the sub directories that have to be searched. If null is passed then all directories are accepted
     */
    public DirectoryFileFilter ( final String name )
    {
        this.name = name;
    }

    /**
     * @see java.io.FileFilter#accept
     */
    public boolean accept ( final File file )
    {
        return file.isDirectory () && ( this.name == null || file.getName ().equalsIgnoreCase ( this.name ) );
    }
}
