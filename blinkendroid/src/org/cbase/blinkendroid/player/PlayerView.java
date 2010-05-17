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
import org.cbase.blinkendroid.player.bml.BLM.Frame;

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

    private final BLM blm;
    private int startX, startY, endX, endY;
    private boolean playing = false;
    private int frameNum = 0;

    private final Handler handler = new Handler();
    private final Paint paint = new Paint();

    private static final int MS_PER_FRAME = 100;
    private static final int PIXEL_PADDING = 1;

    public PlayerView(final Context context, final BLM blm) {
	super(context);
	this.blm = blm;
	this.endX = blm.width;
	this.endY = blm.height;
	handler.post(this);
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

	final Frame frame = blm.frames.get(frameNum);
	final int[][] matrix = frame.matrix;

	final float pixelWidth = getWidth() / (endX - startX);
	final float pixelHeight = getHeight() / (endY - startY);

	// clip
	for (int y = startY; y < endY; y++) {
	    final int clippedY = y - startY;
	    for (int x = startX; x < endX; x++) {
		final int clippedX = x - startX;
		final int value = matrix[y][x] * 16;
		paint.setColor(Color.argb(255, value, value, value));
		canvas.drawRect(pixelWidth * clippedX + PIXEL_PADDING,
			pixelHeight * clippedY + PIXEL_PADDING, pixelWidth
				* (clippedX + 1) - PIXEL_PADDING, pixelHeight
				* (clippedY + 1) - PIXEL_PADDING, paint);
	    }
	}
    }

    public void run() {

	invalidate();

	frameNum++;
	if (frameNum >= blm.frames.size())
	    frameNum = 0;

	if (playing)
	    handler.postDelayed(this, MS_PER_FRAME);
    }
}
