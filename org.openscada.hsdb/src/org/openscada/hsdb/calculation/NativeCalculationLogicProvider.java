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

import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the processing of native values.
 * @author Ludwig Straub
 */
public class NativeCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behavior
     */
    public NativeCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
    {
        super ( inputDataType, outputDataType, parameters );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#getPassThroughValues
     */
    public boolean getPassThroughValues ()
    {
        return true;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final LongValue[] values )
    {
        return 0;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final DoubleValue[] values )
    {
        return 0;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final LongValue[] values )
    {
        return Double.NaN;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final DoubleValue[] values )
    {
        return Double.NaN;
    }
}
