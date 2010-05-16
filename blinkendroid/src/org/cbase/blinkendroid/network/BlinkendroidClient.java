package org.cbase.blinkendroid.network;

import java.net.Socket;
import java.net.UnknownHostException;

import org.cbase.blinkendroid.player.PlayerThread;

public class BlinkendroidClient {
	private String ip;
	private PlayerThread playerThread;
	BlinkendroidProtocol protocol;

	public BlinkendroidClient(String ip) {
		this.ip = ip;
	}

	public void setPlayerThread(PlayerThread playerThread) {
		protocol.registerHandler(BlinkendroidProtocol.PROTOCOL_PLAYER, playerThread.getPlayerProtocolHandler());
		this.playerThread = playerThread;
	}

	public void connect() {
		try {
			Thread.sleep(5000);
			Socket socket = new Socket(ip, 4444);// 5556);
			protocol = new BlinkendroidProtocol(socket,false);
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BlinkendroidProtocol getProtocol() {
		return protocol;
	}

}
