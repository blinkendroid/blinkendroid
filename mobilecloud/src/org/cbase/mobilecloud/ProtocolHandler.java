package org.cbase.mobilecloud;

import java.nio.ByteBuffer;

public interface ProtocolHandler {
    void handle(Node node, ByteBuffer buffer);
}
