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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * Receives Server announcements
 */
public class ReceiverThread extends Thread {

    private InetAddress group;
    private boolean running = true;

    private List<IServerHandler> handlers = Collections
	    .synchronizedList(new ArrayList<IServerHandler>());

    /**
     * Creates a {@link ReceiverThread}
     */
    public ReceiverThread() {
	try {
	    group = InetAddress.getByName(Constants.MULTICAST_GROUP);
	} catch (UnknownHostException e) {
	    Log.e(Constants.LOG_TAG, e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Adds a handler to the {@link ReceiverThread}.
     * 
     * @param handler
     */
    public void addHandler(IServerHandler handler) {
	handlers.add(handler);
    }

    public void removeHandler(IServerHandler handler) {
	handlers.remove(handler);
    }

    /**
     * Notifies the registered handlers
     * 
     * @param serverName
     * @param serverIp
     */
    private void notifyHandlers(String serverName, String serverIp) {
	for (IServerHandler h : handlers) {
	    h.foundServer(serverName, serverIp);
	}
    }

    @Override
    public void run() {
	try {
	    DatagramSocket s = new DatagramSocket(Constants.BROADCAST_CLIENT_PORT);
	    byte[] buf;

	    while (running) {

		buf = new byte[500];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		s.receive(recv);
		Log.d(Constants.LOG_TAG, "received something via broadcast");
		String[] receivedData = new String(recv.getData()).split(" ");

		if (receivedData.length != 3 || !receivedData[0]
				.equals(Constants.SERVER_BROADCAST_COMMAND)) {
		    continue;
		}
		InetAddress address = recv.getAddress();
		String serverName = receivedData[1];

		notifyHandlers(serverName, address.getHostAddress());

		Log.i(Constants.LOG_TAG, receivedData.toString() + " "
			+ recv.getAddress() + " Thread: "
			+ Thread.currentThread().getId());
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
	handlers.clear();
	running = false;
	interrupt();
	Log.d(Constants.LOG_TAG, "ReceiverThread: shutdown complete");
    }
}