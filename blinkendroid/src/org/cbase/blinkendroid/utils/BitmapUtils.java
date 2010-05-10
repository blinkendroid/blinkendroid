package org.cbase.blinkendroid.utils;

import android.graphics.Bitmap;

public class BitmapUtils {
	
	public static final int RED_CHANNEL = 0;
	public static final int GREEN_CHANNEL = 1;
	public static final int BLUE_CHANNEL = 2;

	/**
	 * Scales Bitmap to passed size, preserving the images orientation
	 * @param image
	 * @param max
	 * @param min
	 * @return scaled Bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap image, int max, int min) {
		
		int width;
		int height;
		
		if(image.getHeight() > image.getWidth()) {
			width = min;
			height = max;
		}
		else {
			width = max;
			height = min;			
		}
		
		return Bitmap.createScaledBitmap(image, width, height, true);
	}
	
	public static int[] getChannels(int[] pixels, int x, int y, int width, int height) {
		int index = y * width + x;
		return getChannels(pixels[index]);
	}
	
	public static int[] getChannels(int color) {
		int[] channels = new int[3];
		
		channels[RED_CHANNEL] = (color >> 16) & 0xFF;
		channels[GREEN_CHANNEL] = (color >> 8) & 0xFF;
		channels[BLUE_CHANNEL] = color & 0xFF;
		
		return channels;		
	}
}
