/*
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

import android.util.Log;

/**
 * A multicast Reiciever Thread WARNING: NSFW yet
 * 
 */
public class ReceiverThread extends Thread {

    private InetAddress group;
    public ReceiverThread(InetAddress grp) {
	group = grp;
    }

    @Override
    public void run() {
	try {
	    MulticastSocket s = new MulticastSocket(6789);
	    s.joinGroup(group);
	    byte[] buf;

	    while (true) {

		// get their responses!
		buf = new byte[500];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		s.receive(recv);
		Log.i("foo", new String(recv.getData()));
		// OK, I'm done talking - leave the group...
		Thread.currentThread().sleep(1000);
	    }
	} catch (Exception e) {
	    Log.e("foo", "", e);
	}
    }
}