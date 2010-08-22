package org.blinkendroid.simulator.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import junit.framework.TestCase;

import org.cbase.blinkendroid.Constants;
import org.cbase.blinkendroid.network.BlinkendroidClient;
import org.cbase.blinkendroid.network.BlinkendroidListener;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.player.bml.BLM;
import org.cbase.blinkendroid.server.BlinkendroidServer;

public class ClientConnectionTest extends TestCase {
    private static final String IP = "127.0.0.1";

    private class TestConnectionListener implements ConnectionListener {

	boolean open = false;
	InetAddress inetAddress = null;

	@Override
	public void connectionClosed(InetAddress ia) {
	    System.out.println("TestConnectionListener connectionClosed " + ia);
	    open = false;
	}

	@Override
	public void connectionOpened(InetAddress ia) {
	    System.out.println("TestConnectionListener connectionOpened " + ia);
	    open = true;
	    inetAddress = ia;
	}

    }

    private class TestBlinkendroidListener implements BlinkendroidListener {
	int x, y;
	float startX = -1, startY = -1, endX = -1, endY = -1;
	Boolean connectionClosed = null;
	Boolean shutdown = null;
	long startTime = 0;
	long serverTime = 0;

	public void shutdown() {
	    shutdown = true;
	}

	public void arrow(long duration, float angle, int color) {
	    System.out.println(x + ":" + y + " arrow " + angle);
	}

	public void clip(float startX, float startY, float endX, float endY) {
	    System.out.println(x + ":" + y + " clip " + startX + "," + startY
		    + "," + endX + "," + endY);
	    this.startX = startX;
	    this.startY = startY;
	    this.endX = endX;
	    this.endY = endY;
	}

	public void connectionClosed(InetAddress addr) {
	    System.out.println(x + ":" + y + "connectionClosed");
	    connectionClosed = true;
	}

	public void connectionFailed(String message) {
	    System.out.println(x + ":" + y + " connectionFailed");
	}

	public void connectionOpened(InetAddress addr) {
	    System.out.println(x + ":" + y + " connectionOpened");
	    connectionClosed = false;
	}

	public void play(int x, int y, long startTime, BLM blm) {
	    this.x = x;
	    this.y = y;
	    this.startTime = startTime;
	    System.out.println(x + ":" + y + " play ");
	}

	public void serverTime(long serverTime) {
	    // System.out.println(x+":"+y+" serverTime "+serverTime);
	    this.serverTime = serverTime;
	}

    }

    // public void testReusePosition() throws Exception{
    // TestBlinkendroidListener testListener = new TestBlinkendroidListener();
    // BlinkendroidClient2 client = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
    // client.start();
    // Thread.sleep(500);
    // assertFalse(testListener.connectionClosed);
    // assertEquals(0,testListener.x);
    // assertEquals(0,testListener.y);
    //
    // TestBlinkendroidListener testListener2 = new TestBlinkendroidListener();
    // BlinkendroidClient2 client2 = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener2);
    // client2.start();
    // Thread.sleep(500);
    // assertFalse(testListener2.connectionClosed);
    // assertEquals(1,testListener2.x);
    // assertEquals(0,testListener2.y);
    //	
    // //erste abschiessen
    // client.shutdown();
    // Thread.sleep(500);
    //
    // TestBlinkendroidListener testListener3 = new TestBlinkendroidListener();
    // BlinkendroidClient2 client3 = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener3);
    // client3.start();
    // Thread.sleep(500);
    // assertFalse(testListener3.connectionClosed);
    // assertEquals(0,testListener3.x);
    // assertEquals(0,testListener3.y);
    // }
    //    
    // public void testConnection() throws Exception{
    // //start server
    // TestConnectionListener connectionListener = new TestConnectionListener();
    // BlinkendroidServer blinkendroidServer = new BlinkendroidServer(
    // connectionListener, Constants.SERVER_PORT);
    // blinkendroidServer.start();
    //	
    // Thread.sleep(1000);
    //
    //	
    // TestBlinkendroidListener testListener = new TestBlinkendroidListener();
    // BlinkendroidClient client = new BlinkendroidClient(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
    // client.start();
    // Thread.sleep(1000);
    // assertTrue(connectionListener.open);
    //	
    // assertFalse(testListener.connectionClosed);
    // assertTrue(testListener.startTime>0);
    // assertTrue(testListener.serverTime>0);
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)1.0);
    // assertEquals(testListener.endY,(float)1.0);
    //	
    // long serverTime=testListener.serverTime;
    // Thread.sleep(5001);
    // assertTrue(serverTime<testListener.serverTime);
    // client.shutdown();
    // Thread.sleep(1000);
    // assertTrue(testListener.connectionClosed);
    //	
    // blinkendroidServer.shutdown();
    // Thread.sleep(1000);
    // assertFalse(connectionListener.open);
    // }

    public void testConnectionServerShutdown() throws Exception {
	// start server
	TestConnectionListener connectionListener = new TestConnectionListener();
	BlinkendroidServer blinkendroidServer = new BlinkendroidServer(
		connectionListener, Constants.SERVER_PORT);
	blinkendroidServer.start();

	Thread.sleep(1000);

	TestBlinkendroidListener testListener = new TestBlinkendroidListener();
	BlinkendroidClient client = new BlinkendroidClient(
		new InetSocketAddress(IP, Constants.SERVER_PORT), testListener);
	client.start();
	Thread.sleep(1000);
	assertTrue(connectionListener.open);

	assertFalse(testListener.connectionClosed);
	assertTrue(testListener.startTime > 0);
	assertTrue(testListener.serverTime > 0);
	assertEquals(testListener.startX, (float) 0.0);
	assertEquals(testListener.startY, (float) 0.0);
	assertEquals(testListener.endX, (float) 1.0);
	assertEquals(testListener.endY, (float) 1.0);

	long serverTime = testListener.serverTime;
	Thread.sleep(5001);
	assertTrue(serverTime < testListener.serverTime);

	blinkendroidServer.shutdown();
	Thread.sleep(1000);
	assertFalse(connectionListener.open);

	// client.shutdown();
	// Thread.sleep(1000);
	assertTrue(testListener.connectionClosed);
    }
    // public void test2Connection() throws Exception{
    // TestBlinkendroidListener testListener = new TestBlinkendroidListener();
    // BlinkendroidClient2 client = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
    // client.start();
    // Thread.sleep(1000);
    // assertFalse(testListener.connectionClosed);
    // assertTrue(testListener.startTime>0);
    // assertTrue(testListener.serverTime>0);
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)1.0);
    // assertEquals(testListener.endY,(float)1.0);
    // assertEquals(testListener.x,0);
    // assertEquals(testListener.y,0);
    //	
    // long serverTime=testListener.serverTime;
    // Thread.sleep(5001);
    // assertTrue(serverTime<testListener.serverTime);
    //
    //	
    // //zweites handy dazu
    // TestBlinkendroidListener testListener2 = new TestBlinkendroidListener();
    // BlinkendroidClient2 client2 = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener2);
    // client2.start();
    // Thread.sleep(1000);
    // assertFalse(testListener2.connectionClosed);
    // assertTrue(testListener2.startTime>0);
    // assertTrue(testListener2.serverTime>0);
    // assertEquals(testListener2.startX,(float)0.5);
    // assertEquals(testListener2.startY,(float)0.0);
    // assertEquals(testListener2.endX,(float)1.0);
    // assertEquals(testListener2.endY,(float)1.0);
    // assertEquals(testListener2.x,1);
    // assertEquals(testListener2.y,0);
    //	
    // Thread.sleep(5001);
    // assertTrue(serverTime<testListener.serverTime);
    //	
    // //erstes handy brauch reclip
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)0.5);
    // assertEquals(testListener.endY,(float)1.0);
    //	
    // client.shutdown();
    // Thread.sleep(1000);
    // assertTrue(testListener.connectionClosed);
    //	
    // client2.shutdown();
    // Thread.sleep(1000);
    // assertTrue(testListener2.connectionClosed);
    //	
    // }
    // public void test2ConnectionRemoved() throws Exception{
    // TestBlinkendroidListener testListener = new TestBlinkendroidListener();
    // BlinkendroidClient2 client = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
    // client.start();
    // Thread.sleep(1000);
    // assertFalse(testListener.connectionClosed);
    // assertTrue(testListener.startTime>0);
    // assertTrue(testListener.serverTime>0);
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)1.0);
    // assertEquals(testListener.endY,(float)1.0);
    // assertEquals(testListener.x,0);
    // assertEquals(testListener.y,0);
    //	
    // long serverTime=testListener.serverTime;
    // Thread.sleep(5001);
    // assertTrue(serverTime<testListener.serverTime);
    //
    //	
    // //zweites handy dazu
    // TestBlinkendroidListener testListener2 = new TestBlinkendroidListener();
    // BlinkendroidClient2 client2 = new BlinkendroidClient2(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener2);
    // client2.start();
    // Thread.sleep(1000);
    // assertFalse(testListener2.connectionClosed);
    // assertTrue(testListener2.startTime>0);
    // assertTrue(testListener2.serverTime>0);
    // assertEquals(testListener2.startX,(float)0.5);
    // assertEquals(testListener2.startY,(float)0.0);
    // assertEquals(testListener2.endX,(float)1.0);
    // assertEquals(testListener2.endY,(float)1.0);
    // assertEquals(testListener2.x,1);
    // assertEquals(testListener2.y,0);
    //	
    // Thread.sleep(5001);
    // assertTrue(serverTime<testListener.serverTime);
    //	
    // //erstes handy brauch reclip
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)0.5);
    // assertEquals(testListener.endY,(float)1.0);
    //	
    // client2.shutdown();
    // Thread.sleep(1000);
    // assertTrue(testListener2.connectionClosed);
    //	
    // //jetzt muss es reclip geben
    // //erstes handy brauch reclip
    // assertEquals(testListener.startX,(float)0.0);
    // assertEquals(testListener.startY,(float)0.0);
    // assertEquals(testListener.endX,(float)1.0);
    // assertEquals(testListener.endY,(float)1.0);
    //	
    // client.shutdown();
    // Thread.sleep(1000);
    // assertTrue(testListener.connectionClosed);
    //	
    //
    //	
    // }
    // public void test100Connections() throws Exception{
    // int x=0;
    // int y=0;
    // for(int i=0;i<100;i++){
    // TestBlinkendroidListener testListener = new TestBlinkendroidListener();
    // BlinkendroidClient client = new BlinkendroidClient(new
    // InetSocketAddress(IP,Constants.SERVER_PORT), testListener);
    // client.start();
    // Thread.sleep(500);
    // // //calc expected positions
    // // int maxX = (int)Math.floor(Math.sqrt(i));
    // // assertFalse(testListener.connectionClosed);
    // // assertTrue(testListener.startTime>0);
    // // assertTrue(testListener.serverTime>0);
    // // assertTrue(testListener.resId>0);
    // }
    // Thread.sleep(60000);
    // }

}
