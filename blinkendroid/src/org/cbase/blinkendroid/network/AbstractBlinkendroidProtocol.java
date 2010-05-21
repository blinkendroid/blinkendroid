package org.cbase.blinkendroid.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cbase.blinkendroid.Constants;


public class AbstractBlinkendroidProtocol {

    public static final String PROTOCOL_PLAYER = "P";
    public static final String COMMAND_PLAYER_TIME = "T";
    public static final String COMMAND_CLIP = "C";
    public static final String COMMAND_PLAY = "P";
    public static final String COMMAND_INIT = "I";
    protected PrintWriter out;
    protected BufferedReader in;
    protected Socket socket;
    protected ReceiverThread receiverThread;
    protected final HashMap<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();
    private List<ConnectionListener> connectionListener = new ArrayList<ConnectionListener>();
    private boolean server;

    protected AbstractBlinkendroidProtocol(final Socket socket,
	    ConnectionListener connectionListener, boolean server)
	    throws IOException {
	this.socket = socket;
	this.server = server;
	this.out = new PrintWriter(socket.getOutputStream(), true);
	this.in = new BufferedReader(new InputStreamReader(socket
		.getInputStream()));
	this.connectionListener.add(connectionListener);
	receiverThread = new ReceiverThread();
	receiverThread.start();
    }

    public void addConnectionClosedListener(
	    ConnectionListener connectionListener) {
	this.connectionListener.add(connectionListener);
    }

    public void registerHandler(String proto, CommandHandler handler) {
	handlers.put(proto, handler);
    }

    public void unregisterHandler(CommandHandler handler) {
	handlers.remove(handler);
    }

    protected void connectionClosed(SocketAddress socketAddress) {
	for (ConnectionListener listener : connectionListener) {
	    listener.connectionClosed(socketAddress);
	}
    }

    protected void connectionOpened(SocketAddress socketAddress) {
	for (ConnectionListener listener : connectionListener) {
	    listener.connectionOpened(socketAddress);
	}
    }

    public void close() {
	out.close();
	try {
	    if (!server)// TODO ugly hack, server needs to long
		in.close();
	    socket.close();
	    System.out.println( getMyName()
		    + " BlinkendroidProtocol: Socket closed.");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void shutdown() {
	if (null != receiverThread)
	    receiverThread.shutdown();
	// join
	System.out.println( getMyName() + " Protocol shutdown.");
	close();
    }

    // Inner classes:
    /**
     * A thread that receives information
     */
    class ReceiverThread extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    running = true;
	    System.out.println( getMyName() + " InputThread started");
	    String inputLine;
	    connectionOpened(socket.getRemoteSocketAddress());
	    try {
		while (running && (inputLine = in.readLine()) != null) {
		    if (!running) // fast exit
			break;
		    System.out.println( getMyName()
			    + " InputThread received: " + inputLine);
		    final String proto = inputLine.substring(0, 1);
		    CommandHandler handler = handlers.get(proto);
		    if (null != handler)
			handler.handle(inputLine.substring(1));
		}
	    } catch (SocketException e) {
		 System.out.println( getMyName() + " Socket closed.");
	    } catch (IOException e) {
		System.out.println( getMyName() + " InputThread fucked ");
		e.printStackTrace();
	    }
	    System.out.println(getMyName()
			    + " InputThread ended!!!!!!! ");

	    connectionClosed(socket.getRemoteSocketAddress());
	    close();
	}

	public void shutdown() {
	    System.out.println( getMyName()
		    + " ReceiverThread initiating shutdown");
	    running = false;
	    interrupt();
	}
    }

    protected String getMyName() {
	if (server)
	    return "Server " + socket.getRemoteSocketAddress();
	else
	    return "Client " + socket.getRemoteSocketAddress();
    }
}