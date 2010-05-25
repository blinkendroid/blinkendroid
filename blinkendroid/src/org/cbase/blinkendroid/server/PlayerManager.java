package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidServerProtocol;
import org.cbase.blinkendroid.player.bml.BLMHeader;

import android.util.Log;

public class PlayerManager {

    private PlayerClient[][] clients = new PlayerClient[20][20];
    private int maxX = 1, maxY = 1;
    private long startTime = 0;
    private boolean running = true;
    private String filename=null;
    public synchronized void addClient(
	    BlinkendroidServerProtocol blinkendroidProtocol) {
	if (!running) {
	    Log.e(Constants.LOG_TAG,
		    "PlayerManager not running ignore addClient ");
	    return;
	}
	if (startTime == 0)
	    startTime = System.currentTimeMillis();
	PlayerClient pClient = new PlayerClient(this, blinkendroidProtocol,
		startTime);

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

	pClient.play(filename);
	arrow(pClient);
	if (!found)
	    clip(true);
	else {
	    clip(false);
	    pClient.clip();
	}
	// server starts thread to send globaltime
	blinkendroidProtocol.startTimerThread();
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

    private void clip(boolean clipAll) {
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
		    if (clipAll)
			clients[i][j].clip();
		}
		startX = startX + (float) (1.0 / maxX);
	    }
	    startY = startY + (float) (1.0 / maxY);
	}
    }

    public synchronized void shutdown() {
	running = false;
	Log.i(Constants.LOG_TAG, "PlayerManager.shutdown() start");
	for (int i = 0; i < maxY; i++) {
	    for (int j = 0; j < maxY; j++) {
		if (null != clients[i][j]) {
		    Log.i(Constants.LOG_TAG, "shutdown PlayerClient " + i + ":"
			    + j);
		    clients[i][j].shutdown();
		}
	    }
	}
	Log.i(Constants.LOG_TAG, "PlayerManager.shutdown() end!!!");

    }

    public synchronized void removeClient(PlayerClient playerClient) {
	if (!running) {
	    Log.e(Constants.LOG_TAG,
		    "PlayerManager not running ignore removeClient");
	    return;
	}

	Log.i(Constants.LOG_TAG, "removeClient " + playerClient.x + ":"
		+ playerClient.y);
	clients[playerClient.y][playerClient.x] = null;
	boolean newMaxX = true;
	for (int i = 0; i < maxY; i++) {
	    if (clients[i][maxX-1] != null) {
		newMaxX = false;
		break;
	    }
	}
	if (newMaxX) {
	    maxX--;
	    Log.i(Constants.LOG_TAG, "newMaxX " + maxX);
	}

	boolean newMaxY = true;
	for (int i = 0; i < maxX; i++) {
	    if (clients[maxY-1][i] != null) {
		newMaxY = false;
		break;
	    }
	}
	if (newMaxY) {
	    maxY--;
	    Log.i(Constants.LOG_TAG, "newMaxY " + maxY);
	}
	clip(true);
    }

    // TODO

    public void switchMovie(BLMHeader blmHeader) {
	this.filename=blmHeader.filename;
	Log.i(Constants.LOG_TAG, "switch to movie " + blmHeader.title);
	for (int i = 0; i < maxY; i++) {
	    for (int j = 0; j < maxY; j++) {
		if (null != clients[i][j]) {
		    Log.i(Constants.LOG_TAG, "play PlayerClient " + i + ":"
			    + j+" "+filename);
		    clients[i][j].play(filename);
		}
	    }
	}
    }
}
