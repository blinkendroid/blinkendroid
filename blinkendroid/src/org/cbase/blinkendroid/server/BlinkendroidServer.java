/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.network.tcp.DataServer;
import org.cbase.blinkendroid.network.udp.UDPServerProtocolManager;
import org.cbase.blinkendroid.player.bml.BLMHeader;
import org.cbase.blinkendroid.player.image.ImageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidServer {

    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidServer.class);

    volatile private boolean running = false;
    volatile private DatagramSocket serverSocket;
    private int port = -1;
    private PlayerManager playerManager;
    private List<ConnectionListener> connectionListeners;
    private WhackaMole whackAmole;
    private EffectManager effectManager;
    private UDPServerProtocolManager mServerProto;

    private DataServer videoSocket;

    public PlayerManager getPlayerManager() {
	return playerManager;
    }
    
    public BlinkendroidServer(int port) {
	this.connectionListeners = new ArrayList<ConnectionListener>();
	this.port = port;
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
	this.connectionListeners.add(connectionListener);
    }

    public void start() {

	running = true;

	try {
	    videoSocket = new DataServer(port);
	    videoSocket.start();

	    serverSocket = new DatagramSocket(port);
	    serverSocket.setBroadcast(true);
	    serverSocket.setReuseAddress(true);
	    mServerProto = new UDPServerProtocolManager(serverSocket);

	    playerManager = new PlayerManager(mServerProto);
	    playerManager.setVideoServer(videoSocket);

	    mServerProto.setPlayerManager(playerManager);

	    // register for locateME
	    mServerProto.registerHandler(BlinkendroidApp.PROTOCOL_CLIENT, playerManager);
	    // register for& touch
	    effectManager = new EffectManager(playerManager);
	    
	    // setting default touch effect
	    ITouchEffect effect = new InverseEffect(playerManager);
	    effectManager.setEffect(effect);
	    
	    mServerProto.registerHandler(BlinkendroidApp.PROTOCOL_CLIENT, effectManager);

	    // mServerProto.registerHandler(proto, playerManager);
	    for (ConnectionListener connectionListener : connectionListeners) {
		mServerProto.addConnectionListener(connectionListener);
	    }

	    mServerProto.startTimerThread();

	    // how is the protocol connected to the logic ?
	} catch (SocketException e) {
	    logger.error("SocketException", e);
	} catch (IOException e) {
	    logger.error("IOException", e);
	}
    }

    public void shutdown() {
	if (null != whackAmole)
	    whackAmole.shutdown();
	if (null != videoSocket)
	    videoSocket.shutdown();
	if (null != playerManager)
	    playerManager.shutdown();
	if (null != mServerProto)
	    mServerProto.shutdown();
	if (null != serverSocket)
	    serverSocket.close();
    }

    public boolean isRunning() {
	return running;
    }

    public void switchMovie(BLMHeader blmHeader) {
	playerManager.switchMovie(blmHeader);
    }

    public void switchImage(ImageHeader imageHeader) {
	playerManager.switchImage(imageHeader);
    }
    
    public void setTouchEffect(ITouchEffect effect) {
	effectManager.setEffect(effect);
    }

    public void clip() {
	playerManager.clip(true);
    }

    public void singleclip() {
	playerManager.singleclip();
    }

    public void toggleTimeThread() {
	if (mServerProto.isGlobalTimerThreadRunning())
	    mServerProto.stopTimerThread();
	else
	    mServerProto.startTimerThread();
    }

    public void toggleWhackaMole() {
	if (null != whackAmole && whackAmole.isRunning()) {
	    mServerProto.unregisterHandler(BlinkendroidApp.PROTOCOL_CLIENT, whackAmole);
	    whackAmole.shutdown();
	} else {
	    whackAmole = new WhackaMole(playerManager);
	    mServerProto.registerHandler(BlinkendroidApp.PROTOCOL_CLIENT, whackAmole);
	    whackAmole.start();
	}
    }
}
