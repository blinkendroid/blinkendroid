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
import java.io.InputStreamReader;
import java.io.Reader;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidProtocol;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.network.BlinkendroidProtocolHandler;
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

    private float arrowAngle = 0f, arrowScale = 0f;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.player_content);

	final BLM blm = new BBMZParser().parseBBMZ(getResources().openRawResource(R.raw.allyourbase));
	
	playerView = (PlayerView) findViewById(R.id.player_image);
	playerView.setBLM(blm);

	arrowView = (ArrowView) findViewById(R.id.player_arrow);
	arrowView.setVisibility(View.VISIBLE);
    }

    private Reader resourceAsReader(final int res) {
	try {
	    return new InputStreamReader(getResources().openRawResource(res),
		    "utf-8");
	} catch (final IOException x) {
	    throw new RuntimeException(x);
	}
    }

    @Override
    protected void onResume() {

	super.onResume();

	blinkendroidClient = new BlinkendroidClient(getIntent().getStringExtra(
		INTENT_EXTRA_IP), getIntent().getIntExtra(INTENT_EXTRA_PORT,
		Constants.SERVER_PORT));
	blinkendroidClient.connect();
	blinkendroidClient.getProtocol().registerHandler(BlinkendroidProtocol.PROTOCOL_PLAYER, new BlinkendroidProtocolHandler(this));

	playerView.startPlaying();

	handler.post(this);
    }

    @Override
    protected void onPause() {

	handler.removeCallbacks(this);
	//TODO schtief remove handler from protocol
	playerView.stopPlaying();

	if (blinkendroidClient != null) {
	    blinkendroidClient.shutdown();
	    blinkendroidClient = null;
	}

	super.onPause();
    }

    public void clip(final int startX, final int startY, final int endX,
	    final int endY) {
	runOnUiThread(new Runnable() {
	    public void run() {
		playerView.setClipping(startX, startY, endX, endY);
		Log.i(Constants.LOG_TAG,"setClipping "+startX+","+ startY+","+ endX+","+ endY);
	    }
	});
    }

    public void serverTime(final long serverTime) {
	runOnUiThread(new Runnable() {
	    public void run() {
		long timeDelta = System.currentTimeMillis() - serverTime;
		// TODO
		Log.i(Constants.LOG_TAG,"timeDelta "+timeDelta);
	    }
	});
    }

    public void arrow(final boolean visible, final float angle) {
	runOnUiThread(new Runnable() {
	    public void run() {
		if (visible) {
		    arrowView.setAngle(angle);
		    arrowView.setVisibility(View.VISIBLE);
		} else {
		    arrowView.setVisibility(View.INVISIBLE);
		}
	    }
	});
    }

    public void run() {
	arrowAngle += 2f;
	if (arrowAngle >= 360f)
	    arrowAngle -= 360f;
	arrowView.setAngle(arrowAngle);

	arrowScale += 0.5f;
	if (arrowScale >= 2 * Math.PI)
	    arrowScale -= 2 * Math.PI;
	final float scale = 0.5f + (float) Math.sin(arrowScale) / 20;
	arrowView.setScale(scale);

	handler.postDelayed(this, 20);
    }
}
