package org.cbase.blinkendroid.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

public class BlinkendroidProtocol {
	public final static String PROTOCOL_PLAYER="P";
	public final static String PROTOCOL_INIT="I";
	PrintWriter out;
	BufferedReader in;
	GlobalTimerThread globalTimerThread;

	private final HashMap<String, ICommandHandler> handlers = 
	    new HashMap<String, ICommandHandler>();
		
	public void registerHandler(String proto, ICommandHandler handler) {
	    handlers.put(proto, handler);
	}

	public BlinkendroidProtocol(OutputStream out, InputStream is){
		this.out=new PrintWriter(out, true);
		this.in=new BufferedReader( new InputStreamReader(is));
		new InputThread().start();
	}
	
	public void startTimerThread(){
		if(null==globalTimerThread)
			globalTimerThread=new GlobalTimerThread();
		globalTimerThread.start();
	}
	
	public void close(){
		out.close();
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class InputThread extends Thread {
		private boolean running = true;

		@Override
		public void run() {
			Log.i(Constants.LOG_TAG, "InputThread started");
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					String proto = inputLine.substring(0, 1);
					ICommandHandler handler	=	handlers.get(proto);
					if(null!=handler)
						handler.handle(inputLine.getBytes());
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	private class GlobalTimerThread extends Thread {
		private boolean running = true;

		@Override
		public void run() {
			Log.i(Constants.LOG_TAG, "GlobalTimerThread started");
			while (running) {
				try {
					GlobalTimerThread.sleep(5000);
				} catch (InterruptedException e) {
				}
				long t = System.currentTimeMillis();
				Log.i(Constants.LOG_TAG, "GlobalTimerThread ping " + t);
				out.write(Long.toString(t) + '\n');
				out.flush();
			}
		}
	}

}