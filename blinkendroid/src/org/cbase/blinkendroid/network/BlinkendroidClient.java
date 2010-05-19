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

import android.util.Log;

public class BlinkendroidClient {

    final private String ip;
    final private int port;
    private Socket socket;
    private BlinkendroidProtocol protocol;
    private BlinkendroidProtocolHandler protocolHandler;

    public BlinkendroidClient(final String ip, final int port)
	    throws IOException {
	this.ip = ip;
	this.port = port;
	connect();
    }

    private void connect() throws IOException {
	if (socket != null)
	    throw new IllegalStateException("already connected");

	Log.i(Constants.LOG_TAG, "trying to connect to server: '" + ip + "':"
		+ port);
	socket = new Socket();
	socket.connect(new InetSocketAddress(ip, port),
		Constants.SERVER_SOCKET_CONNECT_TIMEOUT);
	Log.i(Constants.LOG_TAG, "connected");
	protocol = new BlinkendroidProtocol(socket, false);
    }

    public void shutdown() throws IOException {
	if (null != protocol)
	    protocol.shutdown();
	if (socket != null) {
	    socket.close();
	    socket = null;
	}
    }

    public void registerListener(BlinkendroidListener listener) {
	protocolHandler = new BlinkendroidProtocolHandler(listener);
	protocol.registerHandler(BlinkendroidProtocol.PROTOCOL_PLAYER,
		protocolHandler);
	protocol.setConnectionClosedListener(listener);
    }

    public void unregisterListener(BlinkendroidListener listener) {
	protocol.unregisterHandler(protocolHandler);
    }
}
