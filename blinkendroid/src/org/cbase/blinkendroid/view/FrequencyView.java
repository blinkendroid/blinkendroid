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

package org.cbase.blinkendroid.view;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.OldBlinkendroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FrequencyView extends View {

    private Paint paint;
    private Paint paint2;
    public static short[] buffer;
    public static float[] spectrumData;
    public static float power;
    private float f;
    private int maxIndex;

    public FrequencyView(Context context, AttributeSet attrs) {
	super(context, attrs);
	paint = new Paint();
	paint2 = new Paint();
	paint.setColor(Color.WHITE);
	paint2.setColor(Color.BLACK);
	paint2.setTextSize(16);

	Log.d(Constants.LOG_TAG, "FrequencyView: "
		+ (OldBlinkendroid.screenWidth - 10) + ":"
		+ (OldBlinkendroid.screenHeight - 200));
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	canvas.drawColor(Color.HSVToColor(new float[] {
		(float) (maxIndex * 4.0), 1, 1 }));
	if (null != buffer) {
	    f = (65 - power) * 8;
	    for (int i = 0; i < buffer.length; i++) {
		canvas.drawPoint(i, (buffer[i] / f) + 128, paint);
	    }
	}
	if (null != spectrumData) {
	    float max = 0;
	    maxIndex = 0;
	    for (int i = 0; i < spectrumData.length; i++) {
		if (spectrumData[i] > max) {
		    max = spectrumData[i];
		    maxIndex = i;
		}
		canvas.drawLine(i * 2, 256, i * 2,
			256 - spectrumData[i] * 10000, paint2);
	    }
	    String freq = Integer.toString(4000 / 128 * maxIndex) + "Hz";
	    canvas.drawText(freq, 10, 30, paint2);
	}
    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// Log.d(Blinkendroid.LOG_TAG,
	// "onMeasure: "+MeasureSpec.getSize(widthMeasureSpec)+":"+MeasureSpec.getSize(heightMeasureSpec));
	// setMeasuredDimension(256,256);
	setMeasuredDimension(OldBlinkendroid.screenWidth - 20,
		OldBlinkendroid.screenHeight - 100/*
						   * MeasureSpec.getSize(heightMeasureSpec
						   * )
						   */);
    }
}
