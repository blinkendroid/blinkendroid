package org.cbase.blinkendroid;

import android.widget.Toast;

public class ToastPoster implements Runnable {

	private Blinkendroid blinkendroid;
	private String message;
	private int length;
	public ToastPoster(Blinkendroid blinkendroid, String message, int length) {
		this.blinkendroid	=	blinkendroid;
		this.message		=	message;
		this.length			=	length;
		blinkendroid.getHandler().post(this);
	}

	public void run() {
		Toast.makeText(blinkendroid, message, length);
	}

}
