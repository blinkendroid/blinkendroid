package org.cbase.blinkendroid.network;

import java.net.InetAddress;

public interface ConnectionListener {

    void connectionClosed(InetAddress inetAddress);

    void connectionOpened(InetAddress inetAddress);
}