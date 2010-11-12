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

package org.openscada.sec.provider.dummy;

import java.util.Map;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.UserInformation;

/**
 * A dummy authorization service that grants everything
 * @author Jens Reimann
 * @since 0.15.0
 */
public class DummyAuthorizationProviderImpl implements AuthorizationService
{
    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return AuthorizationResult.GRANTED;
    }

}
