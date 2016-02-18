package org.cbase.blinkendroid.player.bml;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.cbase.blinkendroid.Constants;

import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BLMManager {
    private List<BLMHeader> blmHeader = new ArrayList<BLMHeader>();
    BLMManagerListener listener;

    public interface BLMManagerListener {
	public void moviesReady();
    }

    public void readMovies(final BLMManagerListener listener) {
	this.listener = listener;
	new Thread() {
	    @Override
	    public void run() {
		File blinkendroidDir = new File(Environment
			.getExternalStorageDirectory().getPath()
			+ File.separator + "blinkendroid");
		if (!blinkendroidDir.exists()) {
		    Log.d(Constants.LOG_TAG, "/blinkendroid does not exist");
		    return;
		}
		File[] files = blinkendroidDir.listFiles();
		if (null != files) {
		    Log.d(Constants.LOG_TAG, "found files " + files.length);
		    for (int i = 0; i < files.length; i++) {
			if (!files[i].getName().endsWith(".info"))
			    continue;
			BLMHeader header = getBLMHeader(files[i]);
			if (null != header) {
			    header.filename = files[i]
				    .getAbsolutePath()
				    .substring(
					    0,
					    files[i].getAbsolutePath().length() - 5)
				    + ".bbmz";
			    blmHeader.add(header);
			}
		    }
		    listener.moviesReady();
		}
	    }
	}.start();

    }

    public void fillArrayAdapter(ArrayAdapter<String> adapter) {
	for (BLMHeader header : blmHeader) {
	    String title = header.title + "(" + header.width + "*"
		    + header.height + ")";
	    if (null == header.title)
		title = header.filename.substring(20) + "(" + header.width
			+ "*" + header.height + ")";
	    Log.d(Constants.LOG_TAG, "added " + title);
	    adapter.add(title);
	}
    }

    private BLMHeader getBLMHeader(File f) {
	try {
	    ObjectInputStream objIn = new ObjectInputStream(
		    new FileInputStream(f));
	    Object o = objIn.readObject();
	    if (o instanceof BLMHeader) {
		return (BLMHeader) o;
	    }
	} catch (Exception e) {
	    Log.e(Constants.LOG_TAG, "could not get BMLHeader", e);
	}
	return null;
    }

    public BLMHeader getBLMHeader(int pos) {
	if (null != blmHeader.get(pos))
	    return blmHeader.get(pos);
	return null;
    }

    public String getFilename(int arg2) {
	// TODO Auto-generated method stub
	return null;
    }
}
