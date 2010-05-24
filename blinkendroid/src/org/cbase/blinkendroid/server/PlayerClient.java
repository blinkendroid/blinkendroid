package org.cbase.blinkendroid.server;

import java.net.SocketAddress;

import org.cbase.blinkendroid.R;
import org.cbase.blinkendroid.network.BlinkendroidServerProtocol;
import org.cbase.blinkendroid.network.ConnectionListener;

public class PlayerClient implements ConnectionListener {

    //position
    int x,y;
    //clipping
    float startX,endX,startY,endY;
    //protocol
    BlinkendroidServerProtocol blinkendroidProtocol;
    long startTime;
    PlayerManager playerManager;
    
    public PlayerClient(PlayerManager playerManager, BlinkendroidServerProtocol blinkendroidProtocol, long startTime) {
	this.playerManager=playerManager;
	this.blinkendroidProtocol=blinkendroidProtocol;
	this.startTime=startTime;
	blinkendroidProtocol.addConnectionClosedListener(this);
    }

    public void shutdown() {
	blinkendroidProtocol.shutdown();
    }
    public void clip() {
	 blinkendroidProtocol.clip(startX, startY, endX, endY);
    }
    public void play(String filename) {
	blinkendroidProtocol.play(x,y,System.currentTimeMillis(),startTime,filename);
    }
    public void arrow(int degrees) {
	blinkendroidProtocol.arrow(degrees);
    }
    public void connectionClosed() {

    }

    public void connectionClosed(SocketAddress socketAddress) {
	shutdown();
	playerManager.removeClient(this);
    }

    public void connectionOpened(SocketAddress socketAddress) {

    }
}
