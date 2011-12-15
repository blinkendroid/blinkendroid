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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.cbase.blinkendroid.player.ArrowView;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.PuzzleImageView;
import org.cbase.blinkendroid.player.bml.BBMZParser;
import org.cbase.blinkendroid.player.bml.BLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Andreas Schildbach
 */
public class PlayerActivity extends Activity implements BlinkendroidListener, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PlayerActivity.class);

    public static final String INTENT_EXTRA_IP = "ip";
    public static final String INTENT_EXTRA_PORT = "port";
    private View view;
    private PlayerView playerView;
    private PuzzleImageView imageView;
    private ArrowView arrowView;
    private TextView ownerView;
    private ImageView moleView;
    private BlinkendroidClient blinkendroidClient;
    private BLM blm;
    private Map<Integer, Long> arrowDurations = new HashMap<Integer, Long>();
    private float arrowScale = 0f;
    private final Handler handler = new Handler();
    private BlinkendroidApp app;
    private String owner;

    private boolean mole = false;
    private int moleCounter;
    private int molePoints;
    private int hitMole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	app = (BlinkendroidApp) getApplication();

	app.wantWakeLock(true);

	requestWindowFeature(Window.FEATURE_NO_TITLE);

	setContentView(R.layout.player_content);
	playerView = (PlayerView) findViewById(R.id.player_image);
	playerView.setVisibility(View.GONE);
	imageView = (PuzzleImageView) findViewById(R.id.player_puzzle_image);
	imageView.setVisibility(View.VISIBLE);

	moleView = (ImageView) findViewById(R.id.player_mole);

	arrowView = (ArrowView) findViewById(R.id.player_arrow);
	ownerView = (TextView) findViewById(R.id.player_owner);

	owner = getPhoneIdentifier();

	arrowView.setOnTouchListener(new OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_UP)
		    return true;
		logger.info("onTocuh arrowView");

		ownerView.setText(owner);
		ownerView.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {

		    public void run() {
			ownerView.setVisibility(View.GONE);
			ownerView.setText(owner);
		    }
		}, BlinkendroidApp.SHOW_OWNER_DURATION);
		blinkendroidClient.touch();
		return true;
	    }
	});

	moleView.setOnTouchListener(new OnTouchListener() {

	    public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() != MotionEvent.ACTION_DOWN)
		    return true;
		logger.info("onTocuh moleView");
		if (mole) {
		    // hide mole
		    hitMole = moleCounter;
		    moleView.setVisibility(View.INVISIBLE);
		    mole = false;
		    // show points
		    molePoints++;
		    ownerView.setText(Integer.toString(molePoints));
		    ownerView.invalidate();
		    ownerView.setVisibility(View.VISIBLE);
		    blinkendroidClient.hitMole();
		    handler.postDelayed(new Runnable() {
			public void run() {
			    ownerView.setVisibility(View.GONE);
			}
		    }, BlinkendroidApp.SHOW_OWNER_DURATION);
		} else {
		}
		return true;
	    }
	});
	// forcing the screen brightness to max out while playing
	if (isAllowedToChangeBrightness()) {
	    logger.info("Maxing out the brightness");
	    WindowManager.LayoutParams lp = getWindow().getAttributes();
	    lp.screenBrightness = 1.0f;
	    getWindow().setAttributes(lp);
	}
    }

    /**
     * Gets the owner's name from the preferences or falls back to the phone's
     * primary number
     * 
     * @return owner name or phone number
     */
    private String getPhoneIdentifier() {
	String ownerName = PreferenceManager.getDefaultSharedPreferences(this).getString("owner", null);

	if (ownerName == null || ownerName.trim().length() == 0) {
	    TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	    ownerName = tm.getLine1Number();
	}

	return ownerName;
    }

    private boolean isAllowedToChangeBrightness() {
	return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("brightness", true);
    }

    @Override
    protected void onResume() {

	super.onResume();
	String addr = getIntent().getStringExtra(INTENT_EXTRA_IP);
	if (addr == null) {
	    addr = "127.0.0.1";
	}
	int port = getIntent().getIntExtra(INTENT_EXTRA_PORT, BlinkendroidApp.BROADCAST_SERVER_PORT);
	InetSocketAddress socketAddr = new InetSocketAddress(addr, port);
	blinkendroidClient = new BlinkendroidClient(socketAddr, this);
	/*
	 * blinkendroidClient = new BlinkendroidClient( new
	 * InetSocketAddress(getIntent().getStringExtra( INTENT_EXTRA_IP),
	 * getIntent().getIntExtra( INTENT_EXTRA_PORT,
	 * Constants.BROADCAST_SERVER_PORT)), this);
	 */
	blinkendroidClient.start();

	/*
	 * if (playing) playerView.startPlaying();
	 * 
	 * if (!arrowDurations.isEmpty()) handler.post(this);
	 */
    }

    @Override
    protected void onPause() {

	handler.removeCallbacks(this);

	if (view instanceof PlayerView) {
	    ((PlayerView) view).stopPlaying();
	}

	app.wantWakeLock(false);

	if (blinkendroidClient != null) {
	    blinkendroidClient.shutdown();
	    blinkendroidClient = null;
	}

	super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.player_options, menu);
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.player_options_locate_me:
	    blinkendroidClient.locateMe();
	    break;

	default:
	    break;
	}
	return super.onOptionsItemSelected(item);
    }

    public void serverTime(final long serverTime) {
	// logger.debug("*** time " + serverTime);
	final long timeDelta = System.nanoTime() / 1000000 - serverTime;
	runOnUiThread(new Runnable() {

	    public void run() {
		if (view instanceof PlayerView) {
		    ((PlayerView) view).setTimeDelta(timeDelta);
		}
	    }
	});
    }

    public void playBLM(final long startTime, final BLM movie) {
	logger.info("*** play " + startTime);
	runOnUiThread(new Runnable() {

	    public void run() {
		try {

		    blm = movie;
		    if (blm == null) {
			blm = new BBMZParser().parseBBMZ(getResources().openRawResource(R.raw.blinkendroid1), 14345);
		    }

		    playerView.setBLM(blm);
		    playerView.setStartTime(startTime);
		    playerView.startPlaying();
		    imageView.setVisibility(View.GONE);
		    playerView.setVisibility(View.VISIBLE);
		    view = playerView;
		    view.invalidate();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    public void clip(final float startX, final float startY, final float endX, final float endY) {
	logger.info("*** clip " + startX + "," + startY + "," + endX + "," + endY);
	runOnUiThread(new Runnable() {

	    public void run() {
		if (null != playerView)
		    playerView.setClipping(startX, startY, endX, endY);
		if (null != imageView)
		    imageView.setClipping(startX, startY, endX, endY);
	    }
	});
    }

    public void arrow(final long duration, final float angle, final int color) {
	logger.info("*** arrow " + angle + " " + duration);
	runOnUiThread(new Runnable() {

	    public void run() {
		final int id = arrowView.addArrow(angle, color);
		boolean startPost = arrowDurations.isEmpty();
		arrowDurations.put(id, System.currentTimeMillis() + duration);
		if (startPost)
		    handler.post(PlayerActivity.this);
	    }
	});
    }

    public void connectionOpened(final ClientSocket clientSocket) {
	logger.info("*** connectionOpened " + clientSocket.getDestinationAddress().toString());
	runOnUiThread(new Runnable() {

	    public void run() {
		Toast.makeText(PlayerActivity.this, "connected", Toast.LENGTH_SHORT).show();
	    }
	});
    }

    public void connectionClosed(final ClientSocket clientSocket) {
	logger.info("*** connectionClosed " + clientSocket.getDestinationAddress().toString());
	runOnUiThread(new Runnable() {

	    public void run() {
		Toast.makeText(PlayerActivity.this, getString(R.string.connection_closed), Toast.LENGTH_LONG).show();
		if (view instanceof PlayerView) {
		    ((PlayerView) view).stopPlaying();
		}
		ownerView.setText(owner);
		ownerView.setVisibility(View.VISIBLE);
	    }
	});
    }

    public void connectionFailed(final String message) {
	runOnUiThread(new Runnable() {

	    public void run() {
		logger.warn(getString(R.string.connection_failed) + ": " + message);
		if (view instanceof PlayerView) {
		    ((PlayerView) view).stopPlaying();
		}
		ownerView.setVisibility(View.VISIBLE);
		new AlertDialog.Builder(PlayerActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(
			"Cannot connect to server").setMessage(message).setOnCancelListener(new OnCancelListener() {

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

	for (final Iterator<Map.Entry<Integer, Long>> i = arrowDurations.entrySet().iterator(); i.hasNext();) {
	    final Map.Entry<Integer, Long> entry = i.next();

	    if (System.currentTimeMillis() > entry.getValue()) {
		arrowView.removeArrow(entry.getKey());
		i.remove();
	    }
	}

	if (!arrowDurations.isEmpty())
	    handler.postDelayed(this, 20);
    }

    public void showImage(final Bitmap bmp) {

	runOnUiThread(new Runnable() {

	    public void run() {
		Bitmap image = bmp;
		if (image == null) {
		    try {
			image = BitmapFactory.decodeStream(getResources().openRawResource(
				R.drawable.blinkendroid_gingerbread));
		    } catch (OutOfMemoryError oom) {
			logger.error("fuck you!!", oom);
			image = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.icon));
		    }

		}
		imageView.setImage(image);
		imageView.setVisibility(View.VISIBLE);
		playerView.setVisibility(View.GONE);
		view = imageView;
		view.invalidate();
	    }
	});
    }

    public void mole(final int type, final int moleC, final int duration, final int points) {
	runOnUiThread(new Runnable() {
	    public void run() {
		try {
		    if (mole || ownerView.getVisibility() == View.VISIBLE)
			return;

		    moleView.setImageResource(R.drawable.android1 + (type % 5));
		    moleView.setVisibility(View.VISIBLE);
		    mole = true;
		    moleCounter = moleC;
		    molePoints = points;
		    handler.postDelayed(new Runnable() {
			public void run() {
			    moleView.setVisibility(View.GONE);
			    mole = false;

			    // if we did not hit the mole show negative
			    if (moleCounter != hitMole) {
				// show points
				molePoints--;
				ownerView.setText(Integer.toString(molePoints));
				ownerView.invalidate();
				ownerView.setVisibility(View.VISIBLE);
				blinkendroidClient.missedMole();
				handler.postDelayed(new Runnable() {

				    public void run() {
					ownerView.setVisibility(View.GONE);
					ownerView.setText(owner);
				    }
				}, BlinkendroidApp.SHOW_OWNER_DURATION);
			    }
			}
		    }, duration);
		} catch (Exception e) {
		    logger.error("mole failed", e);
		}
	    };

	});

    }

    public void blink(final int type) {
	runOnUiThread(new Runnable() {
	    public void run() {
		try {
		    if (null != playerView)
			playerView.blink(type);
		    if (null != imageView)
			imageView.blink(type);
		} catch (Exception e) {
		    logger.error("mole failed", e);
		}
	    }
	});
    }
}
