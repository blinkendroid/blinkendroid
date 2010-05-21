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

import java.io.IOException;
import java.net.Socket;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

public class BlinkendroidServerProtocol extends AbstractBlinkendroidProtocol {

    GlobalTimerThread globalTimerThread;
    
    public BlinkendroidServerProtocol(final Socket socket, ConnectionListener connectionListener) throws IOException {
	super(socket,connectionListener,true);
    }


    public void startTimerThread() {
	if (globalTimerThread != null) {
	    globalTimerThread.shutdown();
	}
	globalTimerThread = new GlobalTimerThread();
	globalTimerThread.start();
    }

    @Override
    public void shutdown() {
        if (null != globalTimerThread)
            globalTimerThread.shutdown();
        super.shutdown();
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
    
    /**
     * This thread sends the global time to connected devices.
     */
    class GlobalTimerThread extends Thread {

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
}
