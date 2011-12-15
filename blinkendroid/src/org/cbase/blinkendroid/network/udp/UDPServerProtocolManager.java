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

package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.network.udp.ConnectionState.Command;
import org.cbase.blinkendroid.server.PlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UDPServerProtocolManager extends UDPAbstractBlinkendroidProtocol implements ConnectionListener {

    private static final Logger logger = LoggerFactory.getLogger(UDPServerProtocolManager.class);

    protected GlobalTimerThread globalTimerThread;
    private PlayerManager mPlayerManager;

    public void setPlayerManager(PlayerManager mPlayerManager) {
	this.mPlayerManager = mPlayerManager;
    }

    public UDPServerProtocolManager(final DatagramSocket socket) throws IOException {
	super(socket);
    }

    public void startTimerThread() {
	if (globalTimerThread != null) {
	    globalTimerThread.shutdown();
	}
	globalTimerThread = new GlobalTimerThread();
	globalTimerThread.start();
    }

    public void stopTimerThread() {
	if (globalTimerThread != null) {
	    globalTimerThread.shutdown();
	}
    }

    public boolean isGlobalTimerThreadRunning() {
	if (null == globalTimerThread)
	    return false;
	return globalTimerThread.running;
    }

    @Override
    public void shutdown() {
	if (null != globalTimerThread)
	    globalTimerThread.shutdown();
	super.shutdown();
    }

    @Override
    protected void receive(DatagramPacket packet) throws IOException {
	InetSocketAddress from = new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());

	/* every Client has his own connectionHandler ! */

	ByteBuffer in = ByteBuffer.wrap(packet.getData());
	int proto = in.getInt();
	int pos = in.position();
	List<CommandHandler> handler = handlers.get(proto);
	if (null != handler) {
	    for (CommandHandler commandHandler : handler) {
		commandHandler.handle(from, in);
		in.position(pos);
	    }
	} else {
	    if (mPlayerManager != null) {
		mPlayerManager.handle(this, from, proto, in);
	    }
	}
    }

    public void sendBroadcast(ByteBuffer out) {
	try {
	    // TODO: view SenderThread in broadcast and increase performance by
	    // removing constant creation of InetSocketAddresses
	    send(new InetSocketAddress(InetAddress.getAllByName("255.255.255.255")[0],
		    BlinkendroidApp.BROADCAST_CLIENT_PORT), out);
	} catch (UnknownHostException e) {
	    logger.error("Don't know where to send the broadcast", e);
	} catch (IOException e) {
	    logger.error("IOException", e);
	}
    }

    /**
     * This thread sends the global time to connected devices.
     */
    class GlobalTimerThread extends Thread {
	volatile private boolean running = true;

	@Override
	public void run() {
	    this.setName("SRV Send GlobalTimer");
	    logger.info("GlobalTimerThread started");
	    while (running) {
		try {
		    Thread.sleep(BlinkendroidApp.HEARTBEAT_RATE);
		} catch (InterruptedException e) {
		    // swallow
		}
		if (!running) // fast exit
		    break;

		ByteBuffer out = ByteBuffer.allocate(128);
		out.putInt(BlinkendroidApp.PROTOCOL_HEARTBEAT);
		out.putInt(Command.HEARTBEAT.ordinal());
		out.putInt(BlinkendroidApp.GLOBALTIMER);
		out.putLong(System.currentTimeMillis());
		sendBroadcast(out);
		// logger.info( "GlobalTimerThread Broadcast sent: " + out);
	    }
	    logger.info("GlobalTimerThread stopped");
	}

	public void shutdown() {
	    logger.info("GlobalTimerThread initiating shutdown");
	    running = false;
	    interrupt();
	    try {
		join();
	    } catch (InterruptedException e) {
		logger.error(" GlobalTimerThread shutdownjoin failed");
	    }
	    logger.info("GlobalTimerThread shutdown completed");
	}
    }

    public void connectionClosed(ClientSocket clientSocket) {
	for (ConnectionListener connListener : connectionListener) {
	    connListener.connectionClosed(clientSocket);
	}
    }

    public void connectionOpened(ClientSocket clientSocket) {
	for (ConnectionListener connListener : connectionListener) {
	    connListener.connectionOpened(clientSocket);
	}
    }
}
