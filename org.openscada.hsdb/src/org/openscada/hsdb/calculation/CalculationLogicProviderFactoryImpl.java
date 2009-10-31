package org.openscada.hsdb.calculation;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.datatypes.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for creating calculation logic provider objects according to detail level id and calculation method id.
 * @author Ludwig Straub
 */
public class CalculationLogicProviderFactoryImpl implements CalculationLogicProviderFactory
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( CalculationLogicProviderFactoryImpl.class );

    /**
     * @see CalculationLogicProviderFactory#getCalculationLogicProvider(StorageChannelMetaData)
     */
    public CalculationLogicProvider getCalculationLogicProvider ( final StorageChannelMetaData metaData )
    {
        final DataType nativeDataType = metaData.getDataType ();
        final long[] calculationMethodParameters = metaData.getCalculationMethodParameters ();
        switch ( metaData.getCalculationMethod () )
        {
        case AVERAGE:
        {
            return new AverageCalculationLogicProvider ( metaData.getDetailLevelId () > 1 ? DataType.DOUBLE_VALUE : nativeDataType, DataType.DOUBLE_VALUE, calculationMethodParameters );
        }
        case MAXIMUM:
        {
            return new MaximumCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        case MINIMUM:
        {
            return new MinimumCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        case NATIVE:
        {
            return new NativeCalculationLogicProvider ( nativeDataType, nativeDataType, calculationMethodParameters );
        }
        default:
        {
            final String message = String.format ( "invalid calculation method specified (%s)", metaData );
            logger.error ( message );
            return null;
        }
        }
    }
}
