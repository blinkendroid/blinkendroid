package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class ClientSocket {
    private UDPDirectConnection mSocket;
    private InetSocketAddress mSocketAddr;

    public UDPDirectConnection getDirectConnection() {
	return mSocket;
    }

    public int getDestinationPort() {
	return mSocketAddr.getPort();
    }

    public InetAddress getDestinationAddress() {
	return mSocketAddr.getAddress();
    }

    public InetSocketAddress getInetSocketAddress() {
	return mSocketAddr;
    }

    // create socket between localhost:<port> - addr:<port>
    public ClientSocket(UDPDirectConnection socket, InetSocketAddress socketAddr)
	    throws SocketException {
	this.mSocket = socket;
	this.mSocketAddr = socketAddr;
    }

    public void send(ByteBuffer out) throws IOException {

	mSocket.send(mSocketAddr, out);
    }

    @Override
	public String toString() {
	return getDestinationAddress() + ":" + getDestinationPort();
    }
}
