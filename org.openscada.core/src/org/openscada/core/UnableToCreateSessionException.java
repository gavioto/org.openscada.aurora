/*******************************************************************************
 * Copyright (c) 2006, 2012 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.core;

import org.openscada.utils.statuscodes.CodedException;

public class UnableToCreateSessionException extends CodedException
{

    private static final long serialVersionUID = 1L;

    public UnableToCreateSessionException ( final String message )
    {
        super ( StatusCodes.UNABLE_TO_CREATE_SESSION, message );
    }

    public UnableToCreateSessionException ( final CodedException e )
    {
        super ( e.getStatus (), e );
    }

}
