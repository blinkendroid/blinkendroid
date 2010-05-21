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

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * A multicast sender that sends a server name to blinkendroid clients.
 */
public class SenderThread extends Thread {

    private String message;
    private InetAddress group;
    volatile private boolean running = true;
    private DatagramSocket socket;

    /**
     * Creates a new {@link SenderThread}
     * @param serverName The server's name.
     */
    public SenderThread(String serverName) {
	message = Constants.SERVER_BROADCAST_COMMAND + " " + serverName + " ";
    }
    


    @Override
    public void run() {
	try {
	    socket = new DatagramSocket(Constants.BROADCAST_SERVER_PORT);
	    group = InetAddress.getByName("255.255.255.255");
	    Log.i(Constants.LOG_TAG, "Server ip: " + group.toString());

	    while (running) {
		DatagramPacket initPacket = new DatagramPacket(message
			.getBytes(), message.length(), group,
			Constants.BROADCAST_CLIENT_PORT);
		socket.send(initPacket);

		Log.d(Constants.LOG_TAG, "Broadcasting: " + message);
		Thread.currentThread().sleep(5000);
	    }
	} catch (InterruptedException ie) {
	    Log.d(Constants.LOG_TAG, "Stopping SenderThread: ", ie);
	} catch (SocketException e) {
	    Log.e(Constants.LOG_TAG, "Closing SenderSocket: ", e);
	} catch (Exception e) {
	    Log.e(Constants.LOG_TAG, "Oooops: ", e);
	}
    }
    
    public void shutdown() {
	running = false;
	socket.close();
	interrupt();
    }
}
