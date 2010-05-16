package org.cbase.blinkendroid.player;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BLM.Frame;

import android.util.Log;

public class PlayerThread extends Thread implements BlinkendroidProtocolHandler{
	boolean playing=true;
	PlayerView playerView;
	long globalTime;
	boolean restart=false;
	BLM	blm;
	public PlayerThread(PlayerView playerView, BLM blm) {
		this.playerView=playerView;
		this.blm=blm;
	}
	public void setGlobalTime(long globalTime) {
		Log.i(Constants.LOG_TAG,"setGlobalTime("+globalTime+") diff "+(this.globalTime-globalTime));
		this.globalTime=globalTime;
	}	
	public void restart(long globalTime){
		Log.i(Constants.LOG_TAG,"restart("+globalTime+") diff "+(this.globalTime-globalTime));
		restart=true;
		this.globalTime=globalTime;
	}
	@Override
	public void run() {
		super.run();
		globalTime	=	System.currentTimeMillis();	
		while(playing){
			long startTime=System.currentTimeMillis();
			int i=0;
			long nextShow=globalTime;
//			int lastDuration=0;
			boolean jumpframe	=	false;
			long startDiff;
			for (Frame frame : this.blm.frames) {
				//start diff for globalTime Diff
				startDiff=System.currentTimeMillis();
				
				//alle 10 frames von aussen stoeren
//				if(i%10==0)
//					globalTime+=(Math.random()*200)*(Math.random()<0.5?-1:1);
				
				jumpframe	=	false;
				long waitTime = nextShow - globalTime;
				try {
					if(waitTime<20)
						jumpframe=true;
					else
						PlayerThread.sleep(waitTime);
				} catch (InterruptedException e) {;
				}
				nextShow += frame.duration; 
//				lastDuration=frame.duration;
				if(!jumpframe){
					playerView.setMatrix(frame.matrix,blm.width,blm.height,i);
					playerView.postInvalidate();	
				}else{
					Log.i("PlayerThread", "jump frame "+i+" cause waitTime "+waitTime);
				}
				i++;
				//add timediff to globalTime
				globalTime+=System.currentTimeMillis()-startDiff;
			}
			Log.i("PlayerThread", "played movie in "+(System.currentTimeMillis()-startTime));
		}
	}

}