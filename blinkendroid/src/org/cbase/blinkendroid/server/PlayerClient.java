package org.cbase.blinkendroid.server;

import java.net.SocketAddress;

import org.cbase.blinkendroid.network.udp.BlinkendroidServerProtocol;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.cbase.blinkendroid.network.udp.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerClient extends ConnectionState {

	private String name = ""; // TODO where to get the name?
	// position
	int x;
	int y;
	// clipping
	float startX;
	float endX;
	float startY;
	float endY;
	// protocol
	private long startTime;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SocketAddress getClientSocketAddress() {
		return mclientSocket.getInetSocketAddress();
	}

	public BlinkendroidServerProtocol getBlinkenProtocol() {
		return mBlinkenProtocol;
	}

	@Override
	public String toString() {
		return name + mclientSocket.toString() + "@" + x + ":" + y;
	}
}
