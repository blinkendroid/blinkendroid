package org.cbase.blinkendroid.network.broadcast;

public interface IPeerHandler {

    void foundPeer(String name, String ip, int protocolVersion, int pin);

}
