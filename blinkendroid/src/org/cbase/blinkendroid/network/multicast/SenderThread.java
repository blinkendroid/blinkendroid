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

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * A multicast sender that sends a server name to blinkendroid clients.
 */
public class SenderThread extends Thread {

    private String message;
    private InetAddress group;

    /**
     * Creates a new {@link SenderThread}
     * @param serverName The server's name.
     */
    public SenderThread(String serverName) {
	message = Constants.SERVER_MULTICAST_COMMAND + " " + serverName + " ";
	try {
	    group = InetAddress.getByName(Constants.MULTICAST_GROUP);
	} catch (UnknownHostException e) {
	    Log.e(Constants.LOG_TAG, e.getMessage());
	    e.printStackTrace();
	}
    }
    


    @Override
    public void run() {
	try {
	    MulticastSocket s = new MulticastSocket(
		    Constants.MULTICAST_SERVER_PORT);
	    s.joinGroup(group);

	    while (true) {
		DatagramPacket initPacket = new DatagramPacket(message
			.getBytes(), message.length(), group,
			Constants.MULTICAST_SERVER_PORT);
		s.send(initPacket);

		Log.i(Constants.LOG_TAG, "Multicasting: " + message);
		Thread.currentThread().sleep(5000);
	    }
	} catch (Exception e) {
	    Log.e(Constants.LOG_TAG, e.getMessage());
	}
    }

}
