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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import org.cbase.blinkendroid.network.udp.BlinkendroidProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkendroidDataServerProtocol {

    private static final Logger logger = LoggerFactory.getLogger(BlinkendroidDataServerProtocol.class);

    protected BufferedOutputStream out;
    protected BufferedInputStream in;
    protected Socket socket;
    protected ReceiverThread receiver;
    DataServer dataServer;

    public BlinkendroidDataServerProtocol(final Socket socket, DataServer dataServer) throws IOException {
	this.out = new BufferedOutputStream(socket.getOutputStream());
	this.in = new BufferedInputStream(socket.getInputStream());
	this.socket = socket;
	this.dataServer = dataServer;
	receiver = new ReceiverThread();
	receiver.start();
    }

    protected void sendMovie() {
	try {
	    if (null == dataServer.getVideoName()) {
		writeLong(out, 0);
		logger.info("Play default video ");
	    } else {
		File movie = new File(dataServer.getVideoName());
		if (null != movie && movie.exists()) {
		    InputStream is = new FileInputStream(movie);
		    try {
			writeLong(out, movie.length());
			logger.info("try to read file with bytes " + movie.length());

			byte[] buffer = new byte[1024];
			// commented because: not referenced
			// int allLen = 0;
			int len;
			while ((len = is.read(buffer)) != -1) {
			    out.write(buffer, 0, len);
			    // allLen += len;
			}
			logger.info("send movie bytes " + movie.length());
			writeLong(out, movie.length());
		    } catch (IOException ioe) {
			logger.error("sending movie failed", ioe);
		    } finally {
			is.close();
			is = null;
		    }
		} else {
		    logger.error("movie not found" + dataServer.getVideoName());
		}
	    }
	} catch (IOException e) {
	    logger.error("sendMovie failed", e);
	} finally {
	    try {
		out.flush();
		out.close();
	    } catch (IOException e) {
		logger.error("sendMovie out.flush out.close failed", e);
	    }
	}
    }

    protected void sendImage() {
	try {
	    if (null == dataServer.getImageName()) {
		writeLong(out, 0);
		logger.info("Play default image ");
	    } else {
		File image = new File(dataServer.getImageName());
		if (null != image && image.exists()) {
		    InputStream is = new FileInputStream(image);
		    try {
			writeLong(out, image.length());
			logger.info("try to read file with bytes " + image.length());

			byte[] buffer = new byte[1024];
			// commented because: not referenced
			// int allLen = 0;
			int len;
			while ((len = is.read(buffer)) != -1) {
			    out.write(buffer, 0, len);
			    // allLen += len;
			}

			logger.info("send image bytes " + image.length());
			writeLong(out, image.length());
		    } catch (IOException ioe) {
			logger.error("sending movie failed", ioe);
		    } finally {
			is.close();
			is = null;
		    }
		} else {
		    logger.error("movie not found" + dataServer.getImageName());
		}
	    }
	} catch (IOException e) {
	    logger.error("sendImage failed", e);
	} finally {
	    try {
		out.flush();
		out.close();
	    } catch (IOException e) {
		logger.error("sendImage out.flush out.close failed", e);
	    }
	}
    }

    protected long readLong(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[8];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getLong();
    }

    protected int readInt(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[4];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getInt();
    }

    protected float readFloat(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[16];
	in.read(buffer);
	return ByteBuffer.wrap(buffer).getFloat();
    }

    protected void writeInt(BufferedOutputStream out, int i) throws IOException {
	byte[] buffer = new byte[4];
	ByteBuffer.wrap(buffer).putInt(i);
	out.write(buffer);
    }

    protected void writeFloat(BufferedOutputStream out, float f) throws IOException {
	byte[] buffer = new byte[16];
	ByteBuffer.wrap(buffer).putFloat(f);
	out.write(buffer);
    }

    protected void writeLong(BufferedOutputStream out, long l) throws IOException {
	byte[] buffer = new byte[8];
	ByteBuffer.wrap(buffer).putLong(l);
	out.write(buffer);
    }

    // Inner classes:
    /**
     * A thread that receives information
     */
    class ReceiverThread extends Thread {

	volatile private boolean running = true;

	@Override
	public void run() {
	    running = true;
	    int requiredCommand = 0; // we need a to send it
	    try {
		byte[] buffer = new byte[64];
		if (in.read(buffer) != -1) {
		    // if (!running) { // fast exit
		    // break;
		    // }
		    requiredCommand = ByteBuffer.wrap(buffer).getInt();
		    if (requiredCommand == BlinkendroidProtocol.OPTION_PLAY_TYPE_MOVIE) {
			sendMovie();
		    } else if (requiredCommand == BlinkendroidProtocol.OPTION_PLAY_TYPE_IMAGE) {
			sendImage();
		    }
		}
	    } catch (SocketException e) {
		logger.error("Socket closed", e);
	    } catch (IOException e) {
		logger.error("ReceiverThread fucked", e);
	    } finally {
		running = false;
		try {
		    in.close();
		    socket.close();
		} catch (IOException e) {
		    logger.error("ReceiverThread in.close socket.close failed", e);
		}
	    }
	    logger.info("ReceiverThread ended!!!!!!!");
	}

	public void shutdown() {
	    logger.info(" ReceiverThread shutdown start");
	    running = false;
	    interrupt();
	    logger.info(" ReceiverThread shutdown interrupted");
	    try {
		join();
	    } catch (InterruptedException e) {
		// swallow, this is expected when being interrupted
	    }
	    logger.info("ReceiverThread shutdown joined & end");
	}
    }
}