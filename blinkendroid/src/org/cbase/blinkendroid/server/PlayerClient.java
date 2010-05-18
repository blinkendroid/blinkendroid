package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.R;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;

public class PlayerClient {

    //position
    int x,y;
    //clipping
    float startX,endX,startY,endY;
    //protocol
    BlinkendroidProtocol blinkendroidProtocol;
    long startTime;
    
    public PlayerClient(BlinkendroidProtocol blinkendroidProtocol, long startTime) {
	this.blinkendroidProtocol=blinkendroidProtocol;
	this.startTime=startTime;
    }

    public void shutdown() {
	blinkendroidProtocol.shutdown();
    }

    public void play() {
	blinkendroidProtocol.play(x,y,R.raw.arius,System.currentTimeMillis(),startTime);
	blinkendroidProtocol.clip(startX, startY, endX, endY);
    }
}
