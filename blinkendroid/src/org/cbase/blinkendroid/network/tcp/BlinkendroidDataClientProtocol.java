/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbase.blinkendroid.network.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.network.udp.BlinkendroidProtocol;
import org.cbase.blinkendroid.player.bml.BBMZParser;
import org.cbase.blinkendroid.player.bml.BLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BlinkendroidDataClientProtocol {

    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidDataClientProtocol.class);
    public static final Integer PROTOCOL_PLAYER = 42;
    public static final Integer COMMAND_PLAYER_TIME = 23;
    public static final Integer COMMAND_CLIP = 17;
    public static final Integer COMMAND_PLAY = 11;
    public static final Integer COMMAND_INIT = 77;

    public static BLM receiveMovie(InetSocketAddress socketAddress) {
	BLM blm = null;
	Socket socket = new Socket();
	BufferedOutputStream out = null;
	BufferedInputStream in = null;
	try {
	    socket.connect(socketAddress);
	    // socket.setSoTimeout(1000);
	    out = new BufferedOutputStream(socket.getOutputStream());
	    in = new BufferedInputStream(socket.getInputStream());
	    writeInt(out, BlinkendroidProtocol.OPTION_PLAY_TYPE_MOVIE);
	    out.flush();
	    long length = readLong(in); // TODO checking racecondition with
	    // setSoTimeout
	    if (length == 0) {
		logger.info("Play default video ");
	    } else {
		BBMZParser parser = new BBMZParser();
		blm = parser.parseBBMZ(in, length);
	    }
	} catch (Exception e) {
	    logger.error("receive movie failed", e);
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    logger.error(" out.close() failed", e);
		}
		out = null;
	    }
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    logger.error(" in.close() failed", e);
		}
		in = null;
	    }
	    try {
		socket.close();
	    } catch (IOException e) {
		logger.error(" socket.close() failed", e);
	    }
	}
	return blm;
    }

    protected static long readLong(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[8];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getLong();
    }

    protected static int readInt(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[4];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getInt();
    }

    protected float readFloat(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[16];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getFloat();
    }

    protected static void writeInt(BufferedOutputStream out, int i) throws IOException {
	byte[] buffer = new byte[4];
	ByteBuffer.wrap(buffer).putInt(i);
	out.write(buffer);
    }

    protected static void writeFloat(BufferedOutputStream out, float f) throws IOException {
	byte[] buffer = new byte[16];
	ByteBuffer.wrap(buffer).putFloat(f);
	out.write(buffer);
    }

    protected static void writeLong(BufferedOutputStream out, long l) throws IOException {
	byte[] buffer = new byte[8];
	ByteBuffer.wrap(buffer).putLong(l);
	out.write(buffer);
    }

    public static Bitmap receiveImage(InetSocketAddress socketAddress) {
	Bitmap bmp = null;

	Socket socket = new Socket();
	BufferedOutputStream out = null;
	BufferedInputStream in = null;
	try {
	    socket = new Socket();
	    socket.connect(socketAddress);
	    // socket.setSoTimeout(1000);
	    out = new BufferedOutputStream(socket.getOutputStream());
	    in = new BufferedInputStream(socket.getInputStream());
	    writeInt(out, BlinkendroidProtocol.OPTION_PLAY_TYPE_IMAGE);
	    out.flush();
	    long length = readLong(in);
	    // setSoTimeout
	    if (length == 0) {
		logger.info("Play default image ");
	    } else {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte inbuf[] = new byte[1];
		// what is n?
		int n;
		try {
		    while ((n = in.read(inbuf, 0, 1)) != -1) {
			os.write(inbuf, 0, n);
			length -= n;
			if (length == 0)
			    break;
		    }
		} catch (Exception e) {
		    logger.error("invalid image", e);
		}
		try {
		    bmp = BitmapFactory.decodeByteArray(os.toByteArray(), 0, os.size());
		} catch (OutOfMemoryError oom) {
		    logger.error("fuck you!!", oom);
		}
		os.close();
		os = null;
	    }
	} catch (Exception e) {
	    logger.error("receiving image failed", e);
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    logger.error(" out.close() failed", e);
		}
		out = null;
	    }
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    logger.error(" in.close() failed", e);
		}
		in = null;
	    }
	    try {
		socket.close();
	    } catch (IOException e) {
		logger.error(" socket.close() failed", e);
	    }
	}
	return bmp;
    }
}