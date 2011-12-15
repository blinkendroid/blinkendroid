package org.cbase.blinkendroid.player.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageManager {

    private List<ImageHeader> imageHeader;
    ImageHeader defaultImage;
    
    public List<ImageHeader> getImageHeader() {
	return imageHeader;
    }

    ImageManagerListener listener;
    private static final Logger logger = LoggerFactory.getLogger(ImageManager.class);
    private String dir;

    public ImageManager() {
	imageHeader = new ArrayList<ImageHeader>();
	/* Adding the default movie */
	defaultImage = new ImageHeader();
	defaultImage.filename = null;
	defaultImage.title = "Blinkendroid - Default";
	defaultImage.height = 0;
	defaultImage.width = 0;

	imageHeader.add(defaultImage);
    }

    public interface ImageManagerListener {
	public void imagesReady();
    }

    public void readImages(final ImageManagerListener listener, String dir) {
	this.listener = listener;
	this.dir = dir;
	
	imageHeader.clear();
	imageHeader.add(defaultImage);
	
	new Thread() {
	    @Override
	    public void run() {
		File blinkendroidDir = new File(ImageManager.this.dir);
		if (!blinkendroidDir.exists()) {
		    logger.error(ImageManager.this.dir + " dir does not exist");
		    listener.imagesReady();
		    return;
		}
		File[] files = blinkendroidDir.listFiles();
		if (null != files) {
		    logger.info("found files " + files.length);
		    for (int i = 0; i < files.length; i++) {
			if (!(files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpg")))
			    continue;
			ImageHeader header = getImageHeader(files[i]);
			if (null != header) {
			    header.filename = files[i].getAbsolutePath();
			    imageHeader.add(header);
			}
		    }
		    listener.imagesReady();
		}
	    }
	}.start();

    }

    private ImageHeader getImageHeader(File f) {
	try {
	    ImageHeader defaultImage = new ImageHeader();
	    defaultImage.filename = null;
	    defaultImage.title = f.getName();
	    defaultImage.height = 32;
	    defaultImage.width = 32;
	    return defaultImage;
	} catch (Exception e) {
	    logger.error("could not get ImageHeader", e);
	}
	return null;
    }

    public ImageHeader getImageHeader(int pos) {
	if (pos < imageHeader.size() && null != imageHeader.get(pos))
	    return imageHeader.get(pos);
	return null;
    }
}
