package org.cbase.blinkendroid;

import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidServer;
import org.cbase.blinkendroid.player.PlayerThread;
import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BMLParser;
import org.cbase.blinkendroid.utils.NetworkUtils;

import android.app.Activity;
import android.os.Bundle;

public class Player extends Activity {
	PlayerView playerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playerView	=	new PlayerView(this);
		setContentView(playerView);
		
		BlinkendroidServer	blinkendroidServer	=	new BlinkendroidServer();
		blinkendroidServer.start();

		BlinkendroidClient blinkendroidClient 	=	new BlinkendroidClient(/*"10.0.2.2"*/NetworkUtils.getLocalIpAddress());
		blinkendroidClient.connect();
		
		BLM	blm	=	new  BMLParser(this).parseBLM();
		PlayerThread pThread	=	new PlayerThread(playerView,blm);
		pThread.start();
		
		blinkendroidClient.setPlayerThread(pThread);

	}
}
