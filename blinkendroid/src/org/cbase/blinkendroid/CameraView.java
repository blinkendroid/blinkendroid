package org.cbase.blinkendroid;

import java.io.IOException;

import org.cbase.blinkendroid.utils.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

/**
 * An activity that is able to preview an image from the device's camera and take a photo.
 * @author ben
 *
 */
public class CameraView extends Activity implements SurfaceHolder.Callback,
	OnClickListener {
    
    static final int FOTO_MODE = 0;
    Camera camera;
    boolean mPreviewRunning = false;
    private Context mContext = this;

    public void onCreate(Bundle icicle) {
	super.onCreate(icicle);

	Log.e(Constants.LOG_TAG, getLocalClassName() + "onCreate");

	Bundle extras = getIntent().getExtras();

	getWindow().setFormat(PixelFormat.TRANSLUCENT);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
	setContentView(R.layout.camera);
	mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
	mSurfaceView.setOnClickListener(this);
	mSurfaceHolder = mSurfaceView.getHolder();
	mSurfaceHolder.addCallback(this);
	mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	super.onRestoreInstanceState(savedInstanceState);
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
	public void onPictureTaken(byte[] imageData, Camera c) {

	    if (imageData != null) {

		Intent mIntent = new Intent();

		FileUtils.storeByteImage(mContext, imageData, 50, "testImg");
		camera.startPreview();

		setResult(FOTO_MODE, mIntent);

	    }
	}
    };

    protected void onResume() {
	Log.d(Constants.LOG_TAG, getLocalClassName() + "onResume");
	super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
    }

    protected void onStop() {
	Log.d(Constants.LOG_TAG, getLocalClassName() + "onStop");
	super.onStop();
    }

    public void surfaceCreated(SurfaceHolder holder) {
	Log.d(Constants.LOG_TAG, getLocalClassName() + "surfaceCreated");
	camera = Camera.open();

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	Log.d(Constants.LOG_TAG, getLocalClassName() + "surfaceChanged");

	// XXX stopPreview() will crash if preview is not running
	if (mPreviewRunning) {
	    camera.stopPreview();
	}

	Camera.Parameters p = camera.getParameters();
//	p.setPreviewSize(w, h);
	camera.setParameters(p);
	try {
	    camera.setPreviewDisplay(holder);
	} catch (IOException e) {
	    Log.e(Constants.LOG_TAG, getLocalClassName() + e);
	    e.printStackTrace();
	}
	camera.startPreview();
	mPreviewRunning = true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
	Log.d(Constants.LOG_TAG, getLocalClassName() + "surfaceDestroyed");
	camera.stopPreview();
	mPreviewRunning = false;
	camera.release();
    }

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    public void onClick(View arg0) {

	camera.takePicture(null, mPictureCallback, mPictureCallback);

    }

}