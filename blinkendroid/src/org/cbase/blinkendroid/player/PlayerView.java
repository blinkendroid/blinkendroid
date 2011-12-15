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

package org.cbase.blinkendroid.player;

import org.cbase.blinkendroid.player.bml.BLM;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;

/**
 * @author Andreas Schildbach
 */
public class PlayerView extends ClippableView implements Runnable, BlinkenView {

    private BLM blm;
    private boolean playing = false;
    private long startTime;
    private long timeDelta = 0;
    private long[] frameTime;
    private int numFrames;
    private int frame = 0;
    private long duration;
    private boolean blink = false;
    private final Handler handler = new Handler();
    private final Paint paint = new Paint();

    private static final int PIXEL_PADDING = 1;

    public PlayerView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public void setBLM(final BLM blm) {

	// trying to free some memory first
	System.gc();

	this.blm = blm;
	this.numFrames = blm.frames.size();
	long time = 0;
	frameTime = new long[numFrames + 1];
	for (int i = 0; i < numFrames; i++) {
	    frameTime[i] = time;
	    time += blm.frames.get(i).duration;
	}
	frameTime[numFrames] = time;
	duration = time;
	frame = 0;
    }

    public void setStartTime(long startTime) {
	this.startTime = startTime;
    }

    public void setTimeDelta(long timeDelta) {
	this.timeDelta = timeDelta;
    }

    public void startPlaying() {
	if (!playing) {
	    playing = true;
	    handler.post(this);
	}
    }

    public void stopPlaying() {
	if (playing) {
	    playing = false;
	    handler.removeCallbacks(this);
	}
    }

    @Override
    protected void onDraw(final Canvas canvas) {

	if (blm != null) {

	    final byte[][] matrix = blm.frames.get(frame).matrix;

	    final int absStartX = (int) (blm.header.width * startX);
	    final int absStartY = (int) (blm.header.height * startY);
	    final int absEndX = (int) (blm.header.width * endX);
	    final int absEndY = (int) (blm.header.height * endY);

	    final float pixelWidth = (float) getWidth() / (absEndX - absStartX);
	    final float pixelHeight = (float) getHeight() / (absEndY - absStartY);

	    // clip
	    int red = 0;
	    int green = 0;
	    int blue = 0;
	    for (int y = absStartY; y < absEndY; y++) {
		final int clippedY = y - absStartY;
		final byte[] row = matrix[y];
		for (int x = absStartX; x < absEndX; x++) {
		    final int clippedX = x - absStartX;
		    final int value = row[x] << (8 - blm.header.bits);
		    if (blm.header.color) {
			if (blm.header.bits == 6) {
			    red = ((row[x] & 48) >> 4) * 64;
			    green = ((row[x] & 12) >> 2) * 64;
			    blue = (row[x] & 3) * 64;
			} else if (blm.header.bits == 8) {
			    red = ((row[x] & 224) >> 5) * 32;
			    green = ((row[x] & 28) >> 2) * 32;
			    blue = (row[x] & 3) * 64;
			}
			// Log.d(Constants.LOG_TAG, r+","+g+","+b+":"+
			// row[x]+";");
			paint.setColor(Color.argb(255, blink ? 255 - red : red, blink ? 255 - green : green,
				blink ? 255 - blue : blue));
		    } else {
			paint.setColor(Color.argb(255, blink ? 255 - value : value, blink ? 255 - value : value,
				blink ? 255 - value : value));
		    }
		    canvas.drawRect(pixelWidth * clippedX + PIXEL_PADDING, pixelHeight * clippedY + PIXEL_PADDING,
			    pixelWidth * (clippedX + 1) - PIXEL_PADDING, pixelHeight * (clippedY + 1) - PIXEL_PADDING,
			    paint);
		}
	    }
	}
    }

    public void run() {

	// time into movie, taking endless looping into account
	final long serverTime = System.nanoTime() / 1000000 - timeDelta;
	long time = (serverTime - startTime) % duration;
	if (time < 0)
	    time = duration + time;

	// determine frame to be displayed
	long nextFrameTime;
	while (true) {
	    if (time < frameTime[frame]) {
		frame--;
		continue;
	    }
	    nextFrameTime = frameTime[frame + 1];
	    if (time >= nextFrameTime) {
		frame++;
		continue;
	    }
	    break;
	}

	// display frame asap
	invalidate();

	// wait until next frame
	handler.postDelayed(this, nextFrameTime - time);
    }

    public void blink(int type) {
	// logger.info("blink " + type);
	blink = true;
	handler.postDelayed(new Runnable() {
	    public void run() {
		blink = false;
	    }
	}, 500);
    }
}
