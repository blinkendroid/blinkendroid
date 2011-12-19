package org.cbase.blinkendroid.player.bml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import org.cbase.blinkendroid.player.bml.BLM.Frame;

public class GenerateRandomSnakesWhite {
	public static void main(String[] args) throws IOException {
		for (int r = 4; r < 18; r++) {

			String name = "gdd_snake_random_bw" + r;
			Random random = new Random(System.currentTimeMillis());
			BLM blm = new BLM();
			blm.header = new BLMHeader();
			blm.header.width = r;
			blm.header.height = r;
			blm.header.bits = 8;
			blm.header.color = false;
			blm.header.title = "gdd snake bw" + r;
			blm.frames = new ArrayList<Frame>();
			for (int i = 1; i < r * r; i++) {
				BLM.Frame f = new BLM.Frame();
				f.duration = 200;
				f.matrix = new byte[blm.header.height][blm.header.width];
				blm.frames.add(f);
				for (int y = 0; y < blm.header.height; y++) {
					if (y % 2 == 0){
						for (int x = 0; x < blm.header.width; x++) {
							int endY = i / r;
							int endX = i % r;
							if (y < endY)
								f.matrix[y][x] = (byte) 0;
							else if (y == endY && x < endX)
								f.matrix[y][x] = (byte) 0;
							else
								f.matrix[y][x] = (byte) (Math.round((255.0/(r*r))*(y*x))+r*r%255);
						}
					}else{
						for (int x = blm.header.width-1; x >= 0; x--) {
							int endY = i / r;
							int endX = r-(i % r);
							if (y < endY)
								f.matrix[y][x] = (byte) 0;
							else if (y == endY && x > endX)
								f.matrix[y][x] = (byte) 0;
							else
								f.matrix[y][x] =  (byte) (Math.round((255.0/(r*r))*(y*x))+r*r%255);
						}
					}
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
