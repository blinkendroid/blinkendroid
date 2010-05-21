package org.cbase.blinkendroid.network;

import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class BlinkendroidClientProtocol extends AbstractBlinkendroidProtocol implements CommandHandler{
    BlinkendroidListener listener;
    protected BlinkendroidClientProtocol(Socket socket,
	    BlinkendroidListener listener) throws IOException {
	super(socket, listener,false);
	this.listener=listener;
	registerHandler(PROTOCOL_PLAYER, this);   
    }

    public void handle(final String command) {
	System.out.println("received: " + command);
	if (listener != null) {
	    if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_PLAYER_TIME)) {
		listener.serverTime(Long.parseLong(command.substring(1)));
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_CLIP)) {
		final StringTokenizer tokenizer = new StringTokenizer(command
			.substring(1), ",");
		final float startX = Float.parseFloat(tokenizer.nextToken());
		final float startY = Float.parseFloat(tokenizer.nextToken());
		final float endX = Float.parseFloat(tokenizer.nextToken());
		final float endY = Float.parseFloat(tokenizer.nextToken());
		listener.clip(startX, startY, endX, endY);
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_PLAY)) {
		final StringTokenizer tokenizer = new StringTokenizer(command
			.substring(1), ",");
		final int x = Integer.parseInt(tokenizer.nextToken());
		final int y = Integer.parseInt(tokenizer.nextToken());
		final int resId = Integer.parseInt(tokenizer.nextToken());
		final long serverTime = Long.parseLong(tokenizer.nextToken());
		final long startTime = Long.parseLong(tokenizer.nextToken());
		listener.serverTime(serverTime);
		listener.play(x, y, resId, startTime);
	    } else if (command.startsWith(AbstractBlinkendroidProtocol.COMMAND_INIT)) {
		final int degrees = Integer.parseInt(command.substring(1));
		listener.arrow(2500, degrees);
	    }
	}
    }
}
