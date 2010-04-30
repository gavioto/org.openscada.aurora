/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.hsdb.datatypes;

/**
 * Enumeration of all supported datatypes.
 * @author Ludwig Straub
 */
public enum DataType
{
    /** The datatype is unknown. */
    UNKNOWN,

    /** LongValue. */
    LONG_VALUE,

    /** DoubleValue. */
    DOUBLE_VALUE;

    /**
     * This method transforms the passed datatype value to a corresponding short string representation.
     * This method is inverse to the method convertShortStringToDataType.
     * @param dataType datatype value that has to be transformed
     * @return short string representation of the passed datatype value
     */
    public static String convertDataTypeToShortString ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return "INT64";
        }
        case DOUBLE_VALUE:
        {
            return "DOUBLE";
        }
        default:
        {
            return "UNK";
        }
        }
    }

    /**
     * This method transforms the passed short string representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToShortString.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertShortStringToDataType ( final String dataType )
    {
        final String trimmedDataType = dataType == null ? "" : dataType.trim ();
        if ( "INT64".equals ( trimmedDataType ) )
        {
            return LONG_VALUE;
        }
        if ( "DOUBLE".equals ( trimmedDataType ) )
        {
            return DOUBLE_VALUE;
        }
        return UNKNOWN;
    }

    /**
     * This method transforms the passed datatype value to a corresponding string representation.
     * This method is inverse to the method convertStringToDataType.
     * @param dataType datatype value that has to be transformed
     * @return string representation of the passed datatype
     */
    public static String convertDataTypeToString ( final DataType dataType )
    {
        return dataType == null ? UNKNOWN.toString () : dataType.toString ();
    }

    /**
     * This method transforms the passed string representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToString.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertStringToDataType ( final String dataType )
    {
        return dataType == null ? UNKNOWN : Enum.valueOf ( DataType.class, dataType.trim () );
    }

    /**
     * This method transforms the passed datatype value to a corresponding long representation.
     * This method is inverse to the method convertLongToDataType.
     * @param dataType datatype value that has to be transformed
     * @return long representation of the passed datatype
     */
    public static long convertDataTypeToLong ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return 0L;
        }
        case DOUBLE_VALUE:
        {
            return 1L;
        }
        default:
        {
            return -1L;
        }
        }
    }

    /**
     * This method transforms the passed long representation of a datatype value to the corresponding datatype value.
     * This method is inverse to the method convertDataTypeToLong.
     * @param dataType datatype value that has to be transformed
     * @return transformed datatype value
     */
    public static DataType convertLongToDataType ( final long dataType )
    {
        switch ( (int)dataType )
        {
        case 0:
        {
            return LONG_VALUE;
        }
        case 1:
        {
            return DOUBLE_VALUE;
        }
        default:
        {
            return DataType.UNKNOWN;
        }
        }
    }
}
