package org.cbase.blinkendroid.player;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.ICommandHandler;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.player.bml.BLM.Frame;

import android.util.Log;

public class PlayerThread extends Thread {
	boolean playing=true;
	PlayerView playerView;
	long globalTime;
	boolean restart=false;
	PlayerProtocolHandler protocolHandler;
	BLM	blm;
	int startX=0, startY=0, endX=0, endY=0;
	
	public PlayerThread(PlayerView playerView, BLM blm) {
		this.playerView=playerView;
		this.blm=blm;
		this.protocolHandler=new PlayerProtocolHandler(this);
		this.endX=blm.width-1;
		this.endY=blm.height-1;
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
	
	public void setClipping(int startX, int startY, int endX, int endY){
		this.startX=startX;
		this.startY=startY;
		this.endX=endX;
		this.endY=endY;
	}
	
	@Override
	public void run() {
		super.run();
		globalTime	=	System.currentTimeMillis();	
		while(playing){
			long startTime=System.currentTimeMillis();
			int i=0;
			long nextShow=globalTime+1000;
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
					playerView.setMatrix(clipMaptrix(frame.matrix,blm.width,blm.height),blm.width,blm.height,i);
					playerView.postInvalidate();	
				}else{
					Log.i("PlayerThread", "jump frame "+i+" cause waitTime "+waitTime);
				}
				i++;
				//add timediff to globalTime
				globalTime+=System.currentTimeMillis()-startDiff;
			}
//			Log.i("PlayerThread", "played movie in "+(System.currentTimeMillis()-startTime));
		}
	}
	
	public ICommandHandler getPlayerProtocolHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private int[][] clipMaptrix(int[][] matrix, int width, int height){
		int[][] clippedMatrix=new int[endY-startY+1][endX-startX+1];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if(i>=startY && i<=endY && j>=startX && j<=endX){
					clippedMatrix[i-startY][j-startX]=matrix[i][j];
				}
			}
		}
		return clippedMatrix;
	}
}