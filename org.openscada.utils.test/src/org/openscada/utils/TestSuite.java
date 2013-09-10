/*******************************************************************************
 * Copyright (c) 2006, 2010 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openscada.utils.concurrent.ListeningFutureTest;
import org.openscada.utils.exec.AsyncOperationTest;
import org.openscada.utils.exec.SyncOperationTest;
import org.openscada.utils.str.EncoderTest;

@RunWith ( Suite.class )
@Suite.SuiteClasses ( { ListeningFutureTest.class, AsyncOperationTest.class, EncoderTest.class, SyncOperationTest.class } )
public class TestSuite
{
}
