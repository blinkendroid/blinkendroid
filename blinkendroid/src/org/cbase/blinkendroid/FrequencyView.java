package org.cbase.blinkendroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FrequencyView extends View {
   
	private Paint paint;
	private Paint paint2;
	public static short[] buffer;
	public static float[] spectrumData;
	public static float power;
	private float f;
    public FrequencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    	paint = new Paint();
    	paint2 = new Paint();
    	paint.setColor(Color.WHITE);
    	paint2.setColor(Color.RED);
    	paint2.setTextSize(16);
    }


	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		canvas.drawColor(Color.BLUE);
		if(null!=buffer)
		{
			f=(65-power)*16;
			for (int i = 0; i < buffer.length; i++) {
				canvas.drawPoint(i, (buffer[i]/power)+128, paint);
			}
		}
		if(null!=spectrumData)
		{
			float max=0;
			int maxIndex=0;
			for (int i = 0; i < spectrumData.length; i++) {
				if(spectrumData[i]>max){
					max=spectrumData[i];
					maxIndex=i;
				}
				canvas.drawLine(i*2, 256, i*2, 256-spectrumData[i]*5000, paint2);
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
        setMeasuredDimension(256,256);//measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        Log.d(Blinkendroid.LOG_TAG,"measureWidth"+specSize);
//        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
//        } else {
//            // Measure the text
//            result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
//                    + getPaddingRight();
//            if (specMode == MeasureSpec.AT_MOST) {
//                // Respect AT_MOST value if that was what is called for by measureSpec
//                result = Math.min(result, specSize);
//            }
//        }

        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
//        Log.d(Blinkendroid.LOG_TAG,"measureHeight"+specSize);
//        mAscent = (int) mTextPaint.ascent();
//        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
//        } else {
//            // Measure the text (beware: ascent is a negative number)
//            result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
//                    + getPaddingBottom();
//            if (specMode == MeasureSpec.AT_MOST) {
//                // Respect AT_MOST value if that was what is called for by measureSpec
//                result = Math.min(result, specSize);
//            }
//        }
        return result;
    }
}
