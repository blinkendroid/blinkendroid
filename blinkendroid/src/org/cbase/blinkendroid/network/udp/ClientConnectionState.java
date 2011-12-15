package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectionState extends ConnectionState implements CommandHandler {

    private ClientConnectionHeartbeat mHeartbeater;

    private int directTimerThreadRequestCounter = 3;
    private int timerStyle = BlinkendroidApp.GLOBALTIMER;

    private static final Logger logger = LoggerFactory.getLogger(ClientConnectionState.class);

    public ClientConnectionState(ClientSocket clientSocket, ConnectionListener listener) {
	super(clientSocket, listener);
	mHeartbeater = new ClientConnectionHeartbeat();
    }

    @Override
    protected void stateChange(Connstate state) {
	if (state == Connstate.ESTABLISHED) {
	    startHeartbeat();
	}
	if (state == Connstate.NONE) {
	    stopHeartbeat();
	}
	super.stateChange(state);
    }

    public void startHeartbeat() {
	mHeartbeater.start();
    }

    public void stopHeartbeat() {
	mHeartbeater.shutdown();
    }

    @Override
    protected void receivedHeartbeat(int ts) {
	m_LastSeen = System.currentTimeMillis();
	// check timerStyle
	if (this.timerStyle == BlinkendroidApp.DIRECTTIMER && ts == BlinkendroidApp.GLOBALTIMER) {
	    sendDirectHeartbeatCancel();
	    this.timerStyle = BlinkendroidApp.GLOBALTIMER;
	}
    }

    @Override
    public void checkTimeout(int timeout) {
	if (m_state != Connstate.ESTABLISHED) {
	    return;
	}
	long time = System.currentTimeMillis();
	if (m_LastSeen + timeout * 1000 < time) {
	    logger.info("Server Timeout\n");
	    // request 3 times directHeartBeat
	    if (directTimerThreadRequestCounter > 0) {
		sendDirectHeartbeatRequest();
		timerStyle = BlinkendroidApp.DIRECTTIMER;
		directTimerThreadRequestCounter--;
		// reset last_seen
		m_LastSeen = System.currentTimeMillis();
	    } else
		sendReset();
	}
    }

    protected void sendDirectHeartbeatRequest() {
	logger.info("sendDirectHeartbeatRequest");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.REQUEST_DIRECT_HEARTBEAT.ordinal());
	out.putInt(m_connId);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("requestDirectHeartbeat failed", e);
	}
    }

    protected void sendDirectHeartbeatCancel() {
	logger.info("sendDirectHeartbeatCancel");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.CANCEL_DIRECT_HEARTBEAT.ordinal());
	out.putInt(m_connId);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendDirectHeartbeatCancel failed", e);
	}
    }

    protected void sendHeartbeat() {
	// logger.debug("sendHeartbeat");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.HEARTBEAT.ordinal());
	out.putInt(m_connId);
	out.putInt(timerStyle);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendHeartbeat failed", e);
	}
    }

    /**
     * This thread sends heartbeat to the server
     */
    class ClientConnectionHeartbeat extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    this.setName("CLI Heartbeat");
	    logger.info("ClientConnectionState started");
	    while (running) {
		try {
		    Thread.sleep(BlinkendroidApp.HEARTBEAT_RATE);
		} catch (InterruptedException e) {
		    // swallow
		}
		if (!running) // fast exit
		    break;
		sendHeartbeat();
		checkTimeout(BlinkendroidApp.CONNECT_TIMEOUT);
	    }
	    logger.info("ClientConnectionState stopped");
	}

	public void shutdown() {
	    running = false;
	    interrupt();
	    try {
		join();
	    } catch (InterruptedException e) {
		logger.error(" ClientConnectionHeartbeat shutdown join failed");
	    }
	    logger.info("ClientConnectionState initiating shutdown");
	}
    }

}
