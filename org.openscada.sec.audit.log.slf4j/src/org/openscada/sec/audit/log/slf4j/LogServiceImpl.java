/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassid.de)
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

package org.openscada.sec.audit.log.slf4j;

import org.openscada.sec.audit.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class LogServiceImpl implements AuditLogService
{

    private final static Logger logger = LoggerFactory.getLogger ( LogServiceImpl.class );

    @Override
    public void info ( final String message, final Object... arguments )
    {
        logger.info ( message, arguments );
    }

    @Override
    public void debug ( final String message, final Object... arguments )
    {
        logger.debug ( message, arguments );
    }

    @Override
    public void info ( final String message, final Throwable e, final Object... arguments )
    {
        logger.info ( MessageFormatter.arrayFormat ( message, arguments ).getMessage (), e );
    }

    @Override
    public void debug ( final String message, final Throwable e, final Object... arguments )
    {
        logger.debug ( MessageFormatter.arrayFormat ( message, arguments ).getMessage (), e );
    }
}
