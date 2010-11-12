/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
 * This file filter searches for files matching files with the specified name.
 * @author Ludwig Straub
 */
public class FileFileFilter implements FileFilter
{
    /** Case insensitive name of the files that have to be searched. If null is set then all files are accepted. */
    private final String fileNamePattern;

    /**
     * Constructor
     * @param fileNamePattern case pattern of file names that have to be searched. If null is passed then all files are accepted
     */
    public FileFileFilter ( final String fileNamePattern )
    {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * @see java.io.FileFilter#accept
     */
    public boolean accept ( final File file )
    {
        return file.isFile () && ( this.fileNamePattern == null || file.getName ().matches ( this.fileNamePattern ) );
    }
}
