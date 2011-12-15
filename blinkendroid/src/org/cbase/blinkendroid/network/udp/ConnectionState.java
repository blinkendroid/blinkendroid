package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * datapacket:
 * PROTO [Connection]
 * COMMAND [SYN|ACK|SYNACK|..]
 * ConnectionID [random connection id]
 */

public class ConnectionState implements CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionState.class);

    public static enum Command {
	SYN, ACK, SYNACK, RESET, HEARTBEAT, REQUEST_DIRECT_HEARTBEAT, CANCEL_DIRECT_HEARTBEAT
    }

    public static enum Connstate {
	NONE, SYNWAIT, SYNACKWAIT, ACKWAIT, ESTABLISHED
    }

    public Connstate m_state;
    protected int m_connId;
    // private InetSocketAddress m_SocketAddr;
    private ConnectionListener m_Listener;
    protected long m_LastSeen;
    private ClientSocket mClientSocket;
    private DirectTimerThread directTimerThread;

    /**
     * This thread sends the global time to connected devices.
     */
    class DirectTimerThread extends Thread {
	String name;

	public DirectTimerThread(String name) {
	    this.name = name;
	}

	volatile private boolean running = true;

	@Override
	public void run() {
	    this.setName("SRV Send DirectTimer");
	    logger.info("DirectTimerThread started");
	    while (running) {
		try {
		    Thread.sleep(BlinkendroidApp.HEARTBEAT_RATE);
		} catch (InterruptedException e) {
		    // swallow
		}
		if (!running) // fast exit
		    break;

		logger.info("DirectTimerThread " + name);
		ByteBuffer out = ByteBuffer.allocate(128);
		out.putInt(BlinkendroidApp.PROTOCOL_HEARTBEAT);
		out.putInt(ConnectionState.Command.HEARTBEAT.ordinal());
		out.putInt(BlinkendroidApp.DIRECTTIMER);
		out.putLong(System.currentTimeMillis());
		try {
		    // ConnectionState.this.send(out); this adds protocol 1
		    mClientSocket.send(out);
		} catch (IOException e) {
		    logger.error("", e);
		}
	    }
	    logger.info("DirectTimerThread stopped");
	}

	public void shutdown() {
	    running = false;
	    interrupt();
	    try {
		join();
	    } catch (InterruptedException e) {
		logger.error(" DirectTimerThread shutdown join failed");
	    }
	    logger.info("DirectTimerThread initiating shutdown");
	}
    }

    /**
     * 
     * @param connection
     *            interface Connection which used to send packets
     * @param socketAddr
     *            contains destination ip + port
     * @param listener
     *            will be informed about connection state changes
     */
    public ConnectionState(ClientSocket clientSocket, ConnectionListener listener) {
	m_state = Connstate.NONE;
	m_connId = 0;
	mClientSocket = clientSocket;
	m_Listener = listener;
    }

    public void shutdown() {
	sendReset();
    }

    public void handle(SocketAddress socketAddr, ByteBuffer bybuff) throws IOException {
	final int iCommand = bybuff.getInt();
	final int connId = bybuff.getInt();

	if (Command.values().length <= iCommand || iCommand < 0) {
	    // ignore unknown commands return;
	    logger.warn("unknown command: " + iCommand);
	    return;
	}

	final Command command = Command.values()[iCommand];

	// syn is a special case, because we dont have a connection
	if (command == Command.SYN) {
	    receivedSyn(connId);
	    return;
	}

	// heartbeat is also a special case, because it does not need a connId
	if (command == Command.HEARTBEAT) {
	    receivedHeartbeat(connId);
	    return;
	}

	if (connId != this.m_connId) {
	    return; // TODO send reset - not our connection
	}

	switch (command) {
	case ACK:
	    receivedAck();
	    break;
	case SYNACK:
	    receivedSynAck();
	    break;
	case RESET:
	    receivedReset();
	    break;
	case REQUEST_DIRECT_HEARTBEAT:
	    receivedDirectHeartbeatRequest();
	    break;
	case CANCEL_DIRECT_HEARTBEAT:
	    receivedDirectHeartbeatCancel();
	}
    }

    public void openConnection() {
	sendSyn();
    }

    protected void stateChange(Connstate newState) {
	logger.info("ConnectionStateChanged " + newState);
	if (newState == m_state) {
	    return;
	}

	if (newState == Connstate.NONE) {
	    // if there is a directtimerthread running for this client kill him
	    if (null != directTimerThread) {
		directTimerThread.shutdown();
		try {
		    directTimerThread.join(1000);
		} catch (InterruptedException e) {
		    // swallow bitch
		}
	    }
	    m_Listener.connectionClosed(mClientSocket);
	}

	if (newState == Connstate.ESTABLISHED) {
	    m_LastSeen = System.currentTimeMillis();
	    m_Listener.connectionOpened(mClientSocket);
	}

	m_state = newState;

    }

    protected void receivedSynAck() throws IOException {
	logger.info("receivedSynAck");
	if (m_state == Connstate.SYNACKWAIT) {
	    stateChange(Connstate.ESTABLISHED);
	    sendAck();
	}
    }

    protected void receivedSyn(int connId) {
	if (m_state == Connstate.NONE) {
	    m_connId = connId;
	    logger.info("receivedSyn");
	    sendSynAck();
	    stateChange(Connstate.ACKWAIT);
	}
    }

    protected void receivedAck() {
	if (m_state == Connstate.ACKWAIT) {
	    logger.info("receivedAck");
	    stateChange(Connstate.ESTABLISHED);
	}
    }

    protected void receivedReset() {
	logger.info("receivedReset");
	stateChange(Connstate.NONE);
    }

    protected void receivedDirectHeartbeatRequest() {
	logger.info("receivedDirectHeartbeatRequest " + m_connId);
	if (directTimerThread == null) {
	    directTimerThread = new DirectTimerThread(toString());
	    directTimerThread.start();
	}
    }

    protected void receivedDirectHeartbeatCancel() {
	logger.info("receivedDirectHeartbeatCancel " + m_connId);
	if (directTimerThread != null) {
	    directTimerThread.shutdown();
	    directTimerThread = null;
	}
    }

    protected void receivedHeartbeat(int timerStyle) {
	m_LastSeen = System.currentTimeMillis();
    }

    protected void sendSyn() {
	logger.info("sendSyn");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.SYN.ordinal());
	m_connId = (int) System.currentTimeMillis();
	out.putInt(m_connId);// TODO set uuid
	stateChange(Connstate.SYNACKWAIT);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendSyn caused an Exception", e);
	}
    }

    protected void sendSynAck() {
	logger.info("sendSynAck " + m_connId);
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.SYNACK.ordinal());
	out.putInt(m_connId);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendSynAck failed", e);
	}
    }

    protected void sendAck() {
	logger.info("sendAck");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.ACK.ordinal());
	out.putInt(m_connId);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendAck failed", e);
	}
    }

    protected void sendReset() {
	logger.info("sendReset");
	ByteBuffer out = ByteBuffer.allocate(1024);
	out.putInt(Command.RESET.ordinal());
	out.putInt(m_connId);
	try {
	    send(out);
	} catch (IOException e) {
	    logger.error("sendReset failed", e);
	}

	stateChange(Connstate.NONE);
    }

    /**
     * 
     * @param timeout
     *            in seconds
     */
    public void checkTimeout(int timeout) {
	if (m_state != Connstate.ESTABLISHED) {
	    return;
	}
	long time = System.currentTimeMillis();
	if (m_LastSeen + timeout * 1000 < time) {
	    logger.info("Client Timeout\n");
	    sendReset();
	}
    }

    protected void send(ByteBuffer command) throws IOException {
	ByteBuffer out = ByteBuffer.allocate(command.position() + Integer.SIZE);
	out.putInt(BlinkendroidApp.PROTOCOL_CONNECTION); /* protocol header */
	out.put(command.array(), 0, command.position());
	mClientSocket.send(out);
    }

    public int getConnectionId() {
	return m_connId;
    }
}