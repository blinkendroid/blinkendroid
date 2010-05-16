package org.cbase.blinkendroid.player;

import java.net.Socket;
import java.net.UnknownHostException;

public class BlinkendroidClient {
	private String ip;
	private PlayerThread playerThread;
	BlinkendroidProtocol protocol;

	public BlinkendroidClient(String ip) {
		this.ip = ip;
	}

	public void setPlayerThread(PlayerThread playerThread) {
		protocol.addProtocolHandler(playerThread);
		this.playerThread = playerThread;
	}

	public void connect() {
		Socket socket;
		try {
			Thread.sleep(5000);
			socket = new Socket(ip, 4444);// 5556);
			protocol = new BlinkendroidProtocol(socket.getOutputStream(),
					socket.getInputStream());
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
