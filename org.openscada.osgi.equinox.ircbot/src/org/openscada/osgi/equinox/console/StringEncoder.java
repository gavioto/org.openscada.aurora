package org.openscada.osgi.equinox.console;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.textline.LineDelimiter;

public class StringEncoder extends ProtocolEncoderAdapter
{

    private final AttributeKey ENCODER = new AttributeKey ( getClass (), "encoder" );

    private final Charset charset;

    private int maxLineLength = Integer.MAX_VALUE;

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and {@link LineDelimiter#UNIX} delimiter.
     */
    public StringEncoder ( final Charset charset )
    {
        if ( charset == null )
        {
            throw new NullPointerException ( "charset" );
        }
        this.charset = charset;
    }

    /**
     * Returns the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public int getMaxLineLength ()
    {
        return this.maxLineLength;
    }

    /**
     * Sets the allowed maximum size of the encoded line.
     * If the size of the encoded line exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     */
    public void setMaxLineLength ( final int maxLineLength )
    {
        if ( maxLineLength <= 0 )
        {
            throw new IllegalArgumentException ( "maxLineLength: " + maxLineLength );
        }

        this.maxLineLength = maxLineLength;
    }

    public void encode ( final IoSession session, final Object message, final ProtocolEncoderOutput out ) throws Exception
    {
        CharsetEncoder encoder = (CharsetEncoder)session.getAttribute ( this.ENCODER );
        if ( encoder == null )
        {
            encoder = this.charset.newEncoder ();
            session.setAttribute ( this.ENCODER, encoder );
        }

        final String value = message.toString ();
        final IoBuffer buf = IoBuffer.allocate ( value.length () ).setAutoExpand ( true );
        buf.putString ( value, encoder );
        if ( buf.position () > this.maxLineLength )
        {
            throw new IllegalArgumentException ( "Line length: " + buf.position () );
        }
        buf.flip ();
        out.write ( buf );
    }

    public void dispose () throws Exception
    {
        // Do nothing
    }

}
