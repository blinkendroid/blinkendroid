package org.cbase.blinkendroid.network.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.util.Log;
/**
 * A multicast Reiciever Thread
 * WARNING: NSFW yet
 *
 */
public class RecieverThread extends Thread {

    @Override
    public void run() {
	try{
		 InetAddress group = InetAddress.getByName("228.5.6.7");
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
	}
	catch(Exception e){
	    Log.e("foo","",e);
	}
    }

}
