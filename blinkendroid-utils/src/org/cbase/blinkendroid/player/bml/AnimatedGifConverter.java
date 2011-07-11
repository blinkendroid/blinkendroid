package org.cbase.blinkendroid.player.bml;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.cbase.blinkendroid.player.bml.BLM.Frame;

import com.java2s.code.java.GifDecoder;

public class AnimatedGifConverter {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		GifDecoder decoder = new GifDecoder();

		int result = decoder.read("file:Nyancat.gif");
		System.out.println("Finished reading gif with result " + result);

		String name = "nyan";
		Dimension size = decoder.getFrameSize();
		BLM blm = new BLM();
		blm.header = new BLMHeader();
		blm.header.width = size.width;
		blm.header.height = size.height;
		blm.header.bits = 8;
		blm.header.color = true;
		blm.header.title = "nyan";
		blm.frames = new ArrayList<Frame>();
		System.out.printf("Got %d frames\n", decoder.getFrameCount());
		for (int i = 1; i < decoder.getFrameCount(); i++) {
			BufferedImage image = decoder.getFrame(i);
			BLM.Frame f = new BLM.Frame();
			f.duration = decoder.getDelay(i);
			f.matrix = new byte[blm.header.height][blm.header.width];
			System.out.printf("Image dimensions are height: %d; width: %d \n",
					image.getHeight(), image.getWidth());
			blm.frames.add(f);
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int[] rgb = new int[4];
					image.getRaster().getPixel(x, y, rgb);

					byte b = (byte) ((byte) ((rgb[0] / 32) << 5)
							+ (byte) ((rgb[1] / 32) << 2) + (byte) ((rgb[2] / 64)));
					f.matrix[y][x] = b;
				}
			}
		}
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
				"/Users/ben/workspace/private/blinkendroid/blinkendroid-utils/nyan/bbm/"
						+ name + ".bbm"));
		out.writeObject(blm);
		out.flush();
		out.close();
		// die infofiles für
		out = new ObjectOutputStream(new FileOutputStream(
				"/Users/ben/workspace/private/blinkendroid/blinkendroid-utils/nyan/bbmz/"
						+ name + ".info"));
		out.writeObject(blm.header);
		out.flush();
		out.close();
		BMLConverter.compress(
				"/Users/ben/workspace/private/blinkendroid/blinkendroid-utils/nyan/bbm/"
						+ name + ".bbm",
				"/Users/ben/workspace/private/blinkendroid/blinkendroid-utils/nyan/bbmz/"
						+ name + ".bbmz");
		System.out.println("Finished");
	}
}
