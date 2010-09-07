/*******************************************************************************
 * Copyright (c) 2009 TH4 SYSTEMS GmbH and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jens Reimann (TH4 SYSTEMS GmbH) - initial API and implementation
 *******************************************************************************/

package com.inavare.maven.platform;

import java.io.File;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public final class WizardPageExtension extends WizardPage
{
    private Text directoryText;

    WizardPageExtension ( final String pageName )
    {
        super ( pageName );
        setTitle ( "Test Page" );
        setDescription ( "Test Description" );
        setPageComplete ( true );
    }

    public void createControl ( final Composite parent )
    {
        Composite client = new Composite ( parent, SWT.NONE );

        client.setLayout ( new GridLayout ( 3, false ) );

        Label label = new Label ( client, SWT.NONE );
        label.setText ( "Maven Repository Path" );
        label.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.CENTER, false, false ) );

        this.directoryText = new Text ( client, SWT.BORDER );
        this.directoryText.setLayoutData ( new GridData ( SWT.FILL, SWT.CENTER, true, false ) );
        this.directoryText.addModifyListener ( new ModifyListener () {

            public void modifyText ( final ModifyEvent e )
            {
                update ();
            }
        } );
        this.directoryText.setText ( getInitialPath () );

        Button button = new Button ( client, SWT.PUSH );
        button.setLayoutData ( new GridData ( SWT.BEGINNING, SWT.CENTER, false, false ) );
        button.setText ( "Browse..." );
        button.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                WizardPageExtension.this.handleBrowse ();
            }
        } );

        setControl ( client );
        update ();
    }

    private String getInitialPath ()
    {
        File file = new File ( System.getProperty ( "user.home", "/" ) );
        file = new File ( file, ".m2" );
        file = new File ( file, "repository" );
        return file.getAbsolutePath ();
    }

    public File getFile ()
    {
        return new File ( this.directoryText.getText () );
    }

    protected void update ()
    {
        String dir = this.directoryText.getText ();
        File file = new File ( dir );

        setMessage ( null );
        setPageComplete ( false );

        if ( !file.exists () )
        {
            setMessage ( String.format ( "Directory '%s' does not exists", file.getAbsolutePath () ), IMessageProvider.ERROR );
            return;
        }
        if ( !file.isDirectory () )
        {
            setMessage ( String.format ( "Path '%s' is not a directory", file.getAbsolutePath () ), IMessageProvider.ERROR );
            return;
        }
        if ( !file.canRead () )
        {
            setMessage ( String.format ( "Directory '%s' is not readable", file.getAbsolutePath () ), IMessageProvider.WARNING );
        }
        if ( !file.isAbsolute () )
        {
            setMessage ( String.format ( "Path to '%s' is not absolute", file.getAbsolutePath () ), IMessageProvider.WARNING );
        }

        setPageComplete ( true );
    }

    protected void handleBrowse ()
    {
        DirectoryDialog dlg = new DirectoryDialog ( this.getShell (), SWT.PRIMARY_MODAL );
        dlg.setFilterPath ( this.directoryText.getText () );
        String dir = dlg.open ();
        if ( dir != null )
        {
            this.directoryText.setText ( dir );
        }
    }
}