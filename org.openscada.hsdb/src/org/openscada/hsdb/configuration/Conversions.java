package org.openscada.hsdb.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides static methods for converting between different data formats.
 * @author Ludwig Straub
 */
public class Conversions
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( Conversions.class );

    /** Count of milliseconds per seconds. */
    public final static long SECOND_SPAN = 1000;

    /** Count of milliseconds per minute. */
    public final static long MINUTE_SPAN = SECOND_SPAN * 60;

    /** Count of milliseconds per hour. */
    public final static long HOUR_SPAN = MINUTE_SPAN * 60;

    /** Count of milliseconds per day. */
    public final static long DAY_SPAN = HOUR_SPAN * 24;

    /** Count of milliseconds per year (365 days). */
    public final static long YEAR_SPAN = DAY_SPAN * 365;

    /** Suffix for text values of milliseconds. */
    public final static String MILLISECOND_SPAN_SUFFIX = "ms";

    /** Suffix for text values of seconds. */
    public final static String SECOND_SPAN_SUFFIX = "s";

    /** Suffix for text values of minutes. */
    public final static String MINUTE_SPAN_SUFFIX = "m";

    /** Suffix for text values of hours. */
    public final static String HOUR_SPAN_SUFFIX = "h";

    /** Suffix for text values of days. */
    public final static String DAY_SPAN_SUFFIX = "d";

    /** Suffix for text values of years (365 days). */
    public final static String YEAR_SPAN_SUFFIX = "y";

    /** Seperatur used to split the elements of a list within the configuration. */
    public final static String LIST_SEPARATOR = ",";

    /** Empty array of meta data information objects. */
    private final static StorageChannelMetaData[] EMPTY_META_DATA_ARRAY = new StorageChannelMetaData[0];

    /** Empty array of calculation method objects. */
    private final static CalculationMethod[] EMPTY_CALCULATION_METHOD_ARRAY = new CalculationMethod[0];

    /**
     * This method converts the passed time span in milliseconds to a user friendly text.
     * @param timeSpan time span in milliseconds that has to be converted
     * @return user friendly text
     */
    public static String encodeTimeSpan ( final long timeSpan )
    {
        if ( timeSpan == 0 )
        {
            return timeSpan + MILLISECOND_SPAN_SUFFIX;
        }
        if ( ( timeSpan % YEAR_SPAN ) == 0 )
        {
            return ( timeSpan / YEAR_SPAN ) + YEAR_SPAN_SUFFIX;
        }
        if ( ( timeSpan % DAY_SPAN ) == 0 )
        {
            return ( timeSpan / DAY_SPAN ) + DAY_SPAN_SUFFIX;
        }
        if ( ( timeSpan % HOUR_SPAN ) == 0 )
        {
            return ( timeSpan / HOUR_SPAN ) + HOUR_SPAN_SUFFIX;
        }
        if ( ( timeSpan % MINUTE_SPAN ) == 0 )
        {
            return ( timeSpan / MINUTE_SPAN ) + MINUTE_SPAN_SUFFIX;
        }
        if ( ( timeSpan % SECOND_SPAN ) == 0 )
        {
            return ( timeSpan / SECOND_SPAN ) + SECOND_SPAN_SUFFIX;
        }
        return timeSpan + MILLISECOND_SPAN_SUFFIX;
    }

    /**
     * This method converts the passed user friendly text to a valid time span in milliseconds.
     * @param timeSpan text that has to be converted
     * @return time span in milliseconds
     */
    public static long decodeTimeSpan ( final String timeSpan )
    {
        if ( timeSpan == null )
        {
            return 0;
        }
        if ( timeSpan.endsWith ( MILLISECOND_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - MILLISECOND_SPAN_SUFFIX.length () ), 0 );
        }
        if ( timeSpan.endsWith ( SECOND_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - SECOND_SPAN_SUFFIX.length () ), 0 ) * SECOND_SPAN;
        }
        if ( timeSpan.endsWith ( MINUTE_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - MINUTE_SPAN_SUFFIX.length () ), 0 ) * MINUTE_SPAN;
        }
        if ( timeSpan.endsWith ( HOUR_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - HOUR_SPAN_SUFFIX.length () ), 0 ) * HOUR_SPAN;
        }
        if ( timeSpan.endsWith ( DAY_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - DAY_SPAN_SUFFIX.length () ), 0 ) * DAY_SPAN;
        }
        if ( timeSpan.endsWith ( YEAR_SPAN_SUFFIX ) )
        {
            return parseLong ( timeSpan.substring ( 0, timeSpan.length () - YEAR_SPAN_SUFFIX.length () ), 0 ) * YEAR_SPAN;
        }
        return 0;
    }

    /**
     * This method creates a configuration object using the passed meta data objects as input.
     * @param metaDatas input for the configuration object that has to be created
     * @return created configuration object
     * @throws Exception if configuration object could not be created due to missing input data
     */
    public static Configuration convertMetaDatasToConfiguration ( final StorageChannelMetaData[] metaDatas ) throws Exception
    {
        // assure valid input
        if ( ( metaDatas == null ) || metaDatas.length == 0 )
        {
            final String message = "no or invalid meta data objects were passed to configuration factory method";
            logger.error ( message );
            throw new Exception ( message );
        }

        // access first object to retrieve common meta data
        final Configuration configuration = new Configuration ();
        configuration.setId ( metaDatas[0].getConfigurationId () );

        // fill configuration data
        final Map<String, String> data = new HashMap<String, String> ();
        long maxLevel = -1;
        final Set<String> calculationMethods = new HashSet<String> ();
        for ( final StorageChannelMetaData metaData : metaDatas )
        {
            // set configuration id
            if ( !data.containsKey ( Configuration.MANAGER_CONFIGURATION_ID ) )
            {
                data.put ( Configuration.MANAGER_CONFIGURATION_ID, metaData.getConfigurationId () );
            }

            // check calculation method
            final CalculationMethod calculationMethod = metaData.getCalculationMethod ();
            if ( calculationMethod == CalculationMethod.UNKNOWN )
            {
                final String message = String.format ( "unknown calculation method specified in metadata (%s)", metaData );
                logger.error ( message );
                throw new Exception ( message );
            }
            if ( calculationMethod != CalculationMethod.NATIVE )
            {
                calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ) );
            }

            // get detail level
            final long detailLevelId = metaData.getDetailLevelId ();
            maxLevel = Math.max ( maxLevel, detailLevelId );

            // check detail level id
            if ( detailLevelId < 0 )
            {
                final String message = String.format ( "invalid detail level id specified in metadata (%s)", metaData );
                logger.error ( message );
                throw new Exception ( message );
            }

            // set fragments time span
            final String fragmentTimeSpanKey = Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + detailLevelId;
            if ( !data.containsKey ( fragmentTimeSpanKey ) )
            {
                final long timespan = metaData.getEndTime () - metaData.getStartTime ();
                if ( timespan < 1 )
                {
                    final String message = String.format ( "invalid timespan specified in metadata (%s)", metaData );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                data.put ( fragmentTimeSpanKey, Conversions.encodeTimeSpan ( timespan ) );
            }

            // set proposed data age per level
            final String proposedDataAgeKey = Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + detailLevelId;
            final long proposedDataAge = metaData.getProposedDataAge ();
            if ( !data.containsKey ( proposedDataAgeKey ) )
            {
                data.put ( proposedDataAgeKey, Conversions.encodeTimeSpan ( proposedDataAge ) );
            }

            // set accepted future time per level
            final String acceptedTimeDeltaKey = Configuration.ACCEPTED_TIME_DELTA_KEY;
            if ( !data.containsKey ( acceptedTimeDeltaKey ) )
            {
                final long acceptedTimeDelta = metaData.getAcceptedTimeDelta ();
                if ( acceptedTimeDelta < 1 )
                {
                    logger.warn ( "accepted delta time not specified. value must be > 0" );
                }
                data.put ( acceptedTimeDeltaKey, Conversions.encodeTimeSpan ( acceptedTimeDelta ) );
            }

            // set proposed compression time span per level if calculation method is not NATIVE
            if ( calculationMethod != CalculationMethod.NATIVE )
            {
                final String compressionTimeSpanKey = Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + detailLevelId;
                if ( !data.containsKey ( compressionTimeSpanKey ) )
                {
                    // set compression time span per detail level
                    final long[] calculationMethodParameters = metaData.getCalculationMethodParameters ();
                    if ( calculationMethodParameters.length < 1 )
                    {
                        final String message = String.format ( "no calculation methods set (%s)!", metaData );
                        logger.error ( message );
                        throw new Exception ( message );
                    }
                    final long compressionTimeSpan = calculationMethodParameters[0];
                    final String compressionTimeSpanAsString = Conversions.encodeTimeSpan ( compressionTimeSpan );
                    if ( compressionTimeSpan > proposedDataAge )
                    {
                        logger.warn ( String.format ( "invalid compression time span set within calculation method parameters (%s)! compression time span must be at least the value of proposed data age. value of proposed data age will be adapted to %s", metaData, compressionTimeSpanAsString ) );
                        data.put ( proposedDataAgeKey, compressionTimeSpanAsString );
                    }
                    data.put ( compressionTimeSpanKey, compressionTimeSpanAsString );
                }
            }
            else
            {
                data.put ( Configuration.DATA_TYPE_KEY, DataType.convertDataTypeToShortString ( metaData.getDataType () ) );
            }
        }

        // add default calculation methods if none are available
        if ( calculationMethods.isEmpty () )
        {
            calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.AVERAGE ) );
            calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.MINIMUM ) );
            calculationMethods.add ( CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.MAXIMUM ) );
        }

        // assure that all calculated data has been set
        if ( DataType.convertShortStringToDataType ( data.get ( Configuration.DATA_TYPE_KEY ) ) == DataType.UNKNOWN )
        {
            final String message = String.format ( "no data type specified for " + CalculationMethod.convertCalculationMethodToString ( CalculationMethod.NATIVE ) + " calculation method for configuration '%s'", configuration.getId () );
            logger.error ( message );
            throw new Exception ( message );
        }

        // set common data
        //        data.put ( Configuration.CALCULATION_METHODS, StringHelper.join ( calculationMethods, LIST_SEPARATOR ) );
        data.put ( Configuration.MAX_COMPRESSION_LEVEL, "" + maxLevel );

        // the configuration is now complete
        configuration.setData ( data );
        return configuration;
    }

    /**
     * This method returns all calculation methods that are defined within the passed configuration object without the NATIVE calculation method.
     * @param configuration configuration object that has to be evaluated
     * @return all calculation methods that are defined within the passed configuration object without the NATIVE calculation method
     */
    public static CalculationMethod[] getCalculationMethods ( final Configuration configuration )
    {
        final Set<CalculationMethod> calculationMethods = new HashSet<CalculationMethod> ();
        final Map<String, String> data = configuration.getData ();
        if ( data != null )
        {
            final String calculationMethodsValue = data.get ( Configuration.CALCULATION_METHODS );
            if ( ( calculationMethodsValue != null ) && ( calculationMethodsValue.trim ().length () != 0 ) )
            {
                for ( final String s : calculationMethodsValue.split ( LIST_SEPARATOR ) )
                {
                    calculationMethods.add ( CalculationMethod.convertShortStringToCalculationMethod ( s.trim () ) );
                }
            }
            calculationMethods.remove ( CalculationMethod.NATIVE );
        }
        if ( calculationMethods.isEmpty () )
        {
            calculationMethods.add ( CalculationMethod.AVERAGE );
            calculationMethods.add ( CalculationMethod.MINIMUM );
            calculationMethods.add ( CalculationMethod.MAXIMUM );
        }
        return calculationMethods.toArray ( EMPTY_CALCULATION_METHOD_ARRAY );
    }

    /**
     * This method creates meta data objects using the passed configuration as input.
     * @param configuration input for the meta data objects that have to be created
     * @return created meta data objects
     * @throws Exception if meta data objects could not be created due to missing input data
     */
    public static StorageChannelMetaData[] convertConfigurationToMetaDatas ( final Configuration configuration ) throws Exception
    {
        // assure that factory id matches the expected value
        final String configurationId = configuration.getId ();

        // assure that convertable data is availavle
        final Map<String, String> data = configuration.getData ();
        if ( data == null )
        {
            final String message = String.format ( "no data available in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // check detail levels
        final long maxDetailLevelId = Conversions.parseLong ( data.get ( Configuration.MAX_COMPRESSION_LEVEL ), 0 );
        if ( maxDetailLevelId < 0 )
        {
            final String message = String.format ( "invalid maximum compression level specified in configuration '%s'! value must be '0' or greater", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // check calculation methods
        final Set<CalculationMethod> calculationMethods = new HashSet<CalculationMethod> ( Arrays.asList ( getCalculationMethods ( configuration ) ) );
        if ( calculationMethods.contains ( CalculationMethod.UNKNOWN ) )
        {
            final String message = String.format ( "unknown calculation method specified in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // assure the calculation methods are specified if a compression level greater than 0 is set
        if ( calculationMethods.isEmpty () )
        {
            calculationMethods.add ( CalculationMethod.AVERAGE );
            calculationMethods.add ( CalculationMethod.MINIMUM );
            calculationMethods.add ( CalculationMethod.MAXIMUM );
        }

        // check native data type
        final DataType nativeDataType = DataType.convertShortStringToDataType ( data.get ( Configuration.DATA_TYPE_KEY ) );
        if ( nativeDataType == DataType.UNKNOWN )
        {
            final String message = String.format ( "unknown data type specified in configuration '%s'", configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create meta data for native value calculation
        final List<StorageChannelMetaData> metaDatas = new ArrayList<StorageChannelMetaData> ();
        final long now = System.currentTimeMillis ();
        long proposedDataAge = decodeTimeSpan ( data.get ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 0 ) );
        if ( proposedDataAge < 1 )
        {
            final String message = String.format ( "invalid proposed data age for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.NATIVE ), configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long acceptedTimeDelta = decodeTimeSpan ( data.get ( Configuration.ACCEPTED_TIME_DELTA_KEY ) );
        long fragmentTimespan = decodeTimeSpan ( data.get ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0 ) );
        if ( fragmentTimespan < 1 )
        {
            final String message = String.format ( "invalid fragment timespan for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( CalculationMethod.NATIVE ), configurationId );
            logger.error ( message );
            throw new Exception ( message );
        }
        long startTime = getFragmentStartTime ( now, fragmentTimespan );
        metaDatas.add ( new StorageChannelMetaData ( configurationId, CalculationMethod.NATIVE, new long[0], 0, startTime, startTime + fragmentTimespan, proposedDataAge, acceptedTimeDelta, nativeDataType ) );

        // create meta data for other calculation methods if required
        for ( long detailLevelId = 1; detailLevelId <= maxDetailLevelId; detailLevelId++ )
        {
            for ( final CalculationMethod calculationMethod : calculationMethods )
            {
                final long compressionTimeSpan = decodeTimeSpan ( data.get ( Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + detailLevelId ) );
                if ( compressionTimeSpan < 1 )
                {
                    final String message = String.format ( "invalid compression timespan specified in configuration '%s'", configurationId );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                proposedDataAge = decodeTimeSpan ( data.get ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + detailLevelId ) );
                if ( proposedDataAge < 1 )
                {
                    final String message = String.format ( "invalid proposed data age for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), configurationId );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                fragmentTimespan = decodeTimeSpan ( data.get ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + detailLevelId ) );
                if ( fragmentTimespan < 1 )
                {
                    final String message = String.format ( "invalid fragment timespan for calculation method '%s' specified in configuration '%s'", CalculationMethod.convertCalculationMethodToShortString ( calculationMethod ), configurationId );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                startTime = getFragmentStartTime ( now, fragmentTimespan );
                metaDatas.add ( new StorageChannelMetaData ( configurationId, calculationMethod, new long[] { compressionTimeSpan }, detailLevelId, startTime, startTime + fragmentTimespan, proposedDataAge, acceptedTimeDelta, nativeDataType ) );
            }
        }
        return metaDatas.toArray ( EMPTY_META_DATA_ARRAY );
    }

    /**
     * This method returns the start time of a back end fragment.
     * @param now current time
     * @param timespan time span of the back end fragment
     * @return start time of a back end fragment
     */
    public static long getFragmentStartTime ( final long now, final long timespan )
    {
        return now - ( now % timespan );
    }

    /**
     * This method parsed a text to a long value and returns the result.
     * @param value text to be parsed
     * @param defaultValue default value that will be returned if either the passed text is null or if the conversion fails
     * @return converted text of default value if conversion failed
     */
    public static long parseLong ( final String value, final long defaultValue )
    {
        if ( value == null )
        {
            return defaultValue;
        }
        try
        {
            return Long.parseLong ( value );
        }
        catch ( final Exception e )
        {
            return defaultValue;
        }
    }
}
