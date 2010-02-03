package org.openscada.osgi.equinox.console;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ConsoleServerImpl
{

    private final SocketAcceptor acceptor;

    public ConsoleServerImpl ( final int port ) throws IOException
    {
        this.acceptor = new NioSocketAcceptor ();
        this.acceptor.setReuseAddress ( true );

        final DefaultIoFilterChainBuilder chain = this.acceptor.getFilterChain ();

        final ProtocolCodecFilter filter = new ProtocolCodecFilter ( new StringEncoder ( Charset.forName ( "UTF-8" ) ), new TextLineDecoder ( Charset.forName ( "UTF-8" ) ) );
        chain.addLast ( "console", filter );

        // Bind
        this.acceptor.setHandler ( new ConsoleHandlerImpl () );
        this.acceptor.bind ( new InetSocketAddress ( port ) );

    }

    public void dispose ()
    {
        this.acceptor.dispose ();
    }

}
