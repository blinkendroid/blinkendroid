package org.cbase.blinkendroid.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

import org.cbase.blinkendroid.player.bml.BBMZParser;
import org.cbase.blinkendroid.player.bml.BLM;

public class BlinkendroidClientProtocol extends AbstractBlinkendroidProtocol
	implements CommandHandler {
    BlinkendroidListener listener;

    protected BlinkendroidClientProtocol(Socket socket,
	    BlinkendroidListener listener) throws IOException {
	super(socket, listener, false);
	this.listener = listener;
	registerHandler(PROTOCOL_PLAYER, this);
    }

    public void handle(BufferedInputStream in) throws IOException {
	Integer command = readInt(in);
	// System.out.println("received: " + command);
	if (listener != null) {
	    if (command == COMMAND_PLAYER_TIME) {
		listener.serverTime(readLong(in));
	    } else if (command == COMMAND_CLIP) {
		final float startX = readFloat(in);
		final float startY = readFloat(in);
		final float endX = readFloat(in);
		final float endY = readFloat(in);
		System.out.println("clip: " + startX + "," + startY + ","
			+ endX + "," + endY);
		listener.clip(startX, startY, endX, endY);
	    } else if (command == COMMAND_PLAY) {
		final int x = readInt(in);
		final int y = readInt(in);
		final long serverTime = readLong(in);
		final long startTime = readLong(in);
		final long length = readLong(in);

		BBMZParser parser = new BBMZParser();
		BLM blm = null;
		// if length == 0 play default
		if (length == 0) {
		}
		// else read BLM
		else {
		    blm = parser.parseBBMZ(in, length);
		    // while (in.read(buffer) != -1) {
		    // inputLine= ByteBuffer.wrap(buffer).getInt();
		    // if (!running) // fast exit
		    // break;
		    // }
		    long length2 = readLong(in);
		    System.out.println("play length1 " + length + " length2:"
			    + length2);
		}

		listener.serverTime(serverTime);
		listener.play(x, y, startTime, blm);
	    } else if (command == COMMAND_INIT) {
		final int degrees = readInt(in);
		final int color = readInt(in);
		listener.arrow(4000, degrees, color);
	    } else if (command == COMMAND_SHUTDOWN) {
		listener.shutdown();
	    }
	}
    }
}