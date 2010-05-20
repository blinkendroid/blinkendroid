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
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidProtocol.ConnectionClosedListener;

import android.util.Log;

public class BlinkendroidClient implements ICommandHandler,
	ConnectionClosedListener {

    private final String ip;
    private final int port;
    private Socket socket;
    private BlinkendroidProtocol protocol;
    private final AtomicReference<BlinkendroidListener> listenerRef = new AtomicReference<BlinkendroidListener>();

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
	protocol.setConnectionClosedListener(this);
    }

    public void shutdown() throws IOException {
	if (protocol != null) {
	    protocol.shutdown();
	    protocol = null;
	}
	if (socket != null) {
	    socket.close();
	    socket = null;
	}
    }

    public void connectionClosed() {
	try {
	    final BlinkendroidListener listener = listenerRef.get();
	    if (listener != null)
		listener.connectionLost();

	    shutdown();
	} catch (IOException x) {
	    Log
		    .w(Constants.LOG_TAG,
			    "exception after connection was closed", x);
	}
    }

    public void registerListener(final BlinkendroidListener listener) {

	boolean expected = listenerRef.compareAndSet(null, listener);
	if (!expected)
	    throw new IllegalStateException("can only register one listener");

	protocol.registerHandler(BlinkendroidProtocol.PROTOCOL_PLAYER, this);
    }

    public void unregisterListener(final BlinkendroidListener listener) {

	protocol.unregisterHandler(this);

	boolean expected = listenerRef.compareAndSet(listener, null);
	if (!expected)
	    throw new IllegalStateException("listener has not been registered");
    }

    public void handle(byte[] data) {
	Log.d(Constants.LOG_TAG, "BlinkendroidProtocolHandler received "
		+ new String(data));
	final BlinkendroidListener listener = listenerRef.get();
	if (listener != null) {
	    final String input = new String(data);
	    if (input.startsWith(BlinkendroidProtocol.COMMAND_PLAYER_TIME)) {
		listener.serverTime(Long.parseLong(input.substring(1)));
	    } else if (input.startsWith(BlinkendroidProtocol.COMMAND_CLIP)) {
		final StringTokenizer tokenizer = new StringTokenizer(input
			.substring(1), ",");
		final float startX = Float.parseFloat(tokenizer.nextToken());
		final float startY = Float.parseFloat(tokenizer.nextToken());
		final float endX = Float.parseFloat(tokenizer.nextToken());
		final float endY = Float.parseFloat(tokenizer.nextToken());
		listener.clip(startX, startY, endX, endY);
	    } else if (input.startsWith(BlinkendroidProtocol.COMMAND_PLAY)) {
		final StringTokenizer tokenizer = new StringTokenizer(input
			.substring(1), ",");
		final int x = Integer.parseInt(tokenizer.nextToken());
		final int y = Integer.parseInt(tokenizer.nextToken());
		final int resId = Integer.parseInt(tokenizer.nextToken());
		final long serverTime = Long.parseLong(tokenizer.nextToken());
		final long startTime = Long.parseLong(tokenizer.nextToken());
		listener.serverTime(serverTime);
		listener.play(resId, startTime);
	    } else if (input.startsWith(BlinkendroidProtocol.COMMAND_INIT)) {
		final int degrees = Integer.parseInt(input.substring(1));
		listener.arrow(2500, degrees);
	    }
	}
    }
}
