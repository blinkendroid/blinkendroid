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
    
    public PlayerClient(BlinkendroidProtocol blinkendroidProtocol) {
	this.blinkendroidProtocol=blinkendroidProtocol;
    }

    public void shutdown() {
	blinkendroidProtocol.shutdown();
    }

    public void play() {
	blinkendroidProtocol.play(x,y,R.raw.arius,System.currentTimeMillis()+2000);
	blinkendroidProtocol.clip(startX, startY, endX, endY);
    }
}
