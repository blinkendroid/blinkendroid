package org.cbase.blinkendroid;

import org.cbase.blinkendroid.player.PlayerView;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BMLParser;
import org.cbase.blinkendroid.player.bml.BLM.Frame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Player extends Activity {
	PlayerView playerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		playerView	=	new PlayerView(this);
		setContentView(playerView);
		BLM	blm	=	new  BMLParser(this).parseBLM();
		new PlayerThread(playerView,blm).start();
	}

	private class PlayerThread extends Thread{
		boolean playing=true;
		PlayerView playerView;
		BLM	blm;
		public PlayerThread(PlayerView playerView, BLM blm) {
			this.playerView=playerView;
			this.blm=blm;
		}
		@Override
		public void run() {
			super.run();
			while(playing){
				long startTime=System.currentTimeMillis();
				int i=0;
				for (Frame frame : this.blm.frames) {
					try {
//						Log.i("PlayerThread", "sleep for "+frame.duration+" frame "+i);
						this.sleep(frame.duration);
					} catch (InterruptedException e) {;
					}
					playerView.setMatrix(frame.matrix,blm.width,blm.height);
					playerView.postInvalidate();	
					i++;
				}
			}
		}	
	}
}
