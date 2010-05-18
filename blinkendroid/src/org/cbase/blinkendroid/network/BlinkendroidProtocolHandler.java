package org.cbase.blinkendroid.network;

import java.util.StringTokenizer;

import org.cbase.blinkendroid.Constants;

import android.util.Log;


public class BlinkendroidProtocolHandler implements ICommandHandler {
    BlinkendroidListener playerListener;
    public BlinkendroidProtocolHandler(BlinkendroidListener playerListener) {
	this.playerListener=playerListener;
    }

    public void handle(byte[] data) {
	   Log.i(Constants.LOG_TAG,"BlinkendroidProtocolHandler recieved "+new String(data));
	String input = new String(data);
	if (input.startsWith(BlinkendroidProtocol.COMMAND_PLAYER_TIME)) {
	    playerListener.serverTime(Long.parseLong(input.substring(1)));
	} else if (input.startsWith(BlinkendroidProtocol.COMMAND_CLIP)) {
	    // clipping "Cstartx,starty,endx,endy"
	    StringTokenizer tokenizer = new StringTokenizer(input.substring(1),
		    ",");
	    float startX = Float.parseFloat(tokenizer.nextToken());
	    float startY = Float.parseFloat(tokenizer.nextToken());
	    float endX = Float.parseFloat(tokenizer.nextToken());
	    float endY = Float.parseFloat(tokenizer.nextToken());
	    playerListener.clip(startX, startY, endX, endY);
	} else if (input.startsWith(BlinkendroidProtocol.COMMAND_PLAY)) {
	    // clipping "Cstartx,starty,endx,endy"
	    StringTokenizer tokenizer = new StringTokenizer(input.substring(1),
		    ",");
	    int x = Integer.parseInt(tokenizer.nextToken());
	    int y = Integer.parseInt(tokenizer.nextToken());
	    int resId = Integer.parseInt(tokenizer.nextToken());
	    long serverTime = Long.parseLong(tokenizer.nextToken());
	    long startTime = Long.parseLong(tokenizer.nextToken());
	    Log.i(Constants.LOG_TAG,"Play "+x+","+y);
	    playerListener.serverTime(serverTime);
	    playerListener.play(resId, startTime);
	} else if (input.startsWith(BlinkendroidProtocol.COMMAND_INIT)) {
	    //I ::= 0 <= degrees <= 359
	    int degrees = Integer.parseInt(input.substring(1));
	    //Sanity check:
	    if (degrees >= 0 && degrees <= 360) {
		playerListener.arrow(1000, degrees);
		Log.d(Constants.LOG_TAG, "Got arrow input from server!");
	    } else {
		Log.e(Constants.LOG_TAG, "Wrong degree value " + degrees);
	    }
	}

    }
}
