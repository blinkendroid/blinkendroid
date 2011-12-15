/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * Our Application class
 * 
 * @author Benjamin Weiss (c) 2010
 */
public class BlinkendroidApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidApp.class);

    public static final int BROADCAST_ANNOUCEMENT_SERVER_PORT = 6998;
    public static final int BROADCAST_ANNOUCEMENT_CLIENT_PORT = 6999;
    public static final int BROADCAST_ANNOUCEMENT_CLIENT_TICKET_PORT = 7000;
    public static final int BROADCAST_ANNOUCEMENT_SERVER_TICKET_PORT = 7001;
    public static final int BROADCAST_CLIENT_PORT = 6789;
    public static final int BROADCAST_SERVER_PORT = 6790;
    public static final int BROADCAST_IDLE_THRESHOLD = 10000;
    public static final int BROADCAST_RATE = 5000;

    public static final String MULTICAST_GROUP = "230.0.0.1";
    public static final String CLIENT_BROADCAST_COMMAND = "BLINKENDROID_CLIENT";
    public static final String SERVER_TICKET_COMMAND = "BLINKENDROID_TICKET";
    // public static final int SERVER_PORT = 9876;
    public static final int CONNECT_TIMEOUT = 10;
    public static final int HEARTBEAT_RATE = 1000;
    public static final int SHOW_OWNER_DURATION = 1500;
    public static final int BROADCAST_PROTOCOL_VERSION = 6;

    public static final int DIRECTTIMER = 68;
    public static final int GLOBALTIMER = 71;

    public static final int PROTOCOL_CONNECTION = 1;
    public static final int PROTOCOL_PLAYER = 42;
    public static final int PROTOCOL_HEARTBEAT = 23;
    public static final int PROTOCOL_CLIENT = 24;

    public static final int INTERACTIVE_MODE = 6;
    public static final int NON_INTERACTIVE_MODE = 5;

    public static final int PICK_FROM_GALLERY = 666;

    private String downloadUrl;
    private String aboutUrl;

    private WakeLock wakeLock;

    private static BlinkendroidApp app;

    @Override
    public void onCreate() {
	super.onCreate();

	wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.FULL_WAKE_LOCK,
		"BlinkendroidApp");
	app = this;
    }

    public static BlinkendroidApp getApp() {
	return app;
    }

    public String getAboutUrl() {
	return (aboutUrl == null) ? getString(R.string.about_url) : aboutUrl;
    }

    public String getDownloadUrl() {
	return (downloadUrl == null) ? getString(R.string.download_url) : downloadUrl;
    }

    public void wantWakeLock(boolean doWant) {
	if (doWant && !wakeLock.isHeld()) {
	    wakeLock.acquire();
	} else if (!doWant && wakeLock.isHeld()) {
	    wakeLock.release();
	}
	logger.info("WakeLock is " + wakeLock.isHeld());
    }

}
