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
