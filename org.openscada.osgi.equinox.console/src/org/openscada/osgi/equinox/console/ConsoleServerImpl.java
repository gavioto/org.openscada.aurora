/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
