package org.cbase.mobilecloud;

import java.nio.ByteBuffer;

public interface CommunicationManager {

    // recieving date
    void registerProtocolHandler(ProtocolHandler protocolHandler);

    // sending data
    void broadcast(ByteBuffer buffer);

    void send(Node node, ByteBuffer buffer);
}
