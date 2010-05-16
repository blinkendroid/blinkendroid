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
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * A multicast Reciever Thread WARNING: NSFW yet
 * 
 */
public class ReceiverThread extends Thread {

    private InetAddress group;
    private boolean running = true;

    private HashMap<InetAddress, String> servers = new HashMap<InetAddress, String>();
    private ArrayList<IServerHandler> handlers = new ArrayList<IServerHandler>();

    public ReceiverThread() {
	try {
	    group = InetAddress.getByName(Constants.MULTICAST_GROUP);
	} catch (UnknownHostException e) {
	    Log.e(Constants.LOG_TAG, e.getMessage());
	    e.printStackTrace();
	}
    }

    public void addHandler(IServerHandler handler) {
	handlers.add(handler);
    }

    public void removeHandler(IServerHandler handler) {
	handlers.remove(handler);
    }

    public void notifyHandlers(String serverName, String serverIp) {
	for (IServerHandler h : handlers) {
	    h.foundServer(serverName, serverIp);
	}
    }

    @Override
    public void run() {
	try {
	    MulticastSocket s = new MulticastSocket(
		    Constants.MULTICAST_SERVER_PORT);
	    s.joinGroup(group);
	    byte[] buf;

	    while (running) {

		buf = new byte[500];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		s.receive(recv);
		Log.d(Constants.LOG_TAG, "received something via mulitcast");
		String[] receivedData = new String(recv.getData()).split(" ");

		if (receivedData.length != 3
			|| !receivedData[0]
				.equals(Constants.SERVER_MULTICAST_COMMAND)) {
		    continue;
		}
		InetAddress address = recv.getAddress();
		String serverName = receivedData[1];

		if (!servers.containsKey(address)) {
		    servers.put(address, serverName);
		    notifyHandlers(serverName, address.getHostAddress());
		}

		Log.i(Constants.LOG_TAG, receivedData.toString() + " "
			+ recv.getAddress());
	    }
	} catch (Exception e) {
	    Log.e("foo", "", e);
	}
    }
    
    public void shutdown() {
	running = false;
	interrupt();
    }
}