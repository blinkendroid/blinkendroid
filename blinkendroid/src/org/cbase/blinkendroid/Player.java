package org.cbase.blinkendroid;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BMLParser;

import android.app.Activity;
import android.os.Bundle;

public class Player extends Activity {

    public static final String INTENT_EXTRA_IP = "ip";
    public static final String INTENT_EXTRA_PORT = "port";

    private PlayerView playerView;
    private BlinkendroidClient blinkendroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);

	final BLM blm = new BMLParser(this).parseBLM(R.raw.allyourbase);

	playerView = new PlayerView(this, blm);

	setContentView(playerView);
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
}
