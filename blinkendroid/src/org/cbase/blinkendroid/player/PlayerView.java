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
import android.view.View;

/**
 * @author Andreas Schildbach
 */
public class PlayerView extends View implements Runnable {

    private BLM blm;
    private int startX, startY, endX, endY;
    private boolean playing = false;
    private long startedTime;
    private long[] frameTime;
    private int numFrames;
    private int frame = 0;
    private long duration;

    private final Handler handler = new Handler();
    private final Paint paint = new Paint();

    private static final int PIXEL_PADDING = 1;

    public PlayerView(final Context context, final BLM blm) {
	super(context);
	init(blm);
    }

    private void init(final BLM blm) {
	this.blm = blm;
	this.endX = blm.width;
	this.endY = blm.height;
	this.numFrames = blm.frames.size();
	long t = 0;
	frameTime = new long[numFrames + 1];
	for (int i = 0; i < numFrames; i++) {
	    frameTime[i] = t;
	    t += blm.frames.get(i).duration;
	}
	frameTime[numFrames] = t;
	duration = t;
    }

    public void setClipping(int startX, int startY, int endX, int endY) {
	this.startX = startX;
	this.startY = startY;
	this.endX = endX;
	this.endY = endY;
    }

    public void startPlaying() {
	if (!playing) {
	    playing = true;
	    startedTime = System.currentTimeMillis();
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

	final int[][] matrix = blm.frames.get(frame).matrix;

	final float pixelWidth = getWidth() / (endX - startX);
	final float pixelHeight = getHeight() / (endY - startY);

	// clip
	for (int y = startY; y < endY; y++) {
	    final int clippedY = y - startY;
	    final int[] row = matrix[y];
	    for (int x = startX; x < endX; x++) {
		final int clippedX = x - startX;
		final int value = row[x] * 16;
		paint.setColor(Color.argb(255, value, value, value));
		canvas.drawRect(pixelWidth * clippedX + PIXEL_PADDING,
			pixelHeight * clippedY + PIXEL_PADDING, pixelWidth
				* (clippedX + 1) - PIXEL_PADDING, pixelHeight
				* (clippedY + 1) - PIXEL_PADDING, paint);
	    }
	}
    }

    public void run() {

	// time into movie, taking endless looping into account
	long time = (System.currentTimeMillis() - startedTime) % duration;

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
}
