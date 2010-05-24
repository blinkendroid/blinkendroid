package org.cbase.blinkendroid.player.bml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

public class BBMZParser {

    public BLM parseBBMZ(InputStream openRawResource) {
	long time = System.currentTimeMillis();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	uncompress(openRawResource, baos);
	try {
	    ObjectInputStream objIn = new ObjectInputStream(
		    new ByteArrayInputStream(baos.toByteArray()));
	    Object o = objIn.readObject();
	    System.out.println("decompression and parsing time :"
		    + (System.currentTimeMillis() - time));
	    if (o instanceof BLM) {
		return (BLM) o;
	    } else {
		System.out.println("not valid bbmz");
	    }
	} catch (Exception e) {
	    System.out.println("not valid bbmz");
	    e.printStackTrace();
	}

	return null;
    }

    public static void uncompress(InputStream fis, OutputStream fos) {
	try {
	    ZipInputStream zis = new ZipInputStream(fis);
	    zis.getNextEntry();
	    final int BUFSIZ = 4096;
	    byte inbuf[] = new byte[BUFSIZ];
	    int length=0;
	    int n;
	    while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1){
		fos.write(inbuf, 0, n);
		length+=n;
	    }
	    System.out.println("BBMZParser read bytes "+length);
	    // zis.close();
	    // fis = null;
	    // fos.close();
	    // fos = null;
	} catch (IOException e) {
	    System.err.println(e);
	} finally {
	    try {
		// if (fis != null)
		// fis.close();
		if (fos != null)
		    fos.close();
	    } catch (IOException e) {
	    }
	}
    }
}
