package org.blinkendroid.simulator.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidListener;

import junit.framework.TestCase;

public class ClientConnectionTest extends TestCase {
    private class TestBlinkendroidListener implements BlinkendroidListener{
	int x,y;
	float startX, startY, endX,  endY;
	Boolean connectionClosed=null;
	int resId=0;
	long startTime=0;
	long serverTime=0;
	
	public void arrow(long duration, float angle) {
  
	}


	public void clip(float startX, float startY, float endX, float endY) {
	    this.startX=startX;
	    this.startY=startY;
	    this.endX=endX;
	    this.endY=endY;
	}

	public void connectionClosed() {
	    connectionClosed=true;
	}

	public void connectionFailed(String message) {
	}

	public void connectionOpened() {
	    connectionClosed=false;
	}

	public void play(int x, int y, int resId, long startTime) {
	    this.x=x;
	    this.y=y;
	    this.resId=resId;
	    this.startTime=startTime;
	}

	public void serverTime(long serverTime) {
	    this.serverTime=serverTime;
	}
	
    }
    
    private static final String IP = "192.168.1.4";
    
//    public void testConnection() throws Exception{
//	TestBlinkendroidListener testListener =	new TestBlinkendroidListener();
//	BlinkendroidClient client = new BlinkendroidClient(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
//	client.start();
//	Thread.sleep(1000);
//	assertFalse(testListener.connectionClosed);
//	assertTrue(testListener.startTime>0);
//	assertTrue(testListener.serverTime>0);
//	assertTrue(testListener.resId>0);
//	assertEquals(testListener.startX,(float)0.0);
//	assertEquals(testListener.startY,(float)0.0);
//	assertEquals(testListener.endX,(float)1.0);
//	assertEquals(testListener.endY,(float)1.0);
//	
//	long serverTime=testListener.serverTime;
//	Thread.sleep(5001);
//	assertTrue(serverTime<testListener.serverTime);
//	client.shutdown();
//	Thread.sleep(1000);
//	assertTrue(testListener.connectionClosed);
//    }
    
    public void test100Connections() throws Exception{
	int x=0;
	int y=0;
	for(int i=0;i<10;i++){
	    TestBlinkendroidListener testListener =	new TestBlinkendroidListener();
	    BlinkendroidClient client = new BlinkendroidClient(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
	    client.start();
	    Thread.sleep(2000);
	    assertFalse(testListener.connectionClosed);
	    assertTrue(testListener.startTime>0);
	    assertTrue(testListener.serverTime>0);
	    assertTrue(testListener.resId>0);
	}
	Thread.sleep(60000);
    }
}
