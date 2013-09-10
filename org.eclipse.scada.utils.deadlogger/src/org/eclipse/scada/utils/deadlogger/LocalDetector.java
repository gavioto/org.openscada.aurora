/*******************************************************************************
 * Copyright (c) 2011 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.utils.deadlogger;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDetector implements Detector
{

    private final static Logger logger = LoggerFactory.getLogger ( LocalDetector.class );

    @Override
    public boolean isDeadlock ()
    {
        logger.info ( "Checking for deadlocks" );

        return false;
    }

    @Override
    public void dump ( final PrintStream out )
    {
        if ( !isDeadlock () )
        {
            out.println ( "No deadlock detected" );
            return;
        }
    }

}
