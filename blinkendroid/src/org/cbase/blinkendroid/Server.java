package org.cbase.blinkendroid;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class Server extends Thread{
	private boolean running=false;
	private Blinkendroid blinkendroid;
	public Server(Blinkendroid blinkendroid) {
		this.blinkendroid =blinkendroid;
	}

	@Override
	public void run() {
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			Log.e(Blinkendroid.LOG_TAG, "Could not create Socket",e);
			return;
		}
		running=true;
		Log.i(Blinkendroid.LOG_TAG,"Server Thread started");
		while(running){
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
				new ConnectionThread(blinkendroid, clientSocket).start();	
			} catch (IOException e) {
				Log.e(Blinkendroid.LOG_TAG, "Could not accept",e);
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.e(Blinkendroid.LOG_TAG, "Could not close",e);
		}
		Log.i(Blinkendroid.LOG_TAG,"Server Thread closed");
	}

	public void end(){
		running=false;
		Log.i(Blinkendroid.LOG_TAG, "Server Thread ended");
		interrupt();
	}
	
	public boolean isRunning() {
	    return running;
	}
}