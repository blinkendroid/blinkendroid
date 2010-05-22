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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import org.cbase.blinkendroid.Constants;

public class BlinkendroidClient extends Thread {

    volatile private boolean running = false;
    private final InetSocketAddress socketAddress;
    private final BlinkendroidListener listener;

    public BlinkendroidClient(final InetSocketAddress socketAddress,
	    final BlinkendroidListener listener) {
	this.socketAddress = socketAddress;
	this.listener = listener;
    }

    @Override
    public void run() {

	running = true;

	System.out.println("trying to connect to server: " + socketAddress);
	try {
	    final Socket socket = new Socket();
	    socket.connect(socketAddress,
		    Constants.SERVER_SOCKET_CONNECT_TIMEOUT);
	    System.out.println("connected");
	    final BufferedReader in = new BufferedReader(new InputStreamReader(
		    socket.getInputStream()));
	    listener.connectionOpened(socket.getRemoteSocketAddress());

	    while (running) {
		final String inputLine = in.readLine();
		if (!running) // fast exit
		    break;
		if (inputLine == null)
		    break;
		System.out.println("received: " + inputLine);
		handle(inputLine.substring(1));
	    }

	    System.out.println("closing connection");

	    listener.connectionClosed(socket.getRemoteSocketAddress());

	    in.close();
	    socket.close();
	} catch (final IOException x) {
	    System.out.println("connection failed");
	    x.printStackTrace();
	    listener.connectionFailed(x.getClass().getName() + ": "
		    + x.getMessage());
	}

	System.out.println("client thread ended normally");
    }

    private void handle(final String command) {
	/*
	System.out.println("received: " + command);
	if (listener != null) {
	    if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_PLAYER_TIME)) {
		listener.serverTime(Long.parseLong(command.substring(1)));
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_CLIP)) {
		final StringTokenizer tokenizer = new StringTokenizer(command
			.substring(1), ",");
		final float startX = Float.parseFloat(tokenizer.nextToken());
		final float startY = Float.parseFloat(tokenizer.nextToken());
		final float endX = Float.parseFloat(tokenizer.nextToken());
		final float endY = Float.parseFloat(tokenizer.nextToken());
		listener.clip(startX, startY, endX, endY);
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_PLAY)) {
		final StringTokenizer tokenizer = new StringTokenizer(command
			.substring(1), ",");
		final int x = Integer.parseInt(tokenizer.nextToken());
		final int y = Integer.parseInt(tokenizer.nextToken());
		final int resId = Integer.parseInt(tokenizer.nextToken());
		final long serverTime = Long.parseLong(tokenizer.nextToken());
		final long startTime = Long.parseLong(tokenizer.nextToken());
		listener.serverTime(serverTime);
		listener.play(x, y, resId, startTime);
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_INIT)) {
		final int degrees = Integer.parseInt(command.substring(1));
		listener.arrow(2500, degrees);
	    }
	}
	*/
    }

    public void shutdown() {
	running = false;
	interrupt();
	try {
	    join();
	} catch (final InterruptedException x) {
	    throw new RuntimeException(x);
	}
	System.out.println("shutdown completed");
    }
}
