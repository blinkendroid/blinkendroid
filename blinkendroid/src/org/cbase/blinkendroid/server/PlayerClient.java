package org.cbase.blinkendroid.server;

import java.net.SocketAddress;

import org.cbase.blinkendroid.network.udp.BlinkendroidServerProtocol;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.cbase.blinkendroid.network.udp.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerClient extends ConnectionState {

    // position
    int x, y;
    // clipping
    float startX, endX, startY, endY;
    // protocol
    long startTime;
    private ClientSocket mclientSocket;
    private BlinkendroidServerProtocol mBlinkenProtocol;
    private static final Logger logger = LoggerFactory.getLogger(PlayerClient.class);

    public PlayerClient(PlayerManager playerManager, ClientSocket clientSocket) {
	super(clientSocket, playerManager);
	logger.info("new PlayerClient");
	this.mclientSocket = clientSocket;
	// this.registerHandler(BlinkendroidApp.PROTOCOL_CONNECTION, this);
	mBlinkenProtocol = new BlinkendroidServerProtocol(clientSocket);
    }

    public SocketAddress getClientSocketAddress() {
	return mclientSocket.getInetSocketAddress();
    }

    public BlinkendroidServerProtocol getBlinkenProtocol() {
	return mBlinkenProtocol;
    }

    @Override
    public String toString() {
	return x + ":" + y;
    }
}
