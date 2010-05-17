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

package org.cbase.blinkendroid.player.bml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BLM {
    public int width;
    public int height;
    private Header header;
    public List<Frame> frames;

    private static final String ATTRIBUTE_WIDTH = "width";
    private static final String ATTRIBUTE_HEIGHT = "height";
    private static final String HEADER = "header";
    private static final String FRAME = "frame";
    private static final String ROW = "row";
    private static final String TITLE = "title";
    private static final String DURATION = "duration";

    public class Header {
	String title;
	String description;
	String creator;
	String author;
	String email;
	boolean loop;
	int duration;

	public Header(XmlPullParser parser) throws XmlPullParserException,
		IOException {
	    int eventType = parser.next();
	    String name = null;
	    while (true) {
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    name = parser.getName();
		    if (name.equalsIgnoreCase(TITLE)) {
			parser.next();
			title = parser.getText();
			parser.next();
		    } else if (name.equalsIgnoreCase(DURATION)) {
			parser.next();
			duration = Integer.parseInt(parser.getText());
			parser.next();
		    }
		    break;
		case XmlPullParser.END_TAG:
		    name = parser.getName();
		    if (null != name && name.equalsIgnoreCase(HEADER))
			return;
		}
		eventType = parser.next();
	    }
	}
    }

    public class Frame {
	public int duration;
	public int matrix[][];

	public Frame(XmlPullParser parser) throws XmlPullParserException,
		IOException {
	    matrix = new int[height][width];
	    int row = 0;
	    duration = Integer.parseInt(parser
		    .getAttributeValue(null, DURATION));
	    // Log.i("BMLParser", "parsed frame with duration "+duration);
	    int eventType = parser.next();
	    String name = null;
	    while (true) {
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    name = parser.getName();
		    if (name.equalsIgnoreCase(ROW)) {
			parser.next();
			String rowS = parser.getText();
			for (int i = 0; i < width; i++) {
			    matrix[row][i] = Integer.parseInt(rowS.charAt(i)
				    + "", 16);
			}
			parser.next();
			row++;
		    }
		    break;
		case XmlPullParser.END_TAG:
		    name = parser.getName();
		    if (null != name && name.equalsIgnoreCase(FRAME))
			return;
		}
		eventType = parser.next();
	    }
	}

    }

    public BLM(XmlPullParser parser) throws XmlPullParserException, IOException {

	width = Integer.parseInt(parser
		.getAttributeValue(null, ATTRIBUTE_WIDTH));
	height = Integer.parseInt(parser.getAttributeValue(null,
		ATTRIBUTE_HEIGHT));
	frames = new ArrayList<Frame>();
	int eventType = parser.next();
	String name = null;
	while (eventType != XmlPullParser.END_TAG) {
	    switch (eventType) {
	    case XmlPullParser.START_TAG:
		if (parser.getName().equalsIgnoreCase(HEADER)) {
		    header = new Header(parser);
		} else if (parser.getName().equalsIgnoreCase(FRAME)) {
		    frames.add(new Frame(parser));
		}
		break;
	    case XmlPullParser.END_TAG:
		name = parser.getName();
		if (null != name && name.equalsIgnoreCase(BMLParser.BLM))
		    return;
	    }
	    eventType = parser.next();
	}
    }

}
