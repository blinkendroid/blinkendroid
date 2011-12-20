package org.cbase.blinkendroid.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import org.cbase.blinkendroid.BlinkendroidApp;
import org.cbase.blinkendroid.network.ConnectionListener;
import org.cbase.blinkendroid.network.broadcast.IPeerHandler;
import org.cbase.blinkendroid.network.udp.ClientSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TicketManager implements IPeerHandler, ConnectionListener {

	private static final Logger logger = LoggerFactory.getLogger(TicketManager.class);

	int maxClients = 0;
	int clients = 0;
	private Set<String> tickets = new HashSet<String>();
	private Set<String> waitingQueue = new HashSet<String>();
	private String serverName;
	private int pin;

	DatagramSocket socket = null;
	private ClientQueueListener clientQueueListener = null;

	public TicketManager() {
		this.serverName = "Blinkendroid";
		this.pin = 0;
	}

	public boolean start() {
		try {
			socket = new DatagramSocket(BlinkendroidApp.BROADCAST_ANNOUCEMENT_SERVER_TICKET_PORT);
			socket.setReuseAddress(true);
			return true;
		}
		catch (SocketException e) {
			logger.error("new DatagramSocket(Constants.BROADCAST_ANNOUCEMENT_SERVER_TICKET_PORT) failed " + e.getMessage(), e);
			return false;
		}
	}

	public void foundPeer(String name, String ip, int protocolVersion, int p) {
		// version
		if (protocolVersion < BlinkendroidApp.BROADCAST_PROTOCOL_VERSION) {
			logger.warn("old version '" + protocolVersion + "' user: " + name + " ip: " + ip);
			return;
		}
		// noch platz frei?
		if (maxClients == 0 || clients < maxClients || tickets.contains(ip)) {
			// send ticket to ip
			try {
				InetSocketAddress socketAddr = new InetSocketAddress(ip, BlinkendroidApp.BROADCAST_ANNOUCEMENT_CLIENT_TICKET_PORT);
				String message = BlinkendroidApp.BROADCAST_PROTOCOL_VERSION + " " + BlinkendroidApp.SERVER_TICKET_COMMAND + " " + serverName + " " + this.pin;
				logger.debug("ticket: " + message);
				final byte[] messageBytes = message.getBytes("UTF-8");
				final DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, socketAddr);
				socket.send(packet);
				if (!tickets.contains(ip)) {
					clients++;
					tickets.add(ip);
					waitingQueue.remove(ip);
					if (clientQueueListener != null) {
						clientQueueListener.clientNoLongerWaiting(ip);
						logger.info("send ticket for " + name + " " + ip);
					}
				}
				else {
					logger.info("resend sent ticket for " + name + " " + ip);
				}
			}
			catch (Exception e) {
				logger.error("Exception in TicketManager", e);
			}
		}
		else {
			if (!waitingQueue.contains(ip)) {
				waitingQueue.add(ip);
				if (clientQueueListener != null) {
					clientQueueListener.clientWaiting(ip);
				}
			}
			logger.info("Server is full, adding to queue");
		}
		// pech jehabt
	}

	public void setClientQueueListener(ClientQueueListener clientQueueListener) {
		this.clientQueueListener = clientQueueListener;
	}

	public void reset() {
		if (null != tickets)
			tickets.clear();
		if (null != waitingQueue)
			waitingQueue.clear();
		if (null != socket)
			socket.close();
	}

	public void connectionClosed(ClientSocket clientSocket) {
		String ip = clientSocket.getDestinationAddress().getHostAddress();
		clients--;
		tickets.remove(ip);
		if (null != clientQueueListener)
			clientQueueListener.clientNoLongerWaiting(ip);
		waitingQueue.remove(ip);
	}

	// /**
	// * @return the waitingQueue
	// */
	// public Set<String> getWaitingQueue() {
	// return waitingQueue;
	// }

	public void clientStateChangedFromWaitingToConnected(String ip) {
		tickets.add(ip);
		waitingQueue.remove(ip);
		clients++;
		// maxClients++;
	}

	public void connectionOpened(ClientSocket clientSocket) {
		// TODO clients merken und abhaken
	}

	public int getMaxClients() {
		return maxClients;
	}

	public void setMaxClients(int maxClients) {
		this.maxClients = maxClients;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

}
