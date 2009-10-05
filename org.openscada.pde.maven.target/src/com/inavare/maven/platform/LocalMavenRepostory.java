/*******************************************************************************
 * Copyright (c) 2009 inavare GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens Reimann (inavare GmbH) - initial API and implementation
 *******************************************************************************/

package com.inavare.maven.platform;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.pde.ui.IProvisionerWizard;

public class LocalMavenRepostory extends Wizard implements IProvisionerWizard
{

    private WizardPageExtension page;

    public LocalMavenRepostory ()
    {
        setDialogSettings ( Activator.getDefault ().getDialogSettings () );
        setWindowTitle ( "Test Title" );
        setDefaultPageImageDescriptor ( ImageDescriptor.getMissingImageDescriptor () );
        setNeedsProgressMonitor ( true );
    }

    @Override
    public void addPages ()
    {
        addPage ( this.page = new WizardPageExtension ( "Test" ) );
        super.addPages ();
    }

    private File[] files;

    @Override
    public boolean performFinish ()
    {
        File repo = this.page.getFile ();

        Set<File> files = new HashSet<File> ();
        addFiles ( repo, files );
        this.files = files.toArray ( new File[0] );

        return true;
    }

    private void addFiles ( final File parent, final Set<File> files )
    {
        for ( String name : parent.list () )
        {
            File dir = new File ( parent, name );
            if ( dir.isDirectory () )
            {
                if ( shouldAdd ( dir ) )
                {
                    files.add ( dir );
                }
                addFiles ( dir, files );
            }
        }
    }

    private boolean shouldAdd ( final File dir )
    {
        for ( File file : dir.listFiles () )
        {
            if ( isBundle ( file ) )
            {
                return true;
            }
        }
        return false;
    }

    private boolean isBundle ( final File file )
    {
        if ( !file.getName ().toLowerCase ().endsWith ( ".jar" ) )
        {
            return false;
        }

        JarFile jarFile = null;
        try
        {
            jarFile = new JarFile ( file );
            final Manifest m = jarFile.getManifest ();
            if ( m == null )
            {
                return false;
            }
            final Attributes a = m.getMainAttributes ();
            final Object o = a.getValue ( "Bundle-SymbolicName" );
            return o != null;
        }
        catch ( IOException e )
        {
            return false;
        }
        finally
        {
            if ( jarFile != null )
            {
                try
                {
                    jarFile.close ();
                }
                catch ( IOException e )
                {
                    Activator.getDefault ().getLog ().log ( new Status ( Status.ERROR, Activator.PLUGIN_ID, "Failed to close jar file", e ) );
                }
            }
        }
    }

    public File[] getLocations ()
    {
        return this.files;
    }

}
