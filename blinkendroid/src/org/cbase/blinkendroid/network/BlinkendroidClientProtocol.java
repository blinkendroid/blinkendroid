package org.cbase.blinkendroid.network;

import java.io.IOException;
import java.net.Socket;

public class BlinkendroidClientProtocol extends AbstractBlinkendroidProtocol {

    protected BlinkendroidClientProtocol(Socket socket,
	    ConnectionListener connectionListener) throws IOException {
	super(socket, connectionListener,false);
    }

}
