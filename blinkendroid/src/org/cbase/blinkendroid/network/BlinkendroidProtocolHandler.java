package org.cbase.blinkendroid.network;

import java.util.StringTokenizer;


public class BlinkendroidProtocolHandler implements ICommandHandler {
    BlinkendroidListener playerListener;
    public BlinkendroidProtocolHandler(BlinkendroidListener playerListener) {
	this.playerListener=playerListener;
    }

    public void handle(byte[] data) {
	String input = new String(data);
	if (input.startsWith("T"))
	    playerListener.serverTime(Long.parseLong(input.substring(1)));
	else if (input.startsWith("C")) {
	    // clipping "Cstartx,starty,endx,endy"
	    StringTokenizer tokenizer = new StringTokenizer(input.substring(1),
		    ",");
	    int startX = Integer.parseInt(tokenizer.nextToken());
	    int startY = Integer.parseInt(tokenizer.nextToken());
	    int endX = Integer.parseInt(tokenizer.nextToken());
	    int endY = Integer.parseInt(tokenizer.nextToken());
	    playerListener.clip(startX, startY, endX, endY);
	}

    }
}
