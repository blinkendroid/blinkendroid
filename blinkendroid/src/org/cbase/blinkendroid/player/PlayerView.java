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

import org.cbase.blinkendroid.Player;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PlayerView extends View {

	private Paint paint;
	private Paint paint2;
	private int width;
	private int height;
	private int frame;
	public long lastFrameShowed=0;
	public long lastFrame=0;
	private int matrix[][]=null;
	

	public PlayerView(Player player) {
		super(player);
		paint = new Paint();
		paint2 = new Paint();
		paint.setColor(Color.WHITE);
		paint2.setColor(Color.BLACK);
		paint2.setTextSize(16);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawColor(Color.BL);
//		canvas.drawText(Long.toString(System.currentTimeMillis()), 100, 100, paint);
//		if(width>height){
//			getW
//		}else{
//			
//		}
		if(null==matrix)
			return;
		int pwidth=(getWidth()-width*3)/width;
		int pheight=(getHeight()-height*3)/height;
		
		int y=0;
		for (int i = 0; i < height; i++) {
//			Log.i("PlayerView", "draw row "+i);
			int x=0;
			for (int j = 0; j < width; j++) {
				paint.setColor(Color.argb(255, matrix[i][j]*16, matrix[i][j]*16, matrix[i][j]*16));
				canvas.drawRect(x, y, x+pwidth, y+pheight, paint);
				x+=pwidth+3;
			}
			y+=pheight+3;
		}
		if(0==lastFrameShowed){
			lastFrameShowed=System.currentTimeMillis();
			lastFrame=frame;
		}
	}

	public void setMatrix(int[][] matrix, int width, int height, int frame) {
		this.matrix=matrix;
		this.width=width;
		this.height=height;
		this.frame=frame;
		this.lastFrameShowed=0;
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// Log.d(Blinkendroid.LOG_TAG,
//		// "onMeasure: "+MeasureSpec.getSize(widthMeasureSpec)+":"+MeasureSpec.getSize(heightMeasureSpec));
//		// setMeasuredDimension(256,256);
//		setMeasuredDimension(OldBlinkendroid.screenWidth - 20,
//				OldBlinkendroid.screenHeight - 100/*
//												 * MeasureSpec.getSize(heightMeasureSpec
//												 * )
//												 */);
//	}
}
