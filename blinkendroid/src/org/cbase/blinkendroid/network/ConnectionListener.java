package org.cbase.blinkendroid.network;

import java.net.SocketAddress;

public interface ConnectionListener {

    public abstract void connectionClosed(SocketAddress socketAddress);
    public abstract void connectionOpened(SocketAddress socketAddress);
}