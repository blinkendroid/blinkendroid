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

package org.cbase.blinkendroid.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;

import android.util.Log;

public class BlinkendroidServer extends Thread {

    private boolean running = false;
    private int port = -1;
    private PlayerManager playerManager;

    public BlinkendroidServer(int port) {
	this.port = port;
    }

    @Override
    public void run() {

	running = true;
	Log.i(Constants.LOG_TAG, "BlinkendroidServer Thread started");

	try {
	    final ServerSocket serverSocket = new ServerSocket(port);
	    playerManager = new PlayerManager();
	    acceptLoop(serverSocket);
	    playerManager.shutdown();
	    serverSocket.close();
	} catch (final IOException x) {
	    Log.e(Constants.LOG_TAG, "Could not create Socket", x);
	    throw new RuntimeException(x);
	}

	Log.i(Constants.LOG_TAG, "BlinkendroidServer Thread ended");
    }

    private void acceptLoop(final ServerSocket serverSocket) {

	while (running) {
	    try {
		final Socket clientSocket = serverSocket.accept();
		if (!running) // fast exit
		    break;
		Log.i(Constants.LOG_TAG, "BlinkendroidServer got connection "
			+ clientSocket.getRemoteSocketAddress().toString());
		final BlinkendroidProtocol blinkendroidProtocol = new BlinkendroidProtocol(
			clientSocket, true);
		playerManager.addClient(blinkendroidProtocol);
	    } catch (final IOException x) {
		Log.e(Constants.LOG_TAG, "BlinkendroidServer could not accept",
			x);
	    }
	}
    }

    public void shutdown() {
	running = false;
	interrupt();
    }
}
