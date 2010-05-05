package org.cbase.blinkendroid.utils;

import org.cbase.blinkendroid.Blinkendroid;

import android.widget.Toast;

/**
 * Creates and posts Toast from a Thread other than the Blinkendroid Thread.
 */
public class ToastPoster implements Runnable {

    /**
     * The running blinkendroid.
     */
    private Blinkendroid blinkendroid;
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
    public ToastPoster(Blinkendroid blinkendroid, String message, int length) {
	this.blinkendroid = blinkendroid;
	this.message = message;
	this.length = length;
	blinkendroid.getHandler().post(this);
    }
    public void run() {
	Toast.makeText(blinkendroid, message, length).show();
    }
}