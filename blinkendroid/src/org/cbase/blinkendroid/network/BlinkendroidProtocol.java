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

    public static final String COMMAND_PLAYER_TIME = "T";
    public static final String COMMAND_CLIP = "C";
    public static final String COMMAND_PLAY = "P";
    public static final String COMMAND_INIT = "I";

    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private GlobalTimerThread globalTimerThread;
    private ReceiverThread receiverThread;

    private final HashMap<String, ICommandHandler> handlers = new HashMap<String, ICommandHandler>();

    private ConnectionClosedListener connectionClosedListener;

    public interface ConnectionClosedListener {
	public void connectionClosed();
    }

    public void setConnectionClosedListener(
	    ConnectionClosedListener connectionClosedListener) {
	this.connectionClosedListener = connectionClosedListener;
    }

    public BlinkendroidProtocol(final Socket socket) throws IOException {
	this.socket = socket;
	this.out = new PrintWriter(socket.getOutputStream(), true);
	this.in = new BufferedReader(new InputStreamReader(socket
		.getInputStream()));
	// Receiverthread wird beim server benötigt um zu wissen wann client weg
	// ist
	receiverThread = new ReceiverThread();
	receiverThread.start();
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
	    // in.close();
	    socket.close();
	    Log.d(Constants.LOG_TAG, "BlinkendroidProtocol: Socket closed.");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void shutdown() {
	if (null != globalTimerThread)
	    globalTimerThread.shutdown();
	if (null != receiverThread)
	    receiverThread.shutdown();
	Log.i(Constants.LOG_TAG, "Protocol shutdown.");
	close();
    }

    // Inner classes:
    /**
     * A thread that receives information from a Blinkendroid server.
     */
    private class ReceiverThread extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    running = true;
	    Log.i(Constants.LOG_TAG, "server InputThread started");
	    String inputLine;
	    try {
		while (running && (inputLine = in.readLine()) != null) {
		    if (!running) // fast exit
			break;
		    Log.i(Constants.LOG_TAG, "InputThread received: "
			    + inputLine);
		    final String proto = inputLine.substring(0, 1);
		    ICommandHandler handler = handlers.get(proto);
		    if (null != handler)
			handler.handle(inputLine.substring(1));
		}
	    } catch (SocketException e) {
		Log.d(Constants.LOG_TAG, "Socket closed.");
	    } catch (NumberFormatException e) {
		Log.e(Constants.LOG_TAG, "server InputThread fucked ", e);
	    } catch (IOException e) {
		Log.e(Constants.LOG_TAG, "server InputThread fucked ", e);
	    }
	    Log.i(Constants.LOG_TAG, "server InputThread ended!!!!!!! ");

	    // wenn auf serverseite dann PlayerManager remove
	    if (connectionClosedListener != null)
		connectionClosedListener.connectionClosed();
	    close();
	}

	public void shutdown() {
	    running = false;
	    interrupt();
	    Log.d(Constants.LOG_TAG, "ReceiverThread initiating shutdown");
	}
    }

    /**
     * This thread sends the global time to connected devices.
     */
    private class GlobalTimerThread extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    Log.i(Constants.LOG_TAG, "GlobalTimerThread started");
	    while (running) {
		try {
		    GlobalTimerThread.sleep(5000);
		} catch (InterruptedException e) {
		    // swallow
		}
		if (!running) // fast exit
		    break;

		long t = System.currentTimeMillis();
		Log.i(Constants.LOG_TAG, "GlobalTimerThread ping " + t);
		out.write(PROTOCOL_PLAYER + COMMAND_PLAYER_TIME
			+ Long.toString(t) + '\n');
		out.flush();
	    }
	    Log.d(Constants.LOG_TAG, "GlobalTimerThread stopped");
	}

	public void shutdown() {
	    running = false;
	    interrupt();
	    Log.d(Constants.LOG_TAG, "GlobalTimerThread initiating shutdown");
	}
    }

    public void play(int x, int y, int resId, long l, long startTime) {
	final String cmd = PROTOCOL_PLAYER + COMMAND_PLAY + Integer.toString(x)
		+ "," + Integer.toString(y) + "," + Integer.toString(resId)
		+ "," + Long.toString(l) + "," + Long.toString(startTime)
		+ '\n';
	out.write(cmd);
	out.flush();
	Log.i(Constants.LOG_TAG, cmd);
    }

    public void arrow(int degrees) {
	final String cmd = PROTOCOL_PLAYER + COMMAND_INIT + degrees + "\n";
	out.write(cmd);
	out.flush();
	Log.i(Constants.LOG_TAG, cmd);
    }

    public void clip(float startX, float startY, float endX, float endY) {
	final String cmd = PROTOCOL_PLAYER + COMMAND_CLIP + startX + ","
		+ startY + "," + endX + "," + endY + '\n';
	out.write(cmd);
	out.flush();
	Log.i(Constants.LOG_TAG, cmd);
    }
}
