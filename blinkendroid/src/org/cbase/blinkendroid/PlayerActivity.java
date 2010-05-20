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

package org.cbase.blinkendroid;

import java.io.IOException;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.player.ArrowView;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BBMZParser;
import org.cbase.blinkendroid.player.bml.BLM;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

/**
 * @author Andreas Schildbach
 */
public class PlayerActivity extends Activity implements BlinkendroidListener,
	Runnable {

    public static final String INTENT_EXTRA_IP = "ip";
    public static final String INTENT_EXTRA_PORT = "port";

    private PlayerView playerView;
    private ArrowView arrowView;
    private BlinkendroidClient blinkendroidClient;
    private BLM blm;
    private boolean playing = false;
    private long arrowDuration;

    private float arrowScale = 0f;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.player_content);

	playerView = (PlayerView) findViewById(R.id.player_image);
	arrowView = (ArrowView) findViewById(R.id.player_arrow);
    }

    @Override
    protected void onResume() {

	super.onResume();

	try {
	    blinkendroidClient = new BlinkendroidClient(getIntent()
		    .getStringExtra(INTENT_EXTRA_IP), getIntent().getIntExtra(
		    INTENT_EXTRA_PORT, Constants.SERVER_PORT));
	} catch (final IOException x) {
	    throw new RuntimeException(x);
	}
	blinkendroidClient.registerListener(this);

	if (playing)
	    playerView.startPlaying();

	if (System.currentTimeMillis() < arrowDuration) {
	    handler.post(this);
	    arrowView.setVisibility(View.VISIBLE);
	} else {
	    arrowView.setVisibility(View.INVISIBLE);
	}
    }

    @Override
    protected void onPause() {

	handler.removeCallbacks(this);

	playerView.stopPlaying();

	if (blinkendroidClient != null) {
	    try {
		blinkendroidClient.shutdown();
	    } catch (final IOException x) {
		throw new RuntimeException(x);
	    }
	    blinkendroidClient = null;
	}

	super.onPause();
    }

    public void serverTime(final long serverTime) {
	Log.i(Constants.LOG_TAG, "time " + serverTime);
	final long timeDelta = System.currentTimeMillis() - serverTime;
	runOnUiThread(new Runnable() {
	    public void run() {
		Log.i(Constants.LOG_TAG, "ui time start " + serverTime + " "
			+ timeDelta);
		playerView.setTimeDelta(timeDelta);
		Log.i(Constants.LOG_TAG, "ui time end " + serverTime + " "
			+ timeDelta);
	    }
	});
    }

    public void play(final int resId, final long startTime) {
	Log.i(Constants.LOG_TAG, "play " + startTime);
	runOnUiThread(new Runnable() {
	    public void run() {
		Log.i(Constants.LOG_TAG, "ui play start " + startTime);
		blm = new BBMZParser().parseBBMZ(getResources()
			.openRawResource(resId));
		playerView.setBLM(blm);
		playerView.setStartTime(startTime);
		playerView.startPlaying();
		playing = true;
		Log.i(Constants.LOG_TAG, "ui play end " + startTime);
	    }
	});
    }

    public void clip(final float startX, final float startY, final float endX,
	    final float endY) {
	Log.i(Constants.LOG_TAG, "clip " + startX + "," + startY + "," + endX
		+ "," + endY);
	runOnUiThread(new Runnable() {
	    public void run() {
		Log.i(Constants.LOG_TAG, "ui clip start " + startX + ","
			+ startY + "," + endX + "," + endY);
		final int absStartX = (int) (blm.width * startX);
		final int absStartY = (int) (blm.height * startY);
		final int absEndX = (int) (blm.width * endX);
		final int absEndY = (int) (blm.height * endY);
		playerView.setClipping(absStartX, absStartY, absEndX, absEndY);
		Log.i(Constants.LOG_TAG, "ui clip end " + startX + "," + startY
			+ "," + endX + "," + endY);
	    }
	});
    }

    public void arrow(final long duration, final float angle) {
	Log.i(Constants.LOG_TAG, "arrow " + angle + " " + duration);
	runOnUiThread(new Runnable() {
	    public void run() {
		arrowView.setAngle(angle);
		arrowView.setVisibility(View.VISIBLE);
		arrowDuration = System.currentTimeMillis() + duration;
		handler.post(PlayerActivity.this);
	    }
	});
    }

    public void connectionLost() {
	Log.i(Constants.LOG_TAG, "connection lost");
	runOnUiThread(new Runnable() {
	    public void run() {
		Toast.makeText(PlayerActivity.this,
			"connection to server lost", Toast.LENGTH_LONG).show();
		handler.removeCallbacks(this);
		playerView.stopPlaying();
	    }
	});
    }

    public void run() {

	arrowScale += 0.5f;
	if (arrowScale >= 2 * Math.PI)
	    arrowScale -= 2 * Math.PI;
	final float scale = 0.5f + (float) Math.sin(arrowScale) / 20;
	arrowView.setScale(scale);

	if (System.currentTimeMillis() < arrowDuration)
	    handler.postDelayed(this, 20);
	else
	    arrowView.setVisibility(View.INVISIBLE);
    }
}
