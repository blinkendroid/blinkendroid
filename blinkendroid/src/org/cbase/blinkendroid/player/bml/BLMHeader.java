package org.cbase.blinkendroid.player.bml;

import java.io.Serializable;

public class BLMHeader implements Serializable{
    public int width;
    public int height;
    public int bits;
    public String title;
    public String description;
    public String creator;
    public String author;
    public String email;
    public boolean loop;
    public transient String filename;

    public BLMHeader() {
    }
}