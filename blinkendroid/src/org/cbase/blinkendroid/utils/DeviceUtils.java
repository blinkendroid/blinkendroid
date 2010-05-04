package org.cbase.blinkendroid.utils;

import android.app.Activity;
import android.telephony.TelephonyManager;

public class DeviceUtils {
    /**
     * Gets the IMEI of the running device
     * @param a the calling activity.
     * @return the IMEI
     */
    public static String getImei(Activity a) {
	return ((TelephonyManager) a.getSystemService(Activity.TELEPHONY_SERVICE))
		.getDeviceId();
    }
}
