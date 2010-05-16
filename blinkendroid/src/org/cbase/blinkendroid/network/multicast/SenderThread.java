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
 * A multicast sender thread WARNING: NSFW yet
 * 
 */
public class SenderThread extends Thread {

    private String message;
    private InetAddress group;

    public SenderThread(String msg) {
	message = msg;
	try {
	    group = InetAddress.getByName("224.0.0.1");
	} catch (UnknownHostException e) {
	    Log.e(Constants.LOG_TAG, e.getMessage());
	    e.printStackTrace();
	}
    }

    @Override
    public void run() {
	try {
	    int i = 0;
	    MulticastSocket s = new MulticastSocket(6789);
	    s.joinGroup(group);

	    while (true) {
		String msg = message + " " + i;

		DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg
			.length(), group, 6789);
		s.send(hi);
		i++;
		Thread.currentThread().sleep(5000);
	    }
	} catch (Exception e) {
	    Log.e("foo", "", e);
	}
    }

}
