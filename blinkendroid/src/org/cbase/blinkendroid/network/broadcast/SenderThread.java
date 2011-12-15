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

package org.cbase.blinkendroid.network.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A multicast sender that sends a server name to blinkendroid clients.
 */
public class SenderThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(SenderThread.class);
    private final String message;
    private InetAddress group;
    volatile private boolean running = true;
    private DatagramSocket socket;

    /**
     * Creates a new {@link SenderThread}
     * 
     * @param serverName
     *            The server's name.
     */
    public SenderThread(String name) {
	// workaround: remove spaces, as those currently break the protocol
	name = name.replaceAll("\\s", "");
	message = BlinkendroidApp.BROADCAST_PROTOCOL_VERSION + " " + BlinkendroidApp.CLIENT_BROADCAST_COMMAND + " "
		+ name;
    }

    @Override
    public void run() {
	try {
	    this.setName("SRV Send Annouce");
	    socket = new DatagramSocket(BlinkendroidApp.BROADCAST_ANNOUCEMENT_CLIENT_PORT);
	    socket.setReuseAddress(true);
	    socket.setBroadcast(true);
	    logger.info("Sender thread started.");
	    group = InetAddress.getAllByName("255.255.255.255")[0];
	    logger.info("Server ip: " + group.toString());

	    while (running) {
		final byte[] messageBytes = message.getBytes("UTF-8");
		final DatagramPacket initPacket = new DatagramPacket(messageBytes, messageBytes.length, group,
			BlinkendroidApp.BROADCAST_ANNOUCEMENT_SERVER_PORT);
		logger.info("Broadcasting Packet");
		socket.send(initPacket);

		logger.info("Broadcasting: '" + message + "'");
		try {
		    Thread.sleep(BlinkendroidApp.BROADCAST_RATE);
		} catch (final InterruptedException x) {
		    // swallow, this is expected when being interrupted
		}
	    }

	    socket.close();

	} catch (final IOException x) {
	    logger.error("problem sending", x);
	}
    }

    public void shutdown() {
	logger.info("SenderThread: initiating shutdown");
	running = false;

	if (socket != null) {
	    socket.close();
	}
	interrupt();
	try {
	    join();
	} catch (final InterruptedException x) {
	    // swallow, this is expected when being interrupted
	}
    }
}
