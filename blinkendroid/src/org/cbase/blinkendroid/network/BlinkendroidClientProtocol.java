package org.cbase.blinkendroid.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

public class BlinkendroidClientProtocol extends AbstractBlinkendroidProtocol implements CommandHandler{
    BlinkendroidListener listener;
    protected BlinkendroidClientProtocol(Socket socket,
	    BlinkendroidListener listener) throws IOException {
	super(socket, listener,false);
	this.listener=listener;
	registerHandler(PROTOCOL_PLAYER, this);   
    }

    //TLV
    public void handle(BufferedInputStream in) {
	Integer command = readInt(in);
	System.out.println("received: " + command);
	if (listener != null) {
	    if (command==COMMAND_PLAYER_TIME) {
		listener.serverTime(readLong(in));
	    } else if (command==COMMAND_CLIP) {
		final float startX =	readFloat(in);
		final float startY = readFloat(in);
		final float endX =readFloat(in);
		final float endY = readFloat(in);
		listener.clip(startX, startY, endX, endY);
	    } else if (command==COMMAND_PLAY) {
		final int x = readInt(in);
		final int y = readInt(in);
		final int resId = readInt(in);
		final long serverTime = readLong(in);
		final long startTime =readLong(in);
		listener.serverTime(serverTime);
		listener.play(x, y, resId, startTime);
	    } else if (command==COMMAND_INIT) {
		final int degrees = readInt(in);
		listener.arrow(2500, degrees);
	    }                      
	}                          
    }
}
