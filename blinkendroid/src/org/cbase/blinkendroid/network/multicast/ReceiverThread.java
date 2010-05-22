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

package org.cbase.blinkendroid.network.multicast;

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
	    final DatagramSocket s = new DatagramSocket(
		    Constants.BROADCAST_CLIENT_PORT);
	    byte[] buf;

	    while (running) {

		buf = new byte[512];
		final DatagramPacket packet = new DatagramPacket(buf,
			buf.length);
		s.receive(packet);
		Log.d(Constants.LOG_TAG, "received something via broadcast");
		final String receivedString = new String(packet.getData(), 0,
			packet.getLength(), "UTF-8");
		final String[] receivedParts = receivedString.split(" ");

		final int protocolVersion = Integer.parseInt(receivedParts[0]);
		if (protocolVersion <= Constants.BROADCAST_PROTOCOL_VERSION) {

		    if (receivedParts.length < 3
			    || !receivedParts[1]
				    .equals(Constants.SERVER_BROADCAST_COMMAND)) {
			continue;
		    }

		    final InetAddress address = packet.getAddress();
		    final String serverName = receivedParts[2];

		    notifyHandlers(protocolVersion, serverName, address
			    .getHostAddress());

		    Log.i(Constants.LOG_TAG, receivedString + " "
			    + packet.getAddress() + " Thread: "
			    + Thread.currentThread().getId());
		} else {
		    notifyHandlers(protocolVersion);
		}
	    }
	    s.close();
	    Log.i(Constants.LOG_TAG,
		    "Finished receiving broadcast packets. Thread: "
			    + Thread.currentThread().getId());
	} catch (SocketException e) {
	    Log.e(Constants.LOG_TAG, "Closing Receiver Socket: ", e);
	} catch (Exception e) {
	    Log.e(Constants.LOG_TAG, "", e);
	}
    }

    public void shutdown() {
	running = false;
	handlers.clear();
	interrupt();
	Log.d(Constants.LOG_TAG, "ReceiverThread: shutdown complete");
    }
}
