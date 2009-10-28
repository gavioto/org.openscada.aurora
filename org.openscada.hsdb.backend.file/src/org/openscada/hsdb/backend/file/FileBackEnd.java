package org.openscada.hsdb.backend.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for storing and retrieving data in a file using java.io.RandomAccessFile.
 * @author Ludwig Straub
 */
public class FileBackEnd implements BackEnd
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( FileBackEnd.class );

    /** Empty byte array. */
    private final static byte[] emptyByteArray = new byte[0];

    /** Seed value for the parity calculation logic of data records. */
    private final static byte LRC_SEED = 0x5a;

    /** Unique marker identifying file types that can be handled via this class. */
    private final static long FILE_MARKER = 0x0a2d04b20b580ca9L;

    /** Size of one data record in the file. */
    private final static int RECORD_BLOCK_SIZE = 8 + 8 + 8 + 8 + 8 + 1;

    /** Maximum size of buffer when copying data within a file. */
    private final static int MAX_COPY_BUFFER_FILL_SIZE = 1024 * 1024;

    /** Version of file format. */
    private final static long FILE_VERSION = 1L;

    /** Encoder that will be used to store the configuration id within the file header. */
    private final CharsetEncoder charEncoder = Charset.forName ( "utf-8" ).newEncoder ();

    /** Decoder that will be used to extract the configuration id from the file header. */
    private final CharsetDecoder charDecoder = Charset.forName ( "utf-8" ).newDecoder ();

    /** Name of the file that is used to store data. */
    private final String fileName;

    /** Flag indicating whether the file connection should be kept open while the state of the instance is initialized or not. */
    private final boolean keepUpenWhileInitialized;

    /** Metadata of the storage channel. */
    private StorageChannelMetaData metaData;

    /** Open file or null, if currently no file is open. */
    private RandomAccessFile randomAccessFile;

    /** Flag indicating whether the file currently is open in write more or in read only more. */
    private boolean openInWriteMode;

    /** Offset within the file where the header has ended and real data starts. */
    private long dataOffset;

    /** Flag indicating whether the instance has been initialized or not. */
    private boolean initialized;

    /**
     * Constructor expecting the configuration of the file backend.
     * @param fileName name of the existing file that is used to store data
     * @param keepUpenWhileInitialized true, if the file connection should be kept open while the state of the instance is initialized, otherwise false
     */
    public FileBackEnd ( final String fileName, final boolean keepUpenWhileInitialized )
    {
        this.fileName = fileName;
        this.keepUpenWhileInitialized = keepUpenWhileInitialized;
        metaData = null;
        openInWriteMode = false;
        initialized = false;
        if ( ( fileName == null ) || ( fileName.trim ().length () == 0 ) )
        {
            throw new IllegalArgumentException ( "invalid filename passed via configuration" );
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#create
     */
    public synchronized void create ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        // assure that a valid object has been passed
        if ( storageChannelMetaData == null )
        {
            final String message = String.format ( "invalid StorageChannelMetaData object passed for file '%s'!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // extract configuration values
        final String configurationId = storageChannelMetaData.getConfigurationId ();
        final byte[] configurationIdBytes = encodeToBytes ( configurationId );
        final long calculationMethodId = CalculationMethod.convertCalculationMethodToLong ( storageChannelMetaData.getCalculationMethod () );
        final long[] calculationMethodParameters = storageChannelMetaData.getCalculationMethodParameters ();
        final long detailLevelId = storageChannelMetaData.getDetailLevelId ();
        final long startTime = storageChannelMetaData.getStartTime ();
        final long endTime = storageChannelMetaData.getEndTime ();
        final long proposedDataAge = storageChannelMetaData.getProposedDataAge ();
        final long acceptedFutureTime = storageChannelMetaData.getAcceptedFutureTime ();
        final long dataType = DataType.convertDataTypeToLong ( storageChannelMetaData.getDataType () );

        // validate input data
        if ( configurationId == null )
        {
            final String message = String.format ( "invalid configuration id specified for file '%s'!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        if ( startTime >= endTime )
        {
            final String message = String.format ( "invalid timespan specified for file '%s'! (startTime >= endTime)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }

        // create new file including folder
        final File file = new File ( fileName );
        final File parent = file.getParentFile ();
        if ( parent != null )
        {
            parent.mkdirs ();
        }
        logger.info ( String.format ( "creating file '%s'", fileName ) );
        if ( !file.createNewFile () )
        {
            final String message = String.format ( "file '%s' could not be created. please verify the access rights and make sure that no file with the given name already exists. (file exists=%s)", fileName, file.exists () );
            logger.error ( message );
            throw new Exception ( message );
        }

        // write standardized file header to file
        openConnection ( true );
        randomAccessFile.seek ( 0L );
        final long dataOffset = ( 11 + calculationMethodParameters.length ) * 8 + configurationIdBytes.length + 4;
        randomAccessFile.writeLong ( FILE_MARKER );
        randomAccessFile.writeLong ( dataOffset );
        randomAccessFile.writeLong ( FILE_VERSION );
        randomAccessFile.writeLong ( detailLevelId );
        randomAccessFile.writeLong ( startTime );
        randomAccessFile.writeLong ( endTime );
        randomAccessFile.writeLong ( proposedDataAge );
        randomAccessFile.writeLong ( acceptedFutureTime );
        randomAccessFile.writeLong ( dataType );
        randomAccessFile.writeLong ( calculationMethodId );
        randomAccessFile.writeInt ( calculationMethodParameters.length );
        randomAccessFile.writeInt ( configurationIdBytes.length );
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            randomAccessFile.writeLong ( calculationMethodParameters[i] );
        }
        randomAccessFile.write ( configurationIdBytes );
        final CRC32 crc32 = new CRC32 ();
        final ByteBuffer byteBuffer = ByteBuffer.allocate ( (int)dataOffset - 12 );
        byteBuffer.putLong ( dataOffset );
        byteBuffer.putLong ( FILE_VERSION );
        byteBuffer.putLong ( detailLevelId );
        byteBuffer.putLong ( startTime );
        byteBuffer.putLong ( endTime );
        byteBuffer.putLong ( proposedDataAge );
        byteBuffer.putLong ( acceptedFutureTime );
        byteBuffer.putLong ( dataType );
        byteBuffer.putLong ( calculationMethodId );
        byteBuffer.putInt ( calculationMethodParameters.length );
        byteBuffer.putInt ( configurationIdBytes.length );
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            byteBuffer.putLong ( calculationMethodParameters[i] );
        }
        byteBuffer.put ( configurationIdBytes );
        crc32.update ( byteBuffer.array () );
        final int checksum = (int)crc32.getValue ();
        randomAccessFile.writeInt ( checksum );
        logger.debug ( "checksum (write header): " + checksum );
        closeIfRequired ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#initialize
     */
    public synchronized void initialize ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        metaData = null;
        initialized = true;
        getMetaData ();
        closeIfRequired ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        assureInitialized ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        assureInitialized ();
        if ( metaData == null )
        {
            openConnection ( false );
            metaData = extractMetaData ();
            closeIfRequired ();
        }
        return metaData;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#isTimeSpanConstant
     */
    public synchronized boolean isTimeSpanConstant ()
    {
        return true;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#deinitialize
     */
    public synchronized void deinitialize () throws Exception
    {
        closeConnection ();
        initialized = false;
        metaData = null;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#delete
     */
    public synchronized void delete () throws Exception
    {
        // assure that any previous open connection is closed
        closeConnection ();

        // delete old file if any exists
        final File file = new File ( fileName );
        if ( file.exists () )
        {
            logger.info ( String.format ( "deleting existing file '%s'...", fileName ) );
            if ( file.delete () )
            {
                logger.info ( String.format ( "deletion of file '%s' successful", fileName ) );
            }
            else
            {
                logger.warn ( String.format ( "deletion of file '%s' failed", fileName ) );
            }
        }
    }

    /**
     * This method assures that the instance is initialized.
     * @throws Exception if the instance is not initialized
     */
    private void assureInitialized () throws Exception
    {
        if ( !initialized )
        {
            final String message = String.format ( "back end (%s) is not properly initialized!", metaData );
            logger.error ( message );
            throw new Exception ( message );
        }
    }

    /**
     * This method extracts the metadata from the file.
     * It is assumed that the file is already open.
     * @return extracted metadata
     * @throws Exception if the file cannot be read or if the file version or format is invalid
     */
    private StorageChannelMetaData extractMetaData () throws Exception
    {
        randomAccessFile.seek ( 0L );
        final long fileSize = randomAccessFile.length ();
        if ( fileSize < 16 )
        {
            final String message = String.format ( "file '%s' is of invalid format! (too small)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long fileMarker = randomAccessFile.readLong ();
        if ( fileMarker != FILE_MARKER )
        {
            final String message = String.format ( "file '%s' is of invalid format! (invalid marker)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        dataOffset = randomAccessFile.readLong ();
        if ( fileSize < dataOffset )
        {
            final String message = String.format ( "file '%s' is of invalid format! (invalid header)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long version = randomAccessFile.readLong ();
        if ( version != FILE_VERSION )
        {
            final String message = String.format ( "file '%s' is of invalid format! (wrong version)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long detailLevelId = randomAccessFile.readLong ();
        final long startTime = randomAccessFile.readLong ();
        final long endTime = randomAccessFile.readLong ();
        if ( startTime >= endTime )
        {
            final String message = String.format ( "file '%s' has invalid timespan specified! (startTime >= endTime)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long proposedDataAge = randomAccessFile.readLong ();
        final long acceptedFutureTime = randomAccessFile.readLong ();
        final long dataType = randomAccessFile.readLong ();
        final long calculationMethodId = randomAccessFile.readLong ();
        final int calculationMethodParameterCountSize = randomAccessFile.readInt ();
        final int configurationIdSize = randomAccessFile.readInt ();
        if ( ( dataOffset - randomAccessFile.getFilePointer () - 4 - configurationIdSize ) != ( calculationMethodParameterCountSize * 8 ) )
        {
            final String message = String.format ( "file '%s' is of invalid format! (invalid count of calculation method parameters)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final long[] calculationMethodParameters = new long[calculationMethodParameterCountSize];
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            calculationMethodParameters[i] = randomAccessFile.readLong ();
        }
        if ( ( dataOffset - randomAccessFile.getFilePointer () - 4 ) != configurationIdSize )
        {
            final String message = String.format ( "file '%s' is of invalid format! (invalid configuration id)", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        final byte[] configurationIdBytes = new byte[configurationIdSize];
        randomAccessFile.readFully ( configurationIdBytes );
        final String configurationId = decodeStringFromBytes ( configurationIdBytes );
        final CRC32 crc32 = new CRC32 ();
        final ByteBuffer byteBuffer = ByteBuffer.allocate ( (int)dataOffset - 12 );
        byteBuffer.putLong ( dataOffset );
        byteBuffer.putLong ( version );
        byteBuffer.putLong ( detailLevelId );
        byteBuffer.putLong ( startTime );
        byteBuffer.putLong ( endTime );
        byteBuffer.putLong ( proposedDataAge );
        byteBuffer.putLong ( acceptedFutureTime );
        byteBuffer.putLong ( dataType );
        byteBuffer.putLong ( calculationMethodId );
        byteBuffer.putInt ( calculationMethodParameters.length );
        byteBuffer.putInt ( configurationIdSize );
        for ( int i = 0; i < calculationMethodParameters.length; i++ )
        {
            byteBuffer.putLong ( calculationMethodParameters[i] );
        }
        byteBuffer.put ( configurationIdBytes );
        crc32.update ( byteBuffer.array () );
        final int checksum = (int)crc32.getValue ();
        if ( randomAccessFile.readInt () != checksum )
        {
            final String message = String.format ( "file '%s' has a corrupt header!", fileName );
            logger.error ( message );
            throw new Exception ( message );
        }
        logger.debug ( "checksum (read header): " + checksum );

        // create a wrapper object for returning the retrieved data
        return new StorageChannelMetaData ( configurationId, CalculationMethod.convertLongToCalculationMethod ( calculationMethodId ), calculationMethodParameters, detailLevelId, startTime, endTime, proposedDataAge, acceptedFutureTime, DataType.convertLongToDataType ( dataType ) );
    }

    /**
     * This method assures that a valid connection to the file exists.
     * The position of the file pointer is not defined.
     * @param allowWrite flag indicating whether the connection should have write privileges or not
     * @throws Exception in case of problems
     */
    private void openConnection ( final boolean allowWrite ) throws Exception
    {
        // close connection if a writable file is required and the current connection only supports reading
        if ( ( randomAccessFile != null ) && allowWrite && !openInWriteMode )
        {
            closeConnection ();
        }

        // if file already is open, nothing has to be done
        if ( randomAccessFile == null )
        {
            try
            {
                // open new connection
                logger.debug ( String.format ( "OPENING file '%s' successful", fileName ) );
                final File file = new File ( fileName );
                randomAccessFile = new RandomAccessFile ( file, allowWrite ? "rw" : "r" );
                openInWriteMode = allowWrite;
            }
            catch ( final IOException e )
            {
                // close connection in case of problems
                final String message = String.format ( "file '%s' could not be opened", fileName );
                logger.error ( message, e );
                closeConnection ();
                throw new Exception ( message, e );
            }
        }
    }

    /**
     * This method closes any existing connections.
     */
    private void closeConnection ()
    {
        if ( randomAccessFile != null )
        {
            try
            {
                logger.debug ( String.format ( "closing file '%s' successful", fileName ) );
                randomAccessFile.close ();
            }
            catch ( final IOException e )
            {
                logger.warn ( String.format ( "file '%s' could not be closed", fileName ) );
            }
            randomAccessFile = null;
        }
    }

    /**
     * This method reads a long value from the file.
     * It is assumed that an open connection exists.
     * @param position position within the file where the data has to be read
     * @return read long value
     * @throws Exception in case of read problems or file corruption
     */
    private LongValue readLongValue ( final long position ) throws Exception
    {
        if ( randomAccessFile.getFilePointer () != position )
        {
            randomAccessFile.seek ( position );
        }
        final long time = randomAccessFile.readLong ();
        final long qualityIndicatorAsLong = randomAccessFile.readLong ();
        final long manualIndicatorAsLong = randomAccessFile.readLong ();
        final double qualityIndicator = Double.longBitsToDouble ( qualityIndicatorAsLong );
        final double manualIndicator = Double.longBitsToDouble ( manualIndicatorAsLong );
        final long baseValueCount = randomAccessFile.readLong ();
        final long value = randomAccessFile.readLong ();
        final ByteBuffer byteBuffer = ByteBuffer.allocate ( RECORD_BLOCK_SIZE - 1 );
        byteBuffer.putLong ( time );
        byteBuffer.putLong ( qualityIndicatorAsLong );
        byteBuffer.putLong ( manualIndicatorAsLong );
        byteBuffer.putLong ( baseValueCount );
        byteBuffer.putLong ( value );
        final byte checksum = calculateLrcParity ( byteBuffer.array () );
        if ( randomAccessFile.readByte () != checksum )
        {
            final String message = String.format ( "file '%s' is corrupt! invalid timestamp at %x", fileName, time );
            logger.error ( message );
            throw new Exception ( message );
        }
        logger.debug ( "checksum (read data): " + checksum );
        return new LongValue ( time, qualityIndicator, manualIndicator, baseValueCount, value );
    }

    /**
     * This method returns the offset within the file where the new data should be stored.
     * It is assumed that an open connection exists.
     * Since the data most likely has to be appended to the file, the search will be performed starting at the end of the file.
     * @param time time for which the perfect storing position has to be retrieved
     * @return perfect storing position of the passed long value
     * @throws Exception in case of read problems or file corruption
     */
    private long getInsertionPoint ( final long time ) throws Exception
    {
        long endSearch = randomAccessFile.length () - RECORD_BLOCK_SIZE;
        while ( endSearch >= dataOffset )
        {
            final LongValue existingLongValue = readLongValue ( endSearch );
            final long existingTime = existingLongValue.getTime ();
            if ( time > existingTime )
            {
                return endSearch + RECORD_BLOCK_SIZE;
            }
            else if ( time == existingTime )
            {
                return endSearch;
            }
            endSearch -= RECORD_BLOCK_SIZE;
        }
        return dataOffset;
    }

    /**
     * This method returns the offset within the file where data can be read.
     * The first data that is read will be the data with exactly the specified time or the last data before if no exactly matching data can be found.
     * It is assumed that an open connection exists.
     * A binary search is applied in order to find the correct position within the file.
     * @param startTime time for which the perfect storing position has to be retrieved
     * @return perfect storing position of the passed long value
     * @throws Exception in case of read problems or file corruption
     */
    private long getFirstEntryPosition ( final long startTime ) throws Exception
    {
        // ignore incomplete data at file end
        long fileSize = randomAccessFile.length ();
        final long incompleteData = ( fileSize - dataOffset ) % RECORD_BLOCK_SIZE;
        if ( incompleteData > 0 )
        {
            fileSize -= incompleteData;
        }

        // check for bounds to optimize search
        if ( metaData.getEndTime () < startTime )
        {
            return fileSize > dataOffset ? fileSize - RECORD_BLOCK_SIZE : fileSize;
        }
        if ( metaData.getStartTime () > startTime )
        {
            return dataOffset;
        }

        // prepare data for real binary search
        long startSearch = 0;
        long endSearch = ( fileSize - dataOffset ) / RECORD_BLOCK_SIZE;
        if ( startSearch == endSearch )
        {
            return dataOffset;
        }

        // perform real binary search
        long midTime = startSearch;
        long midSearch = startSearch;
        long filePointer = dataOffset;
        while ( startSearch < endSearch )
        {
            midSearch = ( startSearch + endSearch ) / 2;
            filePointer = ( midSearch * RECORD_BLOCK_SIZE ) + dataOffset;
            midTime = readLongValue ( filePointer ).getTime ();
            if ( midTime < startTime )
            {
                startSearch = midSearch + 1;
            }
            else if ( midTime > startTime )
            {
                endSearch = midSearch - 1;
            }
            else
            {
                return filePointer;
            }
        }
        long resultIndex = Math.max ( 0, Math.min ( startSearch, endSearch ) );
        filePointer = ( resultIndex * RECORD_BLOCK_SIZE ) + dataOffset;
        if ( filePointer < fileSize )
        {
            midTime = readLongValue ( filePointer ).getTime ();
            if ( midTime > startTime )
            {
                resultIndex--;
            }
        }
        final long result = ( Math.max ( 0, resultIndex ) * RECORD_BLOCK_SIZE ) + dataOffset;
        return ( result > dataOffset ) && ( result == fileSize ) ? result - RECORD_BLOCK_SIZE : result;
    }

    /**
     * This method stores the passed data in the file.
     * It is assumed that a valid connection exists.
     * Only data that matches the specified time span will be processed.
     * @param longValue data that has to be stored.
     * @throws Exception in case of problems
     */
    private void writeLongValue ( final LongValue longValue ) throws Exception
    {
        // assure that the passed value matches the timespan of the metadata
        final long time = longValue.getTime ();
        if ( ( time < metaData.getStartTime () ) || ( time >= metaData.getEndTime () ) )
        {
            return;
        }

        // calculate insertion point of new data
        final long insertionPoint = getInsertionPoint ( longValue.getTime () );
        long endCopy = randomAccessFile.length ();

        // make room for new data if data cannot be appended at the end or existing data has to be overwritten
        if ( ( insertionPoint != endCopy ) && ( readLongValue ( insertionPoint ).getTime () != time ) )
        {
            // move file content to create cap for new data
            final byte[] buffer = new byte[(int)Math.min ( MAX_COPY_BUFFER_FILL_SIZE, endCopy - insertionPoint )];
            long startCopy = Math.max ( endCopy - buffer.length, insertionPoint );
            while ( startCopy < endCopy )
            {
                final int bufferFillSize = (int) ( endCopy - startCopy );
                randomAccessFile.seek ( startCopy );
                randomAccessFile.read ( buffer, 0, bufferFillSize );
                randomAccessFile.seek ( startCopy + RECORD_BLOCK_SIZE );
                randomAccessFile.write ( buffer, 0, bufferFillSize );
                endCopy = startCopy;
                startCopy = Math.max ( insertionPoint, startCopy - bufferFillSize );
            }
        }

        // set file pointer to correct insertion position
        randomAccessFile.seek ( insertionPoint );

        // prepare values to write
        final long qualityIndicator = Double.doubleToLongBits ( longValue.getQualityIndicator () );
        final long manualIndicator = Double.doubleToLongBits ( longValue.getManualIndicator () );
        final long baseValueCount = longValue.getBaseValueCount ();
        final long value = longValue.getValue ();
        final ByteBuffer byteBuffer = ByteBuffer.allocate ( RECORD_BLOCK_SIZE - 1 );
        byteBuffer.putLong ( time );
        byteBuffer.putLong ( qualityIndicator );
        byteBuffer.putLong ( manualIndicator );
        byteBuffer.putLong ( baseValueCount );
        byteBuffer.putLong ( value );
        final byte checksum = calculateLrcParity ( byteBuffer.array () );

        // write values
        randomAccessFile.writeLong ( time );
        randomAccessFile.writeLong ( qualityIndicator );
        randomAccessFile.writeLong ( manualIndicator );
        randomAccessFile.writeLong ( baseValueCount );
        randomAccessFile.writeLong ( value );
        randomAccessFile.writeByte ( checksum );
        logger.debug ( "checksum (write data): " + checksum );
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        assureInitialized ();
        if ( longValue != null )
        {
            try
            {
                // assure that write operation can be performed
                openConnection ( true );

                // write data to file
                writeLongValue ( longValue );
            }
            finally
            {
                closeIfRequired ();
            }
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        assureInitialized ();
        if ( longValues != null )
        {
            try
            {
                // assure that write operation can be performed
                openConnection ( true );

                // write data to file
                for ( int i = 0; i < longValues.length; i++ )
                {
                    writeLongValue ( longValues[i] );
                }
            }
            finally
            {
                closeIfRequired ();
            }
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        // assure that the current state is valid
        assureInitialized ();

        // assure that a valid timespan is passed
        if ( startTime >= endTime )
        {
            return EMPTY_LONGVALUE_ARRAY;
        }

        // perform search
        try
        {
            // assure that read operation can be performed
            openConnection ( false );

            // get data from file
            final long fileSize = randomAccessFile.length ();
            long startingPosition = getFirstEntryPosition ( startTime );
            final List<LongValue> longValues = new ArrayList<LongValue> ();
            while ( startingPosition + RECORD_BLOCK_SIZE <= fileSize )
            {
                final LongValue longValue = readLongValue ( startingPosition );
                if ( longValue.getTime () >= endTime )
                {
                    break;
                }
                longValues.add ( longValue );
                startingPosition += RECORD_BLOCK_SIZE;
            }
            return longValues.toArray ( EMPTY_LONGVALUE_ARRAY );
        }
        finally
        {
            closeIfRequired ();
        }
    }

    /**
     * This method encodes text so that it can be stored within a file.
     * @param data text to be encoded
     * @return encoded text as byte array
     */
    private byte[] encodeToBytes ( final String data )
    {
        if ( data == null )
        {
            return emptyByteArray;
        }
        synchronized ( this.charEncoder )
        {
            try
            {
                return charEncoder.encode ( CharBuffer.wrap ( data ) ).array ();
            }
            catch ( final CharacterCodingException e )
            {
                return data.getBytes ();
            }
        }
    }

    /**
     * This method decodes previously encoded text.
     * @param bytes text to be decoded
     * @return decoded text
     */
    private String decodeStringFromBytes ( final byte[] bytes )
    {
        if ( bytes == null )
        {
            return "";
        }

        try
        {
            return charDecoder.decode ( ByteBuffer.wrap ( bytes ) ).toString ().replaceAll ( "\u0000", "" );
        }
        catch ( final CharacterCodingException e )
        {
            return new String ( bytes );
        }
    }

    /**
     * This method closes the connection to the file if the connection should not be kept open until the instance is deinitialized.
     */
    private void closeIfRequired ()
    {
        if ( !keepUpenWhileInitialized )
        {
            closeConnection ();
        }
    }

    /**
     * This method calculates a parity value for the passed bytes.
     * @param bytes array of bytes for which a parity value has to be calculated
     * @return calculated parity value
     */
    private static Byte calculateLrcParity ( final byte[] bytes )
    {
        byte result = LRC_SEED;
        final int size = bytes.length;
        for ( int i = 0; i < size; i++ )
        {
            result ^= bytes[i];
        }
        return result;
    }
}
