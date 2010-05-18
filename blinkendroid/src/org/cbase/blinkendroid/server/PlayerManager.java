package org.cbase.blinkendroid.server;

import java.util.List;

import org.cbase.blinkendroid.network.BlinkendroidProtocol;

public class PlayerManager {

    List<PlayerClient> clients;
    
    public void addClient(BlinkendroidProtocol blinkendroidProtocol) {
	    PlayerClient pClient= new PlayerClient(blinkendroidProtocol);
	    clients.add(pClient);
	    //server starts thread to send globaltime
	    blinkendroidProtocol.startTimerThread();
    }

    public void shutdown() {
	for (PlayerClient client : clients) {
	    if (null != client)
		client.shutdown();
	}
    }
}
