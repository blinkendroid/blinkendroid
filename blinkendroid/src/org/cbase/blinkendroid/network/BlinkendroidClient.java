package org.cbase.blinkendroid.network;

import java.net.Socket;
import java.net.UnknownHostException;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.player.PlayerThread;

import android.util.Log;

public class BlinkendroidClient {

    private String ip;
    private int port;
    private PlayerThread playerThread;
    BlinkendroidProtocol protocol;

    public BlinkendroidClient(String ip, int port) {
	this.ip = ip;
	this.port = port;
    }

	public void connect() {
		try {
			Thread.sleep(5000);
			Log.i(Constants.LOG_TAG,"connect to server: '"+ip+"':"+port);
			Socket socket = new Socket(ip, port);
			protocol = new BlinkendroidProtocol(socket,false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public void setPlayerThread(PlayerThread playerThread) {
	if(null!=protocol)
	    protocol.registerHandler(BlinkendroidProtocol.PROTOCOL_PLAYER,
		playerThread.getPlayerProtocolHandler());
	this.playerThread = playerThread;
    }


    public BlinkendroidProtocol getProtocol() {
	return protocol;
    }

}
