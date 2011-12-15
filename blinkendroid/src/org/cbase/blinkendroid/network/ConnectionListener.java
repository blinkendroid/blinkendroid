package org.cbase.blinkendroid.network;

import org.cbase.blinkendroid.network.udp.ClientSocket;

public interface ConnectionListener {
  // TODO ClientSocket should be replaced by SocketAddress
  void connectionClosed(ClientSocket clientSocket);

  void connectionOpened(ClientSocket clientSocket);
}