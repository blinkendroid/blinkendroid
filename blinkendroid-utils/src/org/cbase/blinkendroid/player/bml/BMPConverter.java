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

public class BMPConverter {
    public static void main(String[] args) throws IOException {
	BLM blm = new BLM();
	blm.header = new BLMHeader();
	blm.header.width = 32;
	blm.header.height = 32;
	blm.header.bits = 6;
	blm.header.color = true;
	blm.header.title = "blinkendroid final3";
	blm.frames = new ArrayList<Frame>();
	for (int i = 1; i < 224; i++) {
	    String si = Integer.toString(i);
	    if (i < 10)
		si = "00" + i;
	    else if (i < 100)
		si = "0" + i;
	    BufferedImage image = BMPDecoder.read(new File(
		    "bmp/filmloop03/final01 " + si + ".bmp"));
	    // System.out.print(i+" w:"+image.getWidth()+" h:"+image.getHeight());

	    Frame f = new Frame();
	    f.duration = 150;
	    f.matrix = new byte[image.getWidth()][image.getWidth()];
	    blm.frames.add(f);
	    for (int y = 0; y < image.getHeight(); y++) {
		for (int x = 0; x < image.getWidth(); x++) {

		    // int pff = x + i;
		    // if (pff >= image.getWidth())
		    // pff -= image.getWidth();
		    int[] rgb = new int[3];
		    image.getRaster().getPixel(x, y, rgb);

		    byte b = (byte) ((byte) ((rgb[0] / 64) << 4)
			    + (byte) ((rgb[1] / 64) << 2) + (byte) ((rgb[2] / 64)));
		    f.matrix[y][x] = b;
		    if (i == 59)
			System.out.print(rgb[0] + "," + rgb[1] + "," + rgb[2]
				+ ":" + f.matrix[y][x] + ";");
		}
		if (i == 59)
		    System.out.println("");
	    }

	}
	ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
		"bbm/blinkendroid7.bbm"));
	out.writeObject(blm);
	out.flush();
	out.close();
	// die infofiles für den server
	out = new ObjectOutputStream(new FileOutputStream(
		"bbmz/blinkendroid7.info"));
	out.writeObject(blm.header);
	out.flush();
	out.close();
	BMLConverter.compress("bbm/blinkendroid7.bbm",
		"bbmz/blinkendroid7.bbmz");
    }
}
