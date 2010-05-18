package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.network.BlinkendroidProtocol;

public class PlayerClient {

    //position
    int x,y;
    //clipping
    float startX,endX,startY,endY;
    //protocol
    BlinkendroidProtocol blinkendroidProtocol;
    
    public PlayerClient(BlinkendroidProtocol blinkendroidProtocol) {
	this.blinkendroidProtocol=blinkendroidProtocol;
    }

    public void shutdown() {
	blinkendroidProtocol.shutdown();
    }
}
