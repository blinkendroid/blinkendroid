package org.cbase.blinkendroid.player;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.utils.ToastPoster;

import android.util.Log;

public class TimeClient extends Thread {
    private String ip;
    private boolean running=true;
    
    public TimeClient( String ip) {
    	this.ip = ip;
    }

    @Override
    public void run() {
	Socket socket;
	try {
		TimeClient.sleep(5000);
	    socket = new Socket(ip,4444);// 5556);
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

	    while(running){
	    	try {
				TimeClient.sleep(5000);
			} catch (InterruptedException e) {
			}
			long t=System.currentTimeMillis();
			Log.i(Constants.LOG_TAG,"TimeClient ping "+t);
		    out.write(Long.toString(t) + '\n');
		    out.flush();
	    }
	    out.close();
	    socket.close();
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
