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

package org.cbase.blinkendroid.network.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * Receives Server announcements
 */
public class ReceiverThread extends Thread {

    volatile private boolean running = true;
    private List<IServerHandler> handlers = Collections
	    .synchronizedList(new ArrayList<IServerHandler>());
    private DatagramSocket socket;

    /**
     * Adds a handler to the {@link ReceiverThread}.
     */
    public void addHandler(IServerHandler handler) {
	handlers.add(handler);
    }

    public void removeHandler(IServerHandler handler) {
	handlers.remove(handler);
    }

    /**
     * Notifies the registered handlers
     */
    private void notifyHandlers(final int protocolVersion, String serverName,
	    String serverIp) {
	for (IServerHandler h : handlers) {
	    h.foundServer(serverName, serverIp, protocolVersion);
	}
    }

    private void notifyHandlers(final int protocolVersion) {
	for (IServerHandler h : handlers) {
	    h.foundUnknownServer(protocolVersion);
	}
    }

    @Override
    public void run() {
	try {
	    socket = new DatagramSocket(Constants.BROADCAST_CLIENT_PORT);
	    socket.setReuseAddress(true);

	    while (running) {

		final byte[] buf = new byte[512];
		final DatagramPacket packet = new DatagramPacket(buf,
			buf.length);
		receive(packet);

		if (!running) // fast exit
		    break;

		final String receivedString = new String(packet.getData(), 0,
			packet.getLength(), "UTF-8");
		Log.d(Constants.LOG_TAG, "received via broadcast: '"
			+ receivedString + "'");
		final String[] receivedParts = receivedString.split(" ");
		System.out.println(receivedParts.length);

		final int protocolVersion = Integer.parseInt(receivedParts[0]);
		if (protocolVersion <= Constants.BROADCAST_PROTOCOL_VERSION) {

		    if (!receivedParts[1]
			    .equals(Constants.SERVER_BROADCAST_COMMAND)) {
			continue;
		    }

		    final InetAddress address = packet.getAddress();
		    final String serverName = receivedParts.length >= 3 ? receivedParts[2]
			    : "";

		    notifyHandlers(protocolVersion, serverName, address
			    .getHostAddress());

		    Log.i(Constants.LOG_TAG, receivedString + " "
			    + packet.getAddress() + " Thread: "
			    + Thread.currentThread().getId());
		} else {
		    notifyHandlers(protocolVersion);
		}
	    }
	    socket.close();
	    Log.d(Constants.LOG_TAG, "ReceiverThread: shutdown complete");
	} catch (final IOException x) {
	    Log.e(Constants.LOG_TAG, "problem receiving", x);
	}
    }

    private void receive(final DatagramPacket packet) throws IOException {
	try {
	    socket.receive(packet);
	} catch (final SocketException x) {
	    // swallow, this is expected when being interrupted by
	    // socket.close()
	}
    }

    public void shutdown() {
	Log.d(Constants.LOG_TAG, "ReceiverThread: initiating shutdown");
	running = false;
	handlers.clear();
	socket.close(); // interrupt
	try {
	    join();
	} catch (final InterruptedException x) {
	    throw new RuntimeException(x);
	}
    }
}
