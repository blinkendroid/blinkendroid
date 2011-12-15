package org.cbase.blinkendroid.player.bml;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BLMManager {

    private List<BLMHeader> blmHeader;
    BLMHeader defaultMovie;
    
    public List<BLMHeader> getBlmHeader() {
	return blmHeader;
    }

    BLMManagerListener listener;
    private static final Logger logger = LoggerFactory.getLogger(BLMManager.class);

    public BLMManager() {
	blmHeader = new ArrayList<BLMHeader>();
	/* Adding the default movie */
	defaultMovie = new BLMHeader();
	defaultMovie.filename = null;
	defaultMovie.title = "Blinkendroid - Default";
	defaultMovie.height = 32;
	defaultMovie.width = 32;

	blmHeader.add(defaultMovie);
    }

    public interface BLMManagerListener {
	public void moviesReady();
    }

    public void readMovies(final BLMManagerListener listener, final String dir) {
	this.listener = listener;
	
	blmHeader.clear();
	blmHeader.add(defaultMovie);
	
	new Thread() {
	    @Override
	    public void run() {
		File blinkendroidDir = new File(dir);
		if (!blinkendroidDir.exists()) {
		    logger.info("/blinkendroid does not exist");
		    listener.moviesReady();
		    return;
		}
		File[] files = blinkendroidDir.listFiles();
		if (null != files) {
		    logger.info("found files " + files.length);
		    for (int i = 0; i < files.length; i++) {
			if (!files[i].getName().endsWith(".info"))
			    continue;
			BLMHeader header = getBLMHeader(files[i]);
			if (null != header) {
			    header.filename = files[i].getAbsolutePath().substring(0,
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

    private BLMHeader getBLMHeader(File f) {
	try {
	    ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(f));
	    Object receivedObject = objIn.readObject();
	    if (receivedObject instanceof BLMHeader) {
		return (BLMHeader) receivedObject;
	    }
	} catch (Exception e) {
	    logger.error("could not get BMLHeader", e);
	}
	return null;
    }

    public BLMHeader getBLMHeader(int pos) {
	if (pos < blmHeader.size() && null != blmHeader.get(pos))
	    return blmHeader.get(pos);
	return null;
    }
}
