package org.cbase.blinkendroid.server;

import org.cbase.blinkendroid.network.BlinkendroidProtocol;

public class PlayerManager {

    PlayerClient[][]	clients	=	new PlayerClient[10][10];
    int maxX=0,maxY=0;
    public void addClient(BlinkendroidProtocol blinkendroidProtocol) {
	    PlayerClient pClient= new PlayerClient(blinkendroidProtocol);
	    //server starts thread to send globaltime
	    blinkendroidProtocol.startTimerThread();//TODO evtl nur ein timerthread im server
	    
	    //TODO finde freien Platz in der Matrix
	    boolean found=false;
	    for (int i = 0; i < maxY; i++) {
		    for (int j = 0; j < maxY; j++) {
			//wenn freier platz dann nehmen
			if(clients[i][j]==null){
			    pClient.y=i;
			    pClient.x=j;
			    found=true;
			    break;
			}
			if(found)
			    break;
		    }		
	    }
	    //wenn nicht gefunden dann erweitern, erst dann y
	    if(!found){
        	    //wenn maxX>maxY -> neuen client an maxY+1
        	    if(maxX>maxY){
        		 pClient.y=maxY+1;
			 pClient.x=0;
			 maxY++;
        	    }else{
        		//else -> neuen client an maxX+1
        		 pClient.y=0;
			 pClient.x=maxX+1;
			 maxX++;
        	    }
	    }
	    clients[pClient.y][pClient.x]=pClient;
	    
	    //clipping für alle berechnen
	    float startY=0;
	    for (int i = 0; i < maxY; i++) {
		float startX=0;
		for (int j = 0; j < maxY; j++) {
		    if(clients[i][j]!=null){
			clients[i][j].startX=startX;
			clients[i][j].startY=startY;
			clients[i][j].endX=startX+1/maxX;
			clients[i][j].endY=startY+1/maxY;
		    }
		    startX=startX+1/maxX;
		}
		startY=startY+1/maxY;
	    }
	    //Play
	    pClient.play();
    }

    public void shutdown() {
	 for (int i = 0; i < maxY; i++) {
		    for (int j = 0; j < maxY; j++) {
			  if (null != clients[i][j])
			      clients[i][j].shutdown();
		    }
	 }
    }
}
