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

import org.cbase.blinkendroid.Blinkendroid;
import org.cbase.blinkendroid.OldBlinkendroid;

import android.widget.Toast;

/**
 * Creates and posts Toast from a Thread other than the Blinkendroid Thread.
 */
public class ToastPoster implements Runnable {

    /**
     * The running blinkendroid.
     */
    private OldBlinkendroid blinkendroid;
    /**
     * The message to Toast.
     */
    private String message;
    /**
     * Visibility length
     */
    private int length;

    /**
     * Creates a new {@link ToastPoster}.
     * @param blinkendroid The running {@link Blinkendroid}.
     * @param message The message to Toast.
     * @param length Visibility length. Usually Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     */
    public ToastPoster(OldBlinkendroid blinkendroid, String message, int length) {
	this.blinkendroid = blinkendroid;
	this.message = message;
	this.length = length;
	blinkendroid.getHandler().post(this);
    }

    public void run() {
	Toast.makeText(blinkendroid, message, length).show();
    }
}