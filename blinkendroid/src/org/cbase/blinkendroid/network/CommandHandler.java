package org.cbase.blinkendroid.network;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface CommandHandler {

    void handle(BufferedInputStream bis) throws IOException;
}
