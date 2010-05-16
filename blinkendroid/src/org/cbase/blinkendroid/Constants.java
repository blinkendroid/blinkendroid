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

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * @author Andreas Schildbach
 */
public final class Constants {

    public static final String LOG_TAG = "blinkendroids";
    public static final int MULTICAST_SERVER_PORT = 6789;
    public static final String MULTICAST_GROUP = "224.0.0.1";
    public static final String SERVER_MULTICAST_COMMAND = "BLINKENDROID_SERVER";    
}
