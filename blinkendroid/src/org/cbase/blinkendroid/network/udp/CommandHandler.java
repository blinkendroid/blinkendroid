package org.cbase.blinkendroid.network.udp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public interface CommandHandler {
  void handle(SocketAddress socketAddr, ByteBuffer bybuff) throws IOException;
}
