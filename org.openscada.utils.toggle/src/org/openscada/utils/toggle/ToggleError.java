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
package org.openscada.utils.toggle;

public class ToggleError extends RuntimeException
{
    private static final long serialVersionUID = 5657944778147860794L;

    public ToggleError ()
    {
        super ();
    }

    public ToggleError ( final String message, final Throwable cause )
    {
        super ( message, cause );
    }

    public ToggleError ( final String message )
    {
        super ( message );
    }

    public ToggleError ( final Throwable cause )
    {
        super ( cause );
    }
}
