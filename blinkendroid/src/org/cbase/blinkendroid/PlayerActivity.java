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

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.cbase.blinkendroid.network.BlinkendroidClient2;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.player.ArrowView;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BBMZParser;
import org.cbase.blinkendroid.player.bml.BLM;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.TextView;
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
    private TextView ownerView;
    private BlinkendroidClient2 blinkendroidClient;
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
	ownerView = (TextView) findViewById(R.id.player_owner);

	playerView.setOnTouchListener(new OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
		ownerView.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {
		    public void run() {
			ownerView.setVisibility(View.INVISIBLE);
			// blinkendroidClient.touch();
		    }
		}, Constants.SHOW_OWNER_DURATION);
		return false;
	    }
	});

	String owner = getOwnerOrPhonenumber();
	ownerView.setText(owner);
    }

    /**
     * Gets the owner's name from the preferences or falls back to the
     * phone's primary number
     * @return owner name or phone number
     */
    private String getOwnerOrPhonenumber() {
	String ownerName = PreferenceManager.getDefaultSharedPreferences(this)
		.getString("owner", null);

	if (ownerName == null || ownerName.length() == 0) {
	    TelephonyManager tm = (TelephonyManager) this
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    ownerName = tm.getLine1Number();
	}

	return ownerName;
    }
    
    @Override
    protected void onResume() {

	super.onResume();

	blinkendroidClient = new BlinkendroidClient2(
		new InetSocketAddress(getIntent().getStringExtra(
			INTENT_EXTRA_IP), getIntent().getIntExtra(
			INTENT_EXTRA_PORT, Constants.SERVER_PORT)), this);
	blinkendroidClient.start();

	if (blinkendroidClient != null) {

	    if (playing)
		playerView.startPlaying();

	    if (System.currentTimeMillis() < arrowDuration) {
		handler.post(this);
		arrowView.setVisibility(View.VISIBLE);
	    } else {
		arrowView.setVisibility(View.INVISIBLE);
	    }
	}
    }

    @Override
    protected void onPause() {

	handler.removeCallbacks(this);

	playerView.stopPlaying();

	if (blinkendroidClient != null) {
	    blinkendroidClient.shutdown();
	    blinkendroidClient = null;
	}

	super.onPause();
    }

    public void serverTime(final long serverTime) {
	Log.d(Constants.LOG_TAG, "*** time " + serverTime);
	final long timeDelta = System.currentTimeMillis() - serverTime;
	runOnUiThread(new Runnable() {
	    public void run() {
		playerView.setTimeDelta(timeDelta);
	    }
	});
    }

    public void play(final int x, final int y, final long startTime,
	    final BLM movie) {
	Log.d(Constants.LOG_TAG, "*** play " + startTime);
	runOnUiThread(new Runnable() {
	    public void run() {
		blm = movie;
		if (blm == null)
		    blm = new BBMZParser().parseBBMZ(getResources()
			    .openRawResource(R.raw.blinkendroid1));
		playerView.setBLM(blm);
		playerView.setStartTime(startTime);
		playerView.startPlaying();
		playing = true;
	    }
	});
    }

    public void clip(final float startX, final float startY, final float endX,
	    final float endY) {
	Log.d(Constants.LOG_TAG, "*** clip " + startX + "," + startY + ","
		+ endX + "," + endY);
	runOnUiThread(new Runnable() {
	    public void run() {
		final int absStartX = (int) (blm.header.width * startX);
		final int absStartY = (int) (blm.header.height * startY);
		final int absEndX = (int) (blm.header.width * endX);
		final int absEndY = (int) (blm.header.height * endY);
		playerView.setClipping(absStartX, absStartY, absEndX, absEndY);
	    }
	});
    }

    public void arrow(final long duration, final float angle) {
	Log.d(Constants.LOG_TAG, "*** arrow " + angle + " " + duration);
	runOnUiThread(new Runnable() {
	    public void run() {
		arrowView.setAngle(angle);
		arrowView.setVisibility(View.VISIBLE);
		arrowDuration = System.currentTimeMillis() + duration;
		handler.post(PlayerActivity.this);
	    }
	});
    }

    public void connectionOpened(final SocketAddress socketAddress) {
	Log.d(Constants.LOG_TAG, "*** connectionOpened "
		+ socketAddress.toString());
	runOnUiThread(new Runnable() {
	    public void run() {
		Toast.makeText(PlayerActivity.this, "connected",
			Toast.LENGTH_SHORT).show();
	    }
	});
    }

    public void connectionClosed(final SocketAddress socketAddress) {
	Log.d(Constants.LOG_TAG, "*** connectionClosed "
		+ socketAddress.toString());
	runOnUiThread(new Runnable() {
	    public void run() {
		Toast.makeText(PlayerActivity.this,
			"connection to server closed", Toast.LENGTH_LONG)
			.show();
		handler.removeCallbacks(this);
		playerView.stopPlaying();
		ownerView.setVisibility(View.VISIBLE);
	    }
	});
    }

    public void connectionFailed(final String message) {
	runOnUiThread(new Runnable() {
	    public void run() {
		Log.w(Constants.LOG_TAG, "connection failed: " + message);
		handler.removeCallbacks(this);
		playerView.stopPlaying();
		ownerView.setVisibility(View.VISIBLE);
		new AlertDialog.Builder(PlayerActivity.this).setIcon(
			android.R.drawable.ic_dialog_alert).setTitle(
			"Cannot connect to server").setMessage(message)
			.setOnCancelListener(new OnCancelListener() {
			    public void onCancel(DialogInterface dialog) {
				finish();
			    }
			}).create().show();
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
