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

public class PlayerView extends View implements Runnable {

    private static final int MS_PER_FRAME = 100;

    private final Paint paint = new Paint();
    private int matrix[][] = null;
    private BLM blm;
    private int startX = 0, startY = 0, endX = 0, endY = 0;
    private int frameNum = 0;
    private Handler handler = new Handler();
    private boolean playing = false;

    public PlayerView(final Context context, final BLM blm) {
	super(context);
	this.blm = blm;
	this.endX = blm.width - 1;
	this.endY = blm.height - 1;
	paint.setColor(Color.WHITE);
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
    protected void onDraw(Canvas canvas) {

	if (null == matrix)
	    return;

	int height = matrix.length;
	int width = matrix[0].length;

	int pwidth = (getWidth() - width * 3) / width;
	int pheight = (getHeight() - height * 3) / height;

	int y = 0;
	for (int i = 0; i < height; i++) {
	    int x = 0;
	    for (int j = 0; j < width; j++) {
		paint.setColor(Color.argb(255, matrix[i][j] * 16,
			matrix[i][j] * 16, matrix[i][j] * 16));
		canvas.drawRect(x, y, x + pwidth, y + pheight, paint);
		x += pwidth + 3;
	    }
	    y += pheight + 3;
	}
    }

    public void run() {

	final Frame frame = blm.frames.get(frameNum);

	matrix = clipMatrix(frame.matrix, blm.width, blm.height);
	invalidate();

	frameNum++;
	if (frameNum >= blm.frames.size())
	    frameNum = 0;

	if (playing)
	    handler.postDelayed(this, MS_PER_FRAME);
    }

    private int[][] clipMatrix(int[][] matrix, int width, int height) {

	final int[][] clippedMatrix = new int[endY - startY + 1][endX - startX
		+ 1];

	for (int i = 0; i < height; i++)
	    for (int j = 0; j < width; j++)
		if (i >= startY && i <= endY && j >= startX && j <= endX)
		    clippedMatrix[i - startY][j - startX] = matrix[i][j];

	return clippedMatrix;
    }
}
