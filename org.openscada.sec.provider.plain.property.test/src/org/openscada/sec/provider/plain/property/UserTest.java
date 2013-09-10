/*******************************************************************************
 * Copyright (c) 2013 Jens Reimann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package org.openscada.sec.provider.plain.property;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.sec.authn.AbstractPlainAuthenticationService.UserEntry;

public class UserTest
{
    @Test
    public void test1 ()
    {
        final Map<String, UserEntry> result = PropertyAuthenticationService.split ( "user:password:" );
        Assert.assertEquals ( 1, result.size () );
        final UserEntry user = result.get ( "user" );
        Assert.assertNotNull ( user );
    }
}
