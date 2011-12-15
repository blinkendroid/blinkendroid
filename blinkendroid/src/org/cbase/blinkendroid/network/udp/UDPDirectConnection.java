package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public interface UDPDirectConnection {
  void send(InetSocketAddress socketAddr, ByteBuffer out) throws IOException;
}
