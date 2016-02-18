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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.cbase.blinkendroid.Constants;

public class BlinkendroidServerProtocol extends AbstractBlinkendroidProtocol {

    GlobalTimerThread globalTimerThread;

    public BlinkendroidServerProtocol(final Socket socket,
	    ConnectionListener connectionListener) throws IOException {
	super(socket, connectionListener, true);
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
	System.out.println("BlinkendroidServerProtocol.shutdown() initiated");
	if (null != globalTimerThread)
	    globalTimerThread.shutdown();
	try {
	    writeInt(out, PROTOCOL_PLAYER);
	    writeInt(out, COMMAND_SHUTDOWN);
	    out.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("send shutdown failed ");
	}

	super.shutdown();

	System.out.println("BlinkendroidServerProtocol.shutdown() sended");
    }

    public void play(int x, int y, long l, long startTime, String bbmzFileName) {
	try {
	    writeInt(out, PROTOCOL_PLAYER);
	    writeInt(out, COMMAND_PLAY);
	    writeInt(out, x);
	    writeInt(out, y);
	    writeLong(out, l);
	    writeLong(out, startTime);

	    if (null == bbmzFileName) {
		writeLong(out, 0);
		System.out.println("Play default video ");
	    } else {
		File movie = new File(bbmzFileName);
		if (null != movie && movie.exists()) {

		    try {
			writeLong(out, movie.length());
			System.out.println("try to read file with bytes "
				+ movie.length());
			InputStream is = new FileInputStream(movie);
			byte[] buffer = new byte[1024];
			int allLen = 0;
			int len;
			while ((len = is.read(buffer)) != -1) {
			    out.write(buffer, 0, len);
			    allLen += len;
			}
			is.close();
			System.out
				.println("send movie bytes " + movie.length());
			writeLong(out, movie.length());
		    } catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("sending movie failed");
		    }
		} else {
		    System.out.println("movie not found" + bbmzFileName);
		}
	    }

	    out.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("play failed ");
	}
    }

    public void arrow(int degrees, int color) {
	try {
	    writeInt(out, PROTOCOL_PLAYER);
	    writeInt(out, COMMAND_INIT);
	    writeInt(out, degrees);
	    writeInt(out, color);

	    out.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("arrow failed ");
	}
    }

    public void clip(float startX, float startY, float endX, float endY) {
	try {
	    writeInt(out, PROTOCOL_PLAYER);
	    writeInt(out, COMMAND_CLIP);
	    writeFloat(out, startX);
	    writeFloat(out, startY);
	    writeFloat(out, endX);
	    writeFloat(out, endY);
	    out.flush();
	    System.out.println("clip flushed ");
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("clip failed ");
	}
    }

    /**
     * This thread sends the global time to connected devices.
     */
    class GlobalTimerThread extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    System.out.println("GlobalTimerThread started");
	    while (running) {
		try {
		    GlobalTimerThread.sleep(100);
		} catch (InterruptedException e) {
		    // swallow
		}
		if (!running) // fast exit
		    break;

		long t = System.currentTimeMillis();
		try {
		    writeInt(out, PROTOCOL_PLAYER);
		    writeInt(out, COMMAND_PLAYER_TIME);
		    writeLong(out, t);
		    out.flush();
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("GlobalTimerThread failed ");
		}
	    }
	    System.out.println("GlobalTimerThread stopped");
	}

	public void shutdown() {
	    running = false;
	    interrupt();
	    System.out.println("GlobalTimerThread initiating shutdown");
	}
    }
}
