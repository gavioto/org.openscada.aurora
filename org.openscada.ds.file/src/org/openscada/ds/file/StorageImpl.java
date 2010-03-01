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

package org.openscada.ds.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executor;

import org.openscada.ds.DataNode;
import org.openscada.ds.DataStore;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorage implements DataStore
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final File rootFolder;

    public StorageImpl ( final Executor executor ) throws IOException
    {
        super ( executor );
        this.rootFolder = new File ( System.getProperty ( "org.openscada.ds.file.root", System.getProperty ( "user.dir" ) + File.pathSeparator + ".openscadaDS" ) );
        if ( !this.rootFolder.exists () )
        {
            this.rootFolder.mkdirs ();
        }
        if ( !this.rootFolder.exists () || !this.rootFolder.isDirectory () )
        {
            throw new IOException ( "Unable to use directory: " + this.rootFolder );
        }
    }

    @Override
    public synchronized NotifyFuture<DataNode> readNode ( final String nodeId )
    {
        try
        {
            return new InstantFuture<DataNode> ( loadFile ( nodeId ) );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to load data node", e );
            return new InstantErrorFuture<DataNode> ( e );
        }
    }

    private DataNode loadFile ( final String nodeId ) throws IOException
    {
        final File file = makeFile ( nodeId );
        final FileInputStream stream = new FileInputStream ( file );

        try
        {
            return new DataNode ( nodeId, stream );
        }
        catch ( final FileNotFoundException e )
        {
            return null;
        }
        finally
        {
            stream.close ();
        }
    }

    private File makeFile ( final String nodeId )
    {
        try
        {
            return new File ( this.rootFolder, URLEncoder.encode ( nodeId, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            return new File ( nodeId.replaceAll ( "[^a-zA-Z0-9]", "_" ) );
        }
    }

    public synchronized NotifyFuture<Void> writeNode ( final DataNode node )
    {
        final File file = makeFile ( node.getId () );
        try
        {
            saveTo ( node, file );
            return new InstantFuture<Void> ( null );
        }
        catch ( final IOException e )
        {
            logger.warn ( "Failed to store data node", e );
            return new InstantErrorFuture<Void> ( e );
        }
    }

    private void saveTo ( final DataNode node, final File file ) throws IOException
    {
        file.delete ();

        final byte[] data = node.getData ();
        if ( data == null )
        {
            return;
        }

        final FileOutputStream stream = new FileOutputStream ( file );
        try
        {
            stream.write ( node.getData () );
        }
        finally
        {
            stream.close ();
        }
    }

    public synchronized NotifyFuture<Void> deleteNode ( final String nodeId )
    {
        final File file = makeFile ( nodeId );
        file.delete ();
        return new InstantFuture<Void> ( null );
    }
}
