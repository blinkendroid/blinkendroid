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

public class Ratater {
	public static void main(String[] args) throws IOException {
		String name = "guinness";
		BLM blm = new BLM();
		blm.header = new BLMHeader();
		blm.header.width = 72;
		blm.header.height = 72;
		blm.header.bits = 8;
		blm.header.color = true;
		blm.header.title = "guinness";
		blm.frames = new ArrayList<Frame>();
		BufferedImage orig = BMPDecoder.read(new File("mwc/guinness_72.bmp"));
		int step = 3;
		int j=0;
		BLM.Frame f=null;
		for (int i = 0; i < 360; i+=step) {
			System.out.println(i+" w:"+orig.getWidth()+" h:"+orig.getHeight());
			f = new BLM.Frame();
			f.duration = 100;
			f.matrix = new byte[blm.header.height][blm.header.width];
			blm.frames.add(f);
			for (int y = 0; y < blm.header.height; y++) {
				for (int x = 0; x < blm.header.width; x++) {
					int[] rgb = new int[4];
					int tx=x-orig.getWidth()/2;
					int ty=y-orig.getHeight()/2;
					int rx =(int) Math.round(tx*Math.cos(Math.PI/180*i)-ty*Math.sin(Math.PI/180*i));
					int ry =(int) Math.round(ty*Math.cos(Math.PI/180*i)+tx*Math.sin(Math.PI/180*i));
					rx=rx+orig.getWidth()/2;
					ry=ry+orig.getHeight()/2;
					try{
						orig.getRaster().getPixel(rx, ry, rgb);
					}catch(Exception e){
						rgb= new int[]{255,255,255,255};
					}
					byte b = (byte) ((byte) ((rgb[0] / 32) << 5)
							+ (byte) ((rgb[1] / 32) << 2) + (byte) ((rgb[2] / 64)));
					f.matrix[y][x] = b;
				}
			}
			j=i;
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
