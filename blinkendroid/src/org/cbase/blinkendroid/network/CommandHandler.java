package org.cbase.blinkendroid.network;

import java.io.BufferedInputStream;

public interface CommandHandler {

    void handle(BufferedInputStream bis);
}
