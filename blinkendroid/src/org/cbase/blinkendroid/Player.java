package org.cbase.blinkendroid;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.player.PlayerThread;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BMLParser;

import android.app.Activity;
import android.os.Bundle;

public class Player extends Activity {

    public static final String INTENT_EXTRA_IP = "ip";
    public static final String INTENT_EXTRA_PORT = "port";

    private PlayerView playerView;
    private PlayerThread pThread;
    private BlinkendroidClient blinkendroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	playerView = new PlayerView(this);
	setContentView(playerView);
    }

    @Override
    protected void onResume() {

	super.onResume();

	blinkendroidClient = new BlinkendroidClient(getIntent().getStringExtra(
		INTENT_EXTRA_IP), getIntent().getIntExtra(INTENT_EXTRA_PORT,
		Constants.SERVER_PORT));
	blinkendroidClient.connect();

	final BLM blm = new BMLParser(this).parseBLM(R.raw.anapaula);

	PlayerThread pThread = new PlayerThread(playerView, blm);
	pThread.start();

	blinkendroidClient.setPlayerThread(pThread);
    }

    @Override
    protected void onPause() {

	if (pThread != null) {
	    pThread.shutdown();
	    pThread = null;
	}

	if (blinkendroidClient != null) {
	    blinkendroidClient.shutdown();
	    blinkendroidClient = null;
	}

	super.onPause();
    }
}
