package org.cbase.blinkendroid.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

public class BlinkendroidProtocol {
    public final static String PROTOCOL_PLAYER = "P";
    public final static String PROTOCOL_INIT = "I";

    public final static String COMMAND_PLAYER_TIME = "T";
    public final static String COMMAND_CLIP = "C";
    public static final String COMMAND_PLAY = "P";

    private boolean server;
    PrintWriter out;
    BufferedReader in;
    Socket socket;
    GlobalTimerThread globalTimerThread;
    RecieverThread recieverThread;

    private final HashMap<String, ICommandHandler> handlers = new HashMap<String, ICommandHandler>();

    ConnectionClosedListener connectionClosedListener;
    
    public interface ConnectionClosedListener{
	public void connectionClosed();
    }
    public void setConnectionClosedListener(ConnectionClosedListener connectionClosedListener){
	this.connectionClosedListener=connectionClosedListener;
    }
    public BlinkendroidProtocol(Socket socket, boolean server) {
	this.socket = socket;
	try {
	    this.out = new PrintWriter(socket.getOutputStream(), true);
	    this.in = new BufferedReader(new InputStreamReader(socket
		    .getInputStream()));
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(42);
	}
	this.server = server;
	// if(!server)
	recieverThread = new RecieverThread();
	recieverThread.start();
    }

    public void registerHandler(String proto, ICommandHandler handler) {
	handlers.put(proto, handler);
    }
    
    public void unregisterHandler(ICommandHandler handler) {
	handlers.remove(handler);
    }
    public void startTimerThread() {
	if (globalTimerThread != null) {
	    globalTimerThread.shutdown();
	}
	globalTimerThread = new GlobalTimerThread();
	globalTimerThread.start();
    }

    public void close() {
	out.close();
	try {
	    in.close();
	    socket.close();
	    Log.d(Constants.LOG_TAG, "BlinkendroidProtocol: Socket closed.");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public void shutdown() {
	if (null != globalTimerThread)
	    globalTimerThread.shutdown();
	recieverThread.shutdown();
	Log.i(Constants.LOG_TAG, "Protocol shutdown.");
	close();
    }

    // Inner classes:
    /**
     * A thread that receives information from a Blinkendroid server.
     */
    private class RecieverThread extends Thread {
	private boolean running = true;

	@Override
	public void run() {
	    running = true;
	    Log.i(Constants.LOG_TAG, "InputThread started "
		    + (server ? "server" : "client"));
	    String inputLine;
	    try {
		while (running && (inputLine = in.readLine()) != null) {
		    Log.i(Constants.LOG_TAG, "InputThread recieved: "
			    + inputLine);
		    String proto = inputLine.substring(0, 1);
		    ICommandHandler handler = handlers.get(proto);
		    if (null != handler)
			handler.handle(inputLine.substring(1).getBytes());
		}
	    }catch (SocketException e) {
		Log.d(Constants.LOG_TAG, "Socket closed.");
	    } catch (NumberFormatException e) {
		Log.e(Constants.LOG_TAG, "InputThread fucked "
			+ (server ? "server" : "client"), e);
	    } catch (IOException e) {
		Log.e(Constants.LOG_TAG, "InputThread fucked "
			+ (server ? "server" : "client"), e);
	    }
	    Log.i(Constants.LOG_TAG, "InputThread ended!!!!!!! "
		    + (server ? "server" : "client"));
	    
	    //wenn auf serverseite dann PlayerManager remove
	    connectionClosedListener.connectionClosed();
	}

	public void shutdown() {
	    Log.d(Constants.LOG_TAG, "RecieverThread shutdown.");
	    running = false;
	    interrupt();
	}
    }

    /**
     * This thread sends the global time to connected devices.
     */
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
		out.write(PROTOCOL_PLAYER + COMMAND_PLAYER_TIME
			+ Long.toString(t) + '\n');
		out.flush();
	    }
	}

	public void shutdown() {
	    running = false;
	    Log.d(Constants.LOG_TAG, "GlobalTimerThread shutdown.");
	    interrupt();
	}
    }
    public void play(int x, int y, int resId, long l, long startTime) {
	String cmd=PROTOCOL_PLAYER + COMMAND_PLAY
	+ Integer.toString(x)+"," + Integer.toString(y)+"," + Integer.toString(resId)+"," + Long.toString(l)+"," + Long.toString(startTime)+ '\n';
	out.write(cmd);
	out.flush();
	    Log.i(Constants.LOG_TAG, cmd);
    }
    public void clip(float startX,float startY,float endX,float endY){
	String cmd=PROTOCOL_PLAYER + COMMAND_CLIP +
	    + startX + "," + startY +","+ endX + "," + endY+ '\n';
	    out.write(cmd);
	    out.flush();
	    Log.i(Constants.LOG_TAG, cmd);
    }
}