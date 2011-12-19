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
		String name = "schrift";
		BLM blm = new BLM();
		blm.header = new BLMHeader();
		blm.header.width = 30;
		blm.header.height = 48;
		blm.header.bits = 8;
		blm.header.color = true;
		blm.header.title = "ggd_record_schrift";//+name;
		blm.frames = new ArrayList<Frame>();
		addFrames(name,blm,192,100,true,false);
//		addFrames(name,blm,73,150,false,true);
		ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
				"gdd/bbm/" + name + ".bbm"));
		out.writeObject(blm);
		out.flush();
		out.close();
		// die infofiles für
		out = new ObjectOutputStream(new FileOutputStream("gdd/bbmz/" + name
				+ ".info"));
		out.writeObject(blm.header);
		out.flush();
		out.close();
		BMLConverter.compress("gdd/bbm/" + name + ".bbm", "gdd/bbmz/" + name
				+ ".bbmz");
	}

	private static void addFrames(String name, BLM blm, int size, int duration, boolean forward,boolean leadingZero) throws IOException {

		for(int i=forward?1:size;forward?i<=size:i>0;i+=forward?1:-1) {
			String si = Integer.toString(i);
			if(leadingZero)
			{
				if (i < 10)
					si = "00" + i;
				else if (i < 100)
					si = "0" + i;
			}
			BufferedImage image=null;
			try{
				 image = BMPDecoder.read(new File("gdd/"+name+"/"+name+" "+ si + ".bmp"));
			}catch(Exception e){
				System.out.println("failed"+e.getMessage());
				continue;
			}
			System.out.println(i);
			BLM.Frame f = new BLM.Frame();
			f.duration = duration;
			f.matrix = new byte[image.getHeight()][image.getWidth()];
			blm.frames.add(f);
			for (int y = 0; y < image.getHeight(); y++) {
				for (int x = 0; x < image.getWidth(); x++) {
					int[] rgb = new int[3];
					image.getRaster().getPixel(x, y, rgb);

					byte b = (byte) ((byte) ((rgb[0] / 32) << 5)
							+ (byte) ((rgb[1] / 32) << 2) + (byte) ((rgb[2] / 64)));
					f.matrix[y][x] = b;
				}
			}

		}
	}
}
