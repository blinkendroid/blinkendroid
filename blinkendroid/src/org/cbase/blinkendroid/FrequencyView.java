package org.cbase.blinkendroid;

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
	private int	maxIndex;
    public FrequencyView(Context context, AttributeSet attrs) {
      super(context, attrs);
    	paint = new Paint();
    	paint2 = new Paint();
    	paint.setColor(Color.WHITE);
    	paint2.setColor(Color.BLACK);
    	paint2.setTextSize(16);
    	
    	Log.d(Blinkendroid.LOG_TAG, "FrequencyView: "+(Blinkendroid.screenWidth-10)+":"+(Blinkendroid.screenHeight-200));
    }


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.HSVToColor(new float[]{(float)(maxIndex*4.0),1,1}));
		if(null!=buffer)
		{
			f=(65-power)*8;
			for (int i = 0; i < buffer.length; i++) {
				canvas.drawPoint(i, (buffer[i]/f)+128, paint);
			}
		}
		if(null!=spectrumData)
		{
			float max=0;
			maxIndex=0;
			for (int i = 0; i < spectrumData.length; i++) {
				if(spectrumData[i]>max){
					max=spectrumData[i];
					maxIndex=i;
				}
				canvas.drawLine(i*2, 256, i*2, 256-spectrumData[i]*10000, paint2);
			}
			String freq	=	Integer.toString(4000/128*maxIndex)+"Hz";
			canvas.drawText(freq, 10,30, paint2);
		}
	}

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    	Log.d(Blinkendroid.LOG_TAG, "onMeasure: "+MeasureSpec.getSize(widthMeasureSpec)+":"+MeasureSpec.getSize(heightMeasureSpec));
//    	setMeasuredDimension(256,256);
    	setMeasuredDimension(Blinkendroid.screenWidth-20,Blinkendroid.screenHeight-100/*MeasureSpec.getSize(heightMeasureSpec)*/);
    }
}
