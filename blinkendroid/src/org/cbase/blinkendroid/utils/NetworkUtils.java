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

package org.cbase.blinkendroid.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.cbase.blinkendroid.Constants;

import android.util.Log;

/**
 * Utilities for networking related methods.
 */
public class NetworkUtils {

    /**
     * Gets the IPAdress of the local device
     * 
     * @return The IPAdress or null if none was found
     */
    public static String getLocalIpAddress() {
	try {
	    for (Enumeration<NetworkInterface> en = NetworkInterface
		    .getNetworkInterfaces(); en.hasMoreElements();) {
		NetworkInterface intf = en.nextElement();
		for (Enumeration<InetAddress> enumIpAddr = intf
			.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		    InetAddress inetAddress = enumIpAddr.nextElement();
		    if (!inetAddress.isLoopbackAddress()) {
			return inetAddress.getHostAddress().toString();
		    }
		}
	    }
	} catch (SocketException ex) {
	    Log.e(Constants.LOG_TAG, ex.toString());
	}
	return null;
    }
}
