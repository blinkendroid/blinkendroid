package org.cbase.blinkendroid;

import java.util.ArrayList;
import java.util.List;

import org.cbase.blinkendroid.geom.Pixel;
import org.cbase.blinkendroid.geom.Rectangle;
import org.cbase.blinkendroid.geom.SimpleCluster;
import org.cbase.blinkendroid.graphics.ColorKeyRasterParser;
import org.cbase.blinkendroid.utils.BitmapUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Activity for the image recognition part - more of a testbed now ;)
 * 
 * @author dima
 */
public class IRActivity extends Activity {

    private Button calculateBtn;
    private Button shootBtn;
    private ImageView imgView;

    private Bitmap image;
    private TextView editText;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.irview);

	// please clean up ;)
	System.gc();

	// Initializing our view components
	calculateBtn = (Button) this.findViewById(R.id.calculateBtn);
	shootBtn = (Button) this.findViewById(R.id.shootBtn);
	imgView = (ImageView) this.findViewById(R.id.ImageView01);

	editText = (EditText) this.findViewById(R.id.colorKeyEditText);
	editText.setText("#ff0000");

	seekBar = (SeekBar) this.findViewById(R.id.colorKeySeekBar);
	seekBar.setMax(200);

	seekBar.setOnSeekBarChangeListener(new ThresholdSeekBarListener());
	shootBtn.setOnClickListener(new ShootButtonListener());
	calculateBtn.setOnClickListener(new ClaculateButtonListener());

	loadImage();
    }

    private void loadImage() {
	if (image != null) {
	    image.recycle();

	}

	BitmapFactory.Options opts = new BitmapFactory.Options();
	opts.inSampleSize = 10;
	Bitmap origImage = BitmapFactory.decodeFile(
		"/sdcard/data/blinkendroid/testImg.jpg", opts);
	image = BitmapUtils.scaleBitmap(origImage, 640, 480);
	imgView.setImageBitmap(image);
    }

    @Override
    protected void onRestart() {
	loadImage();
	super.onRestart();
    }

    @Override
    protected void onResume() {
	loadImage();
	super.onResume();
    }

    private void shootBtnClicked() {
	Log.d(Constants.LOG_TAG, getLocalClassName() + " shootBtnClicked");
    }

    private void calculationBtnClicked() {

	long startTime = System.nanoTime();

	int width = image.getWidth();
	int height = image.getHeight();
	int[] pixels = new int[width * height];

	image.getPixels(pixels, 0, width, 0, 0, width, height);

	// Doing our parsing stuff
	int threshold = seekBar.getProgress();
	int keyColor = Color.parseColor(editText.getText().toString());

	ColorKeyRasterParser rParser = new ColorKeyRasterParser(pixels, width,
		height, keyColor, threshold);
	ArrayList<Pixel> borders = rParser.detectEdges();

	SimpleCluster biggestCluster;
	biggestCluster = SimpleCluster.findBiggestCluster(borders);

	long endTime = System.nanoTime() - startTime;

	if (biggestCluster == null) {
	    return;
	}

	Bitmap resultImg = image.copy(image.getConfig(), true);

	Canvas c = new Canvas(resultImg);
	Paint p = new Paint();
	p.setColor(Color.argb(200, 0, 0, 249));

	// drawing the whole thing
	ArrayList<SimpleCluster> clusters = new ArrayList<SimpleCluster>(1);
	clusters.add(biggestCluster);
	drawPixels(borders, c, p);
	drawClusters(clusters, c, p);

	imgView.setImageBitmap(resultImg);

	showToastNotification("Duration " + (endTime / 1000000) + "ms");

    }

    private void showToastNotification(String string) {
	Context context = getApplicationContext();
	int duration = Toast.LENGTH_SHORT;

	Toast toast = Toast.makeText(context, string, duration);
	toast.show();
    }

    /**
     * Draws a list of pixels
     * 
     * @param pixels
     *            points to draw
     * @param canvas
     * @param paint
     */
    private static void drawPixels(List<Pixel> pixels, Canvas canvas,
	    Paint paint) {
	for (Pixel pix : pixels) {
	    if (pix != null) {
		canvas.drawPoint(pix.getX(), pix.getY(), paint);
	    }
	}
    }

    /**
     * Draws a list of clusters on a canvas using two vertical lines
     * 
     * @param clusters
     *            clusters to draw
     * @param canvas
     * @param paint
     */
    private static void drawClusters(List<SimpleCluster> clusters,
	    Canvas canvas, Paint paint) {
	Rectangle bounds;
	for (SimpleCluster sc : clusters) {
	    bounds = sc.getBounds();

	    paint.setColor(Color.argb(230, 0, 252, 249));

	    canvas.drawLine(bounds.getX(), bounds.getY(), bounds.getX()
		    + bounds.getWidth(), bounds.getY() + bounds.getHeight(),
		    paint);

	    canvas.drawLine(bounds.getX(), bounds.getY() + bounds.getHeight(),
		    bounds.getX() + bounds.getWidth(), bounds.getY(), paint);

	    paint.setColor(Color.YELLOW);
	    canvas.drawText("Cluster", bounds.getX() + (bounds.getWidth() / 2),
		    bounds.getY() + (bounds.getHeight() / 2), paint);
	}

    }

    /**
     * Handler for SeekBar events
     * 
     * @author dima
     */
    private class ThresholdSeekBarListener implements OnSeekBarChangeListener {

	public void onProgressChanged(SeekBar seekBar, int progress,
		boolean fromUser) {
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	/**
	 * Displays seekbars progress value
	 */
	public void onStopTrackingTouch(SeekBar seekBar) {
	    showToastNotification(Integer.toString(seekBar.getProgress()));
	}
    }

    private class ShootButtonListener implements OnClickListener {

	public void onClick(View v) {
	    shootBtnClicked();
	}
    }

    private class ClaculateButtonListener implements OnClickListener {

	public void onClick(View v) {
	    calculationBtnClicked();
	}
    }
}
