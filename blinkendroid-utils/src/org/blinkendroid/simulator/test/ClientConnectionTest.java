package org.blinkendroid.simulator.test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import junit.framework.TestCase;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidClient2;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.player.bml.BLM;

public class ClientConnectionTest extends TestCase {
    private static final String IP = "192.168.1.4";
    private class TestBlinkendroidListener implements BlinkendroidListener{
	int x,y;
	float startX, startY, endX,  endY;
	Boolean connectionClosed=null;
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

	public void connectionClosed(SocketAddress addr) {
	    connectionClosed=true;
	}

	public void connectionFailed(String message) {
	}

	public void connectionOpened(SocketAddress addr) {
	    connectionClosed=false;
	}

	public void play(int x, int y, long startTime, BLM blm) {
	    this.x=x;
	    this.y=y;
	    this.startTime=startTime;
	}

	public void serverTime(long serverTime) {
	    this.serverTime=serverTime;
	}
	
    }
    

    
//    public void testReusePosition() throws Exception{
//	TestBlinkendroidListener testListener =	new TestBlinkendroidListener();
//	BlinkendroidClient2 client = new BlinkendroidClient2(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
//	client.start();
//	Thread.sleep(500);
//	assertFalse(testListener.connectionClosed);
//	assertEquals(0,testListener.x);
//	assertEquals(0,testListener.y);
//
//	TestBlinkendroidListener testListener2 =	new TestBlinkendroidListener();
//	BlinkendroidClient2 client2 = new BlinkendroidClient2(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener2);
//	client2.start();
//	Thread.sleep(500);
//	assertFalse(testListener2.connectionClosed);
//	assertEquals(1,testListener2.x);
//	assertEquals(0,testListener2.y);
//	
//	//erste abschiessen
//	client.shutdown();
//	Thread.sleep(500);
//
//	TestBlinkendroidListener testListener3 =	new TestBlinkendroidListener();
//	BlinkendroidClient2 client3 = new BlinkendroidClient2(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener3);
//	client3.start();
//	Thread.sleep(500);
//	assertFalse(testListener3.connectionClosed);
//	assertEquals(0,testListener3.x);
//	assertEquals(0,testListener3.y);
//    }
//    
    public void testConnection() throws Exception{
	TestBlinkendroidListener testListener =	new TestBlinkendroidListener();
	BlinkendroidClient2 client = new BlinkendroidClient2(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
	client.start();
	Thread.sleep(1000);
	assertFalse(testListener.connectionClosed);
	assertTrue(testListener.startTime>0);
	assertTrue(testListener.serverTime>0);
	assertEquals(testListener.startX,(float)0.0);
	assertEquals(testListener.startY,(float)0.0);
	assertEquals(testListener.endX,(float)1.0);
	assertEquals(testListener.endY,(float)1.0);
	
	long serverTime=testListener.serverTime;
	Thread.sleep(5001);
	assertTrue(serverTime<testListener.serverTime);
	client.shutdown();
	Thread.sleep(1000);
	assertTrue(testListener.connectionClosed);
    }
    
//    public void test100Connections() throws Exception{
//	int x=0;
//	int y=0;
//	for(int i=0;i<100;i++){
//	    TestBlinkendroidListener testListener =	new TestBlinkendroidListener();
//	    BlinkendroidClient2 client = new BlinkendroidClient2(new InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
//	    client.start();
//	    Thread.sleep(500);
//	    //calc expected positions
//	    int maxX = (int)Math.floor(Math.sqrt(i));
//	    assertFalse(testListener.connectionClosed);
//	    assertTrue(testListener.startTime>0);
//	    assertTrue(testListener.serverTime>0);
//	    assertTrue(testListener.resId>0);
//	}
//	Thread.sleep(60000);
//    }

}
