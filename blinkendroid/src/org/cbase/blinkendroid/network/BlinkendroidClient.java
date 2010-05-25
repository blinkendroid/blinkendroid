/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.cbase.blinkendroid.Constants;

public class BlinkendroidClient extends Thread {

    private final InetSocketAddress socketAddress;
    private final BlinkendroidListener listener;
    private BlinkendroidClientProtocol protocol;
    
    public BlinkendroidClient(final InetSocketAddress socketAddress,
	    final BlinkendroidListener listener) {
	this.socketAddress = socketAddress;
	this.listener = listener;
    }

    @Override
    public void run() {
	System.out.println("trying to connect to server: " + socketAddress);
	try {
	    final Socket socket = new Socket();
	    long t	=	System.currentTimeMillis();
	    socket.connect(socketAddress,
		    Constants.SERVER_SOCKET_CONNECT_TIMEOUT);
	    protocol = new BlinkendroidClientProtocol(socket, listener);
	    System.out.println("connected "+(System.currentTimeMillis()-t));
	   
	} catch (final IOException x) {
	    System.out.println("connection failed");
	    x.printStackTrace();
	    listener.connectionFailed(x.getClass().getName() + ": "
		    + x.getMessage());
	}
    }
    
    public void shutdown() {
	if(null!=protocol)
	    protocol.shutdown();
	System.out.println("client shutdown completed");
    }

    public void locateMe() {
	// TODO Auto-generated method stub
	
    }
}
