package org.cbase.blinkendroid.network;

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

import java.util.StringTokenizer;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

public class BlinkendroidProtocolHandler implements ICommandHandler {

    private final BlinkendroidListener playerListener;

    public BlinkendroidProtocolHandler(final BlinkendroidListener playerListener) {
	this.playerListener = playerListener;
    }

    public void handle(byte[] data) {
	Log.i(Constants.LOG_TAG, "BlinkendroidProtocolHandler recieved "
		+ new String(data));
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
	    Log.i(Constants.LOG_TAG, "Play " + x + "," + y);
	    playerListener.serverTime(serverTime);
	    playerListener.play(resId, startTime);
	} else if (input.startsWith(BlinkendroidProtocol.COMMAND_INIT)) {
	    // I ::= 0 <= degrees <= 359
	    int degrees = Integer.parseInt(input.substring(1));
	    // Sanity check:
	    if (degrees >= 0 && degrees <= 360) {
		playerListener.arrow(2500, degrees);
		Log.d(Constants.LOG_TAG, "Got arrow input from server!");
	    } else {
		Log.e(Constants.LOG_TAG, "Wrong degree value " + degrees);
	    }
	}

    }
}
