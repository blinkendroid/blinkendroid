package org.cbase.blinkendroid.player.bml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BBMZParser {
    private static final Logger logger = LoggerFactory.getLogger(BBMZParser.class);

    public BLM parseBBMZ(ByteBuffer input, long length) {
	return parseBBMZ(new ByteArrayInputStream(input.array()), length);
    }

    public BLM parseBBMZ(InputStream openRawResource, long length) {
	long time = System.currentTimeMillis();

	// TODO Save os to sdcard/blinkendroid
	ByteArrayOutputStream os = new ByteArrayOutputStream();
	byte inbuf[] = new byte[1];
	// what is n?
	int n;
	try {
	    while ((n = openRawResource.read(inbuf, 0, 1)) != -1) {
		os.write(inbuf, 0, n);
		length -= n;
		if (length == 0) {
		    break;
		}
	    }
//	    Looper.prepare();
//	    new SaveToSDCard().execute(os);
	} catch (Exception e) {
	    logger.error("invalid bbmz", e);
	}


	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	System.gc();
	uncompress(new ByteArrayInputStream(os.toByteArray()), baos);
        try {
            os.close();
        } catch (IOException e1) {
            logger.error("parseBBMZ os.close()", e1);
        }
        os = null;
	try {
	    ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
	    Object receivedObject = objIn.readObject();
	    logger.info("decompression and parsing time :" + (System.currentTimeMillis() - time));
	    baos.close();
	    baos = null;
	    if (receivedObject instanceof BLM) {
		return (BLM) receivedObject;
	    } else {
		logger.error("invalid bbmz");
	    }
	} catch (Exception e) {
	    logger.error("invalid bbmz", e);
	}
	baos = null;
	return null;
    }

    public static void uncompress(InputStream fis, OutputStream fos) {
	try {
	    ZipInputStream zis = new ZipInputStream(fis);
	    zis.getNextEntry();
	    final int BUFSIZ = 4096;
	    byte inbuf[] = new byte[BUFSIZ];
	    int length = 0;
	    // what is n?
	    int n;
	    while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
		fos.write(inbuf, 0, n);
		length += n;
	    }
	    logger.info("BBMZParser read bytes " + length);
	    // zis.close();
	    // fis = null;
	    // fos.close();
	    // fos = null;
	} catch (IOException e) {
	    logger.error("invalid bbmz", e);
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

//    /**
//     * Saves the received blinkendroid video to the SD-Card.
//     */
//    private class SaveToSDCard extends AsyncTask<ByteArrayOutputStream, Void, Boolean> {
//
//	private BlinkendroidApp app;
//	private boolean mExternalStorageAvailable, mExternalStorageWriteable;
//
//	public SaveToSDCard() {
//	    app = BlinkendroidApp.getApp();
//	    String state = Environment.getExternalStorageState();
//
//	    if (Environment.MEDIA_MOUNTED.equals(state)) {
//		// We can read and write the media
//		mExternalStorageAvailable = mExternalStorageWriteable = true;
//	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//		// We can only read the media
//		mExternalStorageAvailable = true;
//		mExternalStorageWriteable = false;
//	    } else {
//		// Something else is wrong. It may be one of many other states,
//		// but all we need
//		// to know is we can neither read nor write
//		mExternalStorageAvailable = mExternalStorageWriteable = false;
//	    }
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.AsyncTask#doInBackground(Params[])
//	 */
//	@Override
//	protected Boolean doInBackground(ByteArrayOutputStream... params) {
//	    if (mExternalStorageAvailable && mExternalStorageWriteable) {
//		String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "blinkendroid";
//		File dir = new File(path);
//		if (!dir.exists()) {
//		    dir.mkdirs();
//		}
//		for (ByteArrayOutputStream bos : params) {
//		    FileOutputStream fos;
//		    try {
//			File file = new File(dir, System.currentTimeMillis() + ".bbmz");
//			if (!file.exists()) {
//			    file.createNewFile();
//			}
//
//			fos = app.openFileOutput(file.toString(), Context.MODE_WORLD_READABLE);
//
//			fos.write(bos.toByteArray());
//			fos.close();
//
//			bos.close();
//			bos = null;
//		    } catch (FileNotFoundException fnfe) {
//			// TODO Auto-generated catch block
//			fnfe.printStackTrace();
//		    } catch (IOException ioe) {
//			// TODO Auto-generated catch block
//			ioe.printStackTrace();
//		    }
//		}
//	    }
//	    return null;
//	}
//
//    }
}
