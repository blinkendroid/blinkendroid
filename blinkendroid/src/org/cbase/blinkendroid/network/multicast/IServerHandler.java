package org.cbase.blinkendroid.network.multicast;

public interface IServerHandler {

    void foundServer(String serverName, String serverIp, int protocolVersion);

    void foundUnknownServer(int protocolVersion);
}
