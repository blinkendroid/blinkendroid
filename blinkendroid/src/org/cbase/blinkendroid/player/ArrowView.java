package org.cbase.blinkendroid.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class ArrowView extends View {
    private int width;
    private int height;
    private float angle;
    private int color;
    private float scale = 1f;
    private static final Path path = new Path();
    private static final Paint arrowStroke = new Paint();

    static {
	path.moveTo(0f, -5f);
	path.lineTo(-3f, 4f);
	path.lineTo(3f, 4f);
	path.close();

	arrowStroke.setStyle(Style.STROKE);
	arrowStroke.setColor(Color.WHITE);
	arrowStroke.setStrokeWidth(0.1f);
	arrowStroke.setAntiAlias(true);
    }

    public ArrowView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    @Override
    protected void onDraw(final Canvas canvas) {

	final Paint arrowPaint = new Paint();
	arrowPaint.setColor(color);
	arrowPaint.setAntiAlias(true);

	canvas.translate(width / 2f, height / 2f);
	canvas.rotate(angle);
	canvas.scale(width / 10f * scale, height / 10f * scale);
	canvas.drawPath(path, arrowPaint);
	canvas.drawPath(path, arrowStroke);
    }

    public void setAngle(final float angle) {
	this.angle = angle;
	postInvalidate();
    }

    public void setColor(int color) {
	this.color = color;
	postInvalidate();
    }

    public void setScale(final float scale) {
	this.scale = scale;
	postInvalidate();
    }

    @Override
    protected void onMeasure(final int wMeasureSpec, final int hMeasureSpec) {
	final int wMode = MeasureSpec.getMode(wMeasureSpec);
	final int wSize = MeasureSpec.getSize(wMeasureSpec);

	if (wMode == MeasureSpec.EXACTLY)
	    width = wSize;
	else if (wMode == MeasureSpec.AT_MOST)
	    width = Math.min(width, wSize);

	final int hMode = MeasureSpec.getMode(hMeasureSpec);
	final int hSize = MeasureSpec.getSize(hMeasureSpec);

	if (hMode == MeasureSpec.EXACTLY)
	    height = hSize;
	else if (hMode == MeasureSpec.AT_MOST)
	    height = Math.min(height, hSize);

	setMeasuredDimension(this.width, this.height);
    }
}
