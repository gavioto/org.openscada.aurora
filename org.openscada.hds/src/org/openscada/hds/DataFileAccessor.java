/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
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
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hds;

import java.io.IOException;
import java.util.Date;

public interface DataFileAccessor
{

    public abstract void insertValue ( final double value, final Date date, final boolean error, final boolean manual, final boolean heartbeat ) throws IOException;

    public abstract boolean visit ( final ValueVisitor visitor ) throws Exception;

    public abstract void dispose ();

    public abstract boolean visitFirstValue ( ValueVisitor visitor ) throws Exception;

    /**
     * Forward correct entries
     * <p>
     * </p>
     * @param value the value
     * @param date the starting point
     * @param error the error flag
     * @param manual the manual flag
     * @throws Exception 
     */
    public abstract void forwardCorrect ( double value, Date date, boolean error, boolean manual ) throws Exception;

}