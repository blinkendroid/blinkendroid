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
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.udp.BlinkendroidClientProtocol;
import org.cbase.blinkendroid.network.udp.ClientConnectionState;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.cbase.blinkendroid.network.udp.UDPClientProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidClient extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidClient.class);

    private final InetSocketAddress socketAddress;
    private final BlinkendroidListener listener;
    private DatagramSocket socket;
    private UDPClientProtocolManager protocol;
    private ClientConnectionState mConnstate;
    private BlinkendroidClientProtocol blinkenProto;

    public BlinkendroidClient(final InetSocketAddress socketAddress, final BlinkendroidListener listener) {
	this.socketAddress = socketAddress;
	this.listener = listener;
    }

    @Override
    public synchronized void start() {
	logger.info("trying to connect to server: " + socketAddress);
	try {
	    socket = new DatagramSocket(BlinkendroidApp.BROADCAST_CLIENT_PORT);
	    socket.setReuseAddress(true);
	    protocol = new UDPClientProtocolManager(socket, socketAddress);

	    ClientSocket serverSocket = new ClientSocket(protocol, socketAddress);
	    mConnstate = new ClientConnectionState(serverSocket, listener);
	    protocol.registerHandler(BlinkendroidApp.PROTOCOL_CONNECTION, mConnstate);
	    mConnstate.openConnection();

	    blinkenProto = new BlinkendroidClientProtocol(listener, serverSocket);
	    protocol.registerHandler(BlinkendroidApp.PROTOCOL_PLAYER, blinkenProto);
	    logger.info("connected");

	} catch (final IOException x) {
	    logger.error("connection failed");
	    x.printStackTrace();
	    listener.connectionFailed(x.getClass().getName() + ": " + x.getMessage());
	}
    }

    public void shutdown() {
	if (null != mConnstate)
	    mConnstate.shutdown();
	if (null != protocol)
	    protocol.shutdown();
	if (null != socket) {
	    if (!socket.isClosed())
		socket.close();
	}
	logger.info("client shutdown completed");
    }

    public void locateMe() {
	blinkenProto.locateMe();
    }

    public void touch() {
	blinkenProto.touch();
    }

    public void hitMole() {
	blinkenProto.hitMole();
    }

    public void missedMole() {
	blinkenProto.missedMole();
    }
}
