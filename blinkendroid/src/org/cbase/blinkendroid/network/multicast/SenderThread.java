package org.cbase.blinkendroid.network.multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.util.Log;
/**
 * A multicast sender thread
 * WARNING: NSFW yet
 *
 */
public class SenderThread extends Thread {

    String message;
    public SenderThread(String msg) {
	message = msg;
    }
    @Override
    public void run() {
	try{
	int i = 0;
	 InetAddress group = InetAddress.getByName("228.5.6.7");
	 MulticastSocket s = new MulticastSocket(6789);
	 s.joinGroup(group);
	 
	while (true) {
	String msg = message + " " + i;

	 DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
	                             group, 6789);
	 s.send(hi);
	 i++;
	 Thread.currentThread().sleep(1000);
	}
	}
	catch(Exception e){
	    Log.e("foo","",e);
	}
    }

}
