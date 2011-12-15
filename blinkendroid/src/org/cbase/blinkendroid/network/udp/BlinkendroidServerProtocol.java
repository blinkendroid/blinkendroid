package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidServerProtocol extends BlinkendroidProtocol {

    private ClientSocket mClientSocket;
    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidServerProtocol.class);

    public BlinkendroidServerProtocol(ClientSocket clientSocket) {
	mClientSocket = clientSocket;
    }

    public void play(long startTime, int dataType) {
	/**
	 * PLAY length of string (0 for default) string
	 */

	ByteBuffer out = ByteBuffer.allocate(1024);
	try {
	    out.putInt(COMMAND_PLAY);
	    out.putInt(dataType);
	    //TODO put the video name here.
	    out.putLong(startTime);
	    send(out);
	} catch (IOException e) {
	    e.printStackTrace();
	    logger.error("play failed", e);
	}
    }

    public void arrow(int degrees, int color) {
	try {
	    ByteBuffer out = ByteBuffer.allocate(1024);
	    out.putInt(COMMAND_INIT);
	    out.putInt(degrees);
	    out.putInt(color);

	    send(out);
	} catch (IOException e) {
	    logger.error("arrow failed ", e);
	}
    }

    public void clip(float startX, float startY, float endX, float endY) {
	try {
	    ByteBuffer out = ByteBuffer.allocate(1024);
	    out.putInt(COMMAND_CLIP);
	    out.putFloat(startX);
	    out.putFloat(startY);
	    out.putFloat(endX);
	    out.putFloat(endY);
	    send(out);
	    logger.info("clip flushed ");
	} catch (IOException e) {
	    logger.error("clip failed ", e);
	}
    }

    protected void send(ByteBuffer command) throws IOException {
	ByteBuffer out = ByteBuffer.allocate(command.position() + Integer.SIZE);
	out.putInt(BlinkendroidApp.PROTOCOL_PLAYER); /* protocol header */
	out.put(command.array(), 0, command.position());
	mClientSocket.send(out);
    }

    public void mole(int style, int moleCounter, int duration, int points) {
	try {
	    ByteBuffer out = ByteBuffer.allocate(1024);
	    out.putInt(COMMAND_MOLE);
	    out.putInt(style);
	    out.putInt(moleCounter);
	    out.putInt(duration);
	    out.putInt(points);
	    send(out);
	} catch (IOException e) {
	    logger.error("mole failed ", e);
	}
    }

    public void blink(int type) {
	try {
	    ByteBuffer out = ByteBuffer.allocate(16);
	    out.putInt(COMMAND_BLINK);
	    out.putInt(type);
	    send(out);
	    logger.info("blink flushed ");
	} catch (IOException e) {
	    logger.error("blink failed ", e);
	}
    }
}
