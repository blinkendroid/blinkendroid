package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.R;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;
import org.cbase.blinkendroid.network.BlinkendroidProtocol.ConnectionClosedListener;

public class PlayerClient implements ConnectionClosedListener {

    //position
    int x,y;
    //clipping
    float startX,endX,startY,endY;
    //protocol
    BlinkendroidProtocol blinkendroidProtocol;
    long startTime;
    PlayerManager playerManager;
    
    public PlayerClient(PlayerManager playerManager, BlinkendroidProtocol blinkendroidProtocol, long startTime) {
	this.playerManager=playerManager;
	this.blinkendroidProtocol=blinkendroidProtocol;
	this.startTime=startTime;
	blinkendroidProtocol.setConnectionClosedListener(this);
    }

    public void shutdown() {
	blinkendroidProtocol.shutdown();
    }
    public void clip() {
	blinkendroidProtocol.clip(startX, startY, endX, endY);
    }
    public void play() {
	blinkendroidProtocol.play(x,y,R.raw.allyourbase,System.currentTimeMillis(),startTime);
    }
    public void arrow(int degrees) {
	blinkendroidProtocol.arrow(degrees);
    }
    public void connectionClosed() {
	playerManager.removeClient(this);
    }
}
