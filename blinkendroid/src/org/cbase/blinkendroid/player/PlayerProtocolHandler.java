package org.cbase.blinkendroid.player;

import java.util.StringTokenizer;

import org.cbase.blinkendroid.network.ICommandHandler;

public class PlayerProtocolHandler implements ICommandHandler {

    public void handle(byte[] data) {
	String input = new String(data);
	if (input.startsWith("T"))
	    // playerThread.setGlobalTime(Long.parseLong(input.substring(1)))
	    ;
	else if (input.startsWith("C")) {
	    // clipping "Cstartx,starty,endx,endy"
	    StringTokenizer tokenizer = new StringTokenizer(input.substring(1),
		    ",");
	    int startX = Integer.parseInt(tokenizer.nextToken());
	    int startY = Integer.parseInt(tokenizer.nextToken());
	    int endX = Integer.parseInt(tokenizer.nextToken());
	    int endY = Integer.parseInt(tokenizer.nextToken());
	    // playerThread.setClipping(startX, startY, endX, endY);
	}

    }
}
