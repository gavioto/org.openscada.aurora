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
package org.openscada.sec.osgi;

import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.utils.concurrent.NotifyFuture;

public interface AuthorizationManager
{

    public NotifyFuture<AuthorizationReply> authorize ( AuthorizationContext context, AuthorizationResult defaultResult );

}
