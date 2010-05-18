package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;

import android.util.Log;

public class PlayerManager {

    PlayerClient[][] clients = new PlayerClient[10][10];
    int maxX = 1, maxY = 1;
    long startTime = 0;

    public void PlayerManager() {

    }

    public void addClient(BlinkendroidProtocol blinkendroidProtocol) {
	if (startTime == 0)
	    startTime = System.currentTimeMillis();
	PlayerClient pClient = new PlayerClient(this, blinkendroidProtocol, startTime);
	// server starts thread to send globaltime
	blinkendroidProtocol.startTimerThread();// TODO evtl nur ein timerthread
						// im server

	// TODO finde freien Platz in der Matrix
	boolean found = false;
	for (int i = 0; i < maxY; i++) {
	    for (int j = 0; j < maxY; j++) {
		// wenn freier platz dann nehmen
		if (clients[i][j] == null) {
		    pClient.y = i;
		    pClient.x = j;
		    found = true;
		    break;
		}
		if (found)
		    break;
	    }
	}
	// wenn nicht gefunden dann erweitern, erst dann y
	if (!found) {
	    // wenn maxX>maxY -> neuen client an maxY+1
	    if (maxX > maxY) {
		pClient.y = maxY;
		pClient.x = 0;
		maxY++;
	    } else {
		// else -> neuen client an maxX+1
		pClient.y = 0;
		pClient.x = maxX;
		maxX++;
	    }
	}
	Log.i(Constants.LOG_TAG, "added Client at pos "+pClient.x+":"+pClient.y);
	clients[pClient.y][pClient.x] = pClient;
	// Play
	pClient.play();
	
	clip();
    }

    private void clip() {
	// clipping für alle berechnen
	float startY = 0;
	for (int i = 0; i < maxY; i++) {
	    float startX = 0;
	    for (int j = 0; j < maxY; j++) {
		if (clients[i][j] != null) {
		    clients[i][j].startX = startX;
		    clients[i][j].startY = startY;
		    clients[i][j].endX = startX + (float)(1.0 / maxX);
		    clients[i][j].endY = startY + (float)(1.0 / maxY);
		    clients[i][j].clip();
		}
		startX = startX + (float)(1.0 / maxX);
	    }
	    startY = startY + (float)(1.0 / maxY);
	}
    }

    public void shutdown() {
	for (int i = 0; i < maxY; i++) {
	    for (int j = 0; j < maxY; j++) {
		if (null != clients[i][j])
		    Log.i(Constants.LOG_TAG, "shutdown PlayerClient "+i+":"+j);
		    clients[i][j].shutdown();
	    }
	}
    }

    public void removeClient(PlayerClient playerClient) {
	Log.i(Constants.LOG_TAG, "removeClient "+ playerClient.x+":"+playerClient.y);
	clients[playerClient.y][playerClient.x]=null;
    }
}
