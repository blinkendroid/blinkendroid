package org.cbase.blinkendroid.player.bml;

import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.cbase.blinkendroid.player.bml.BLM.Frame;

import com.java2s.code.java.GifDecoder;

public class Bavarian {
	public static void main(String[] args) throws IOException {

		GifDecoder decoder = new GifDecoder();
		decoder.read("file:poptart1red1.gif");
		Dimension size = decoder.getFrameSize();
		
		String name = "nyan";
		
		BLM blm = new BLM();
		blm.header = new BLMHeader();
		blm.header.width = size.width;
		blm.header.height = size.height;
		blm.header.bits = 8;
		blm.header.color = true;
		blm.header.title = "nyan cat all the way";
		blm.frames = new ArrayList<Frame>();
		for (int i = 1; i < decoder.getFrameCount(); i++) {
			BLM.Frame f = new BLM.Frame();
			f.duration = 200;
			f.matrix = new byte[blm.header.height][blm.header.width];
			blm.frames.add(f);
			for (int y = 0; y < blm.header.height; y++) {

				for (int x = 0; x < blm.header.width; x++) {
					if ((x + y + i) % 2 == 0)
						f.matrix[y][x] = (byte) 255;
					else
						f.matrix[y][x] = (byte) 3;

				}

			}
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
					"gdd/bbm/" + name + ".bbm"));
			out.writeObject(blm);
			out.flush();
			out.close();
			// die infofiles für
			out = new ObjectOutputStream(new FileOutputStream("gdd/bbmz/"
					+ name + ".info"));
			out.writeObject(blm.header);
			out.flush();
			out.close();
			BMLConverter.compress("gdd/bbm/" + name + ".bbm", "gdd/bbmz/"
					+ name + ".bbmz");
		}
	}
}
