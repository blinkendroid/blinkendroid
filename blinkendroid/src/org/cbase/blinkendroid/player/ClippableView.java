package org.cbase.blinkendroid.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ClippableView extends View {

    protected float startX = 0f;
    protected float startY = 0f;
    protected float endX = 1f;
    protected float endY = 1f;

    public ClippableView(Context context) {
	super(context);
    }

    public ClippableView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    public ClippableView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
    }

    public void setClipping(float startX, float startY, float endX, float endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    
        postInvalidate();
    }

}