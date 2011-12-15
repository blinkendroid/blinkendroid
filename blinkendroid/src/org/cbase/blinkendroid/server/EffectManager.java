package org.cbase.blinkendroid.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.network.udp.BlinkendroidProtocol;
import org.cbase.blinkendroid.network.udp.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectManager implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(EffectManager.class);
    PlayerManager playerManager;
    ITouchEffect effect;
    
    public EffectManager(PlayerManager playerManager) {
	this.playerManager = playerManager;
    }
    
    public void setEffect(ITouchEffect effect) {
	this.effect = effect;
    }

    public void handle(SocketAddress from, ByteBuffer in) throws IOException {
	final PlayerClient playerClient = playerManager.getPlayerClientBySocketAddress(from);
	int command = in.getInt();
	if (null == playerClient) {
	    logger.error("PlayerClient from command not found " + command);
	    return;
	}

	if (command == BlinkendroidProtocol.COMMAND_TOUCH) {
	    touch(playerClient);
	}
    }

    private void touch(final PlayerClient playerClient) {
	
	if(effect == null || playerClient == null) {
	    return;
	}
	
	logger.info("touch from " + playerClient.toString());

	new Thread() {
	    @Override
	    public void run() {
		effect.showEffect(playerClient);
	    }

	}.start();
    }
}
