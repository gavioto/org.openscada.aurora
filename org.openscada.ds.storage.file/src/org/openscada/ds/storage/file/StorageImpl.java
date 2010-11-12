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

package org.openscada.ds.storage.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Executor;

import org.openscada.ds.DataNode;
import org.openscada.ds.storage.AbstractStorage;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorage
{

    private static final int SPLIT_PATH_DEPTH = 2;

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final File rootFolder;

    public StorageImpl ( final Executor executor ) throws IOException
    {
        super ( executor );
        this.rootFolder = new File ( System.getProperty ( "org.openscada.ds.storage.file.root", System.getProperty ( "user.home" ) + File.separator + ".openscadaDS" ) );
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

        // quick check for existence
        if ( !file.exists () || !file.canRead () )
        {
            return null;
        }

        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream ( file );

            return new DataNode ( nodeId, stream );
        }
        catch ( final FileNotFoundException e )
        {
            return null;
        }
        finally
        {
            if ( stream != null )
            {
                stream.close ();
            }
        }
    }

    private File makeFile ( final String nodeId )
    {
        final String hash = String.format ( "%08X", nodeId.hashCode () );
        File root = this.rootFolder;
        for ( int i = 1; i <= SPLIT_PATH_DEPTH; i++ )
        {
            root = new File ( root, hash.substring ( 0, i ) );
        }

        try
        {
            return new File ( root, URLEncoder.encode ( nodeId, "UTF-8" ) );
        }
        catch ( final UnsupportedEncodingException e )
        {
            return new File ( root, nodeId.replaceAll ( "[^a-zA-Z0-9]", "_" ) );
        }
    }

    public synchronized NotifyFuture<Void> writeNode ( final DataNode node )
    {
        final File file = makeFile ( node.getId () );
        try
        {
            saveTo ( node, file );
            fireUpdate ( node );
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

        file.getParentFile ().mkdirs ();

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
