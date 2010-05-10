package org.cbase.blinkendroid;

import java.io.File;
import java.io.FileNotFoundException;
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
import android.widget.ImageView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.irview);

	calculateBtn = (Button) this.findViewById(R.id.calculateBtn);
	shootBtn = (Button) this.findViewById(R.id.shootBtn);
	imgView = (ImageView) this.findViewById(R.id.ImageView01);

	Bitmap origImage = BitmapFactory.decodeFile("/sdcard/data/blinkendroid/testImg.jpg");

	image = BitmapUtils.scaleBitmap(origImage, 640, 480);
	imgView.setImageBitmap(image);

	shootBtn.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		shootBtnClicked();
	    }
	});

	calculateBtn.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		calculationBtnClicked();
	    }
	});

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
	// TODO: Read color and threshold from EditView
	ColorKeyRasterParser rParser = new ColorKeyRasterParser(pixels, width,
		height, Color.WHITE, 30);
	ArrayList<Pixel> borders = rParser.detectEdges();
	SimpleCluster biggestCluster = SimpleCluster
		.findBiggestCluster(borders);

	long endTime = System.nanoTime() - startTime;

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

	// a little bit of feedback
	Context context = getApplicationContext();
	CharSequence text = "Duration " + (endTime / 1000000000) + "s";
	int duration = Toast.LENGTH_SHORT;

	Toast toast = Toast.makeText(context, text, duration);
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
	for (SimpleCluster sc : clusters) {
	    Rectangle bounds = sc.getBounds();

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
}
