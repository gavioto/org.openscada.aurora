/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
