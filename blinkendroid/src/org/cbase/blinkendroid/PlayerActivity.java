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
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BMLParser;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Andreas Schildbach
 */
public class PlayerActivity extends Activity implements BlinkendroidListener {

    public static final String INTENT_EXTRA_IP = "ip";
    public static final String INTENT_EXTRA_PORT = "port";

    private PlayerView playerView;
    private BlinkendroidClient blinkendroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	final BLM blm = new BMLParser()
		.parseBLM(resourceAsReader(R.raw.allyourbase));

	playerView = new PlayerView(this, blm);

	setContentView(playerView);
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

	playerView.startPlaying();
    }

    @Override
    protected void onPause() {

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
	    }
	});
    }

    public void serverTime(final long serverTime) {
	runOnUiThread(new Runnable() {
	    public void run() {
		long timeDelta = System.currentTimeMillis() - serverTime;
		// TODO
	    }
	});
    }

    public void arrow(final boolean visible, final float angle) {
	runOnUiThread(new Runnable() {
	    public void run() {
		// TODO
	    }
	});
    }
}
