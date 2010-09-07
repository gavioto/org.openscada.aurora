/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.hsdb.calculation;

import org.openscada.hsdb.StorageChannelMetaData;

/**
 * This interface provides methods for creating calculation logic provider objects.
 * @author Ludwig Straub
 */
public interface CalculationLogicProviderFactory
{
    /**
     * This method creates and returns a calculation logic provider instance that supports the specified configuration.
     * @param metaData configuration that is used when creating the calculation logic provider instance
     * @return created logic provider instance
     */
    public abstract CalculationLogicProvider getCalculationLogicProvider ( StorageChannelMetaData metaData );
}
