package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;

import android.util.Log;

public class PlayerManager {

    private PlayerClient[][] clients = new PlayerClient[10][10];
    private int maxX = 1, maxY = 1;
    private long startTime = 0;

    public void addClient(BlinkendroidProtocol blinkendroidProtocol) {
	if (startTime == 0)
	    startTime = System.currentTimeMillis();
	PlayerClient pClient = new PlayerClient(this, blinkendroidProtocol,
		startTime);
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
	Log.i(Constants.LOG_TAG, "added Client at pos " + pClient.x + ":"
		+ pClient.y);
	clients[pClient.y][pClient.x] = pClient;

	// Testing arrow allocation:
	// int posOffsets[] = { -1, 0, +1 };
	//
	// for (int i = 0; i < posOffsets.length; i++) {
	// int xOffset = posOffsets[i];
	//
	// for (int j = 0; j < posOffsets.length; j++) {
	// int yOffset = posOffsets[j];
	// if (!(xOffset == 0 && yOffset == 0)
	// && pClient.x + xOffset < clients.length && pClient.x + xOffset >= 0
	// && pClient.y + yOffset < clients[pClient.x].length
	// && pClient.y + yOffset >= 0) {
	// if (clients[i][j] != null) {
	// //TODO change to a variable value:
	// clients[i][j].arrow(360);
	// Log.d(Constants.LOG_TAG, "Arrow set to " + 360 + " degrees.");
	// }
	// // System.out.println("Adding " + (position.x + xOffset) +
	// // ";" + (position.y + yOffset) + " as neightbour");
	// }
	// }
	// }
	arrow(pClient);
	// Play
	pClient.play();

	clip();
    }

    private void arrow(final PlayerClient pClient) {
	arrow(pClient, 0, 1, 0);
	arrow(pClient, -1, 1, 45);
	arrow(pClient, -1, 0, 90);
	arrow(pClient, -1, -1, 135);
	arrow(pClient, 0, -1, 180);
	arrow(pClient, +1, -1, 225);
	arrow(pClient, +1, 0, 270);
	arrow(pClient, +1, 1, 315);
    }

    private void arrow(final PlayerClient pClient, final int dx, final int dy,
	    final int deg) {
	if (pClient.y + dy >= 0 && pClient.x + dx >= 0
		&& null != clients[pClient.y + dy][pClient.x + dx]) {
	    clients[pClient.y + dy][pClient.x + dx].arrow(deg);
	}
    }

    private void clip() {
	// clipping für alle berechnen
	// Log.i(Constants.LOG_TAG, "clip maxX "+i+":"+j);
	float startY = 0;
	for (int i = 0; i < maxY; i++) {
	    float startX = 0;
	    for (int j = 0; j < maxX; j++) {
		if (clients[i][j] != null) {
		    clients[i][j].startX = startX;
		    clients[i][j].startY = startY;
		    clients[i][j].endX = startX + (float) (1.0 / maxX);
		    clients[i][j].endY = startY + (float) (1.0 / maxY);
		    clients[i][j].clip();
		}
		startX = startX + (float) (1.0 / maxX);
	    }
	    startY = startY + (float) (1.0 / maxY);
	}
    }

    public void shutdown() {
	for (int i = 0; i < maxY; i++) {
	    for (int j = 0; j < maxY; j++) {
		if (null != clients[i][j]) {
		    Log.i(Constants.LOG_TAG, "shutdown PlayerClient " + i + ":"
			    + j);
		    clients[i][j].shutdown();
		}
	    }
	}
    }

    public void removeClient(PlayerClient playerClient) {
	Log.i(Constants.LOG_TAG, "removeClient " + playerClient.x + ":"
		+ playerClient.y);
	clients[playerClient.y][playerClient.x] = null;
	// boolean newMaxX=true;
	// for (int i = 0; i < maxY; i++) {
	// if(clients[i][maxX]!=null){
	// newMaxX=false;
	// break;
	// }
	// }
	// if(newMaxX){
	// maxX--;
	// Log.i(Constants.LOG_TAG, "newMaxX "+maxX);
	// }
	//	
	// boolean newMaxY=true;
	// for (int i = 0; i < maxX; i++) {
	// if(clients[maxY][i]!=null){
	// newMaxY=false;
	// break;
	// }
	// }
	// if(newMaxY){
	// maxY--;
	// Log.i(Constants.LOG_TAG, "newMaxY "+maxY);
	// }
	clip();
    }
    //TODO
}
