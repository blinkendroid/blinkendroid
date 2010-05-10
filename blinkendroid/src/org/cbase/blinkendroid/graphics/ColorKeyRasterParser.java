package org.cbase.blinkendroid.graphics;
import java.util.ArrayList;

import org.cbase.blinkendroid.geom.Pixel;
import org.cbase.blinkendroid.utils.BitmapUtils;

/**
 * This Class iterates through the bitmap Raster (actually int[])
 * and detects all pixels matching our color key within a given threshold
 * @author dima
 */
public class ColorKeyRasterParser {

	private int[] pixels;
	private int height;
	private int width;
	private int keyColor;
	private int colorKeyThreshold;
	
	/**
	 * Constructs an instance of ColorKeyRasterParser
	 * @param pixels bitmap data
	 * @param width width of the bitmap
	 * @param height height of the bitmap
	 * @param keyColor key color we're looking for
	 * @param colorKeyThreshold threshold/tolerance for selection
	 */
	public ColorKeyRasterParser(int[] pixels, int width, int height, int keyColor, int colorKeyThreshold) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.keyColor = keyColor;
		this.colorKeyThreshold = colorKeyThreshold;
	}
	
	/**
	 * Parses the passed pixel-Raster and 
	 * @return ArrayList<Pixel> the pixels matching our color key within the threshold
	 */
	public ArrayList<Pixel> detectEdges() {
		ArrayList<Pixel> matches = new ArrayList<Pixel>();
		
		// getting the Key's channels
		int[] keyChannels = BitmapUtils.getChannels(keyColor);
		int keyRed = keyChannels[BitmapUtils.RED_CHANNEL];
		int keyGreen = keyChannels[BitmapUtils.GREEN_CHANNEL];
		int keyBlue = keyChannels[BitmapUtils.BLUE_CHANNEL];
		
		for(int y = 0; y < height; y++) {					
			for(int x = 0; x < width; x++) {			

				// the current pixels index in the one-dimensional raster array
				int curPixelIdx = y * width + x;
				
				// current pixels channels
				int[] pixelChannels = BitmapUtils.getChannels(pixels[curPixelIdx]);
				int pixelRed = pixelChannels[BitmapUtils.RED_CHANNEL];
				int pixelGreen = pixelChannels[BitmapUtils.GREEN_CHANNEL];
				int pixelBlue = pixelChannels[BitmapUtils.BLUE_CHANNEL];
				
				int diffRed = Math.abs(pixelRed-keyRed);
				int diffGreen = Math.abs(pixelGreen-keyGreen);
				int diffBlue = Math.abs(pixelBlue-keyBlue);
				
				if ((diffRed < colorKeyThreshold
					&& diffGreen  < colorKeyThreshold
					&& diffBlue < colorKeyThreshold)) {
						// color matches, adding this pixel to our list
						matches.add(new Pixel(x, y));
				}	
			}
		}
		
		return matches;
	}
	
}
