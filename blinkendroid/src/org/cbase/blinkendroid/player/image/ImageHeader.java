package org.cbase.blinkendroid.player.image;

import java.io.Serializable;

public class ImageHeader implements Serializable {

    private static final long serialVersionUID = 1L;
    public int width;
    public int height;
    public transient String filename;
    public String title;
    public ImageHeader() {
    }
    
    @Override
    public String toString() {
	return title;
    }
}