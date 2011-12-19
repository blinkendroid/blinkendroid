package org.cbase.blinkendroid.player.bml;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import net.sf.image4j.codec.bmp.BMPDecoder;

import org.cbase.blinkendroid.player.bml.BLM.Frame;

public class ScrollerHorizontal {
	public static void main(String[] args) throws IOException {
		String name = "jamwithwip";
		BLM blm = new BLM();
		blm.header = new BLMHeader();
		blm.header.width = 30;
		blm.header.height = 48;
		blm.header.bits = 8;
		blm.header.color = true;
		blm.header.title = "jam with wip 48";
		blm.frames = new ArrayList<Frame>();
		BufferedImage orig = BMPDecoder.read(new File("mwc/jamwithwip_48.bmp"));
		int step = 3;
		int j=0;
		BLM.Frame f=null;
		for (int i = 0; i < orig.getWidth()-blm.header.width; i+=step) {
			System.out.println(i+" w:"+orig.getWidth()+" h:"+orig.getHeight());
			f = new BLM.Frame();
			f.duration = 200;
			f.matrix = new byte[blm.header.height][blm.header.width];
			blm.frames.add(f);
			for (int y = 0; y < blm.header.height; y++) {
				for (int x = 0; x < blm.header.width; x++) {
					int[] rgb = new int[4];
					orig.getRaster().getPixel(x+i, y, rgb);

					byte b = (byte) ((byte) ((rgb[0] / 32) << 5)
							+ (byte) ((rgb[1] / 32) << 2) + (byte) ((rgb[2] / 64)));
					f.matrix[y][x] = b;
				}
			}
			j=i;
		}
		f.duration = 500;
		for (int i = j; i >=0; i-=step) {
			System.out.println(i+" w:"+orig.getWidth()+" h:"+orig.getHeight());
			f = new BLM.Frame();
			f.duration = 200;
			f.matrix = new byte[blm.header.height][blm.header.width];
			blm.frames.add(f);
			for (int y = 0; y < blm.header.height; y++) {
				for (int x = 0; x < blm.header.width; x++) {
					int[] rgb = new int[4];
					orig.getRaster().getPixel(x+i, y, rgb);

					byte b = (byte) ((byte) ((rgb[0] / 32) << 5)
							+ (byte) ((rgb[1] / 32) << 2) + (byte) ((rgb[2] / 64)));
					f.matrix[y][x] = b;
				}
			}
		}
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
				"mwc/bbm/" + name + ".bbm"));
		out.writeObject(blm);
		out.flush();
		out.close();
		// die infofiles für
		out = new ObjectOutputStream(new FileOutputStream("mwc/bbmz/" + name
				+ ".info"));
		out.writeObject(blm.header);
		out.flush();
		out.close();
		BMLConverter.compress("mwc/bbm/" + name + ".bbm", "mwc/bbmz/" + name
				+ ".bbmz");
	}
}
