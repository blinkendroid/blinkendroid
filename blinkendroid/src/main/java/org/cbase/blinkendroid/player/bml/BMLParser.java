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
import java.io.Reader;
import java.util.ArrayList;

import org.cbase.blinkendroid.player.bml.BLM.Frame;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BMLParser {

    private static final String BLM = "blm";
    private static final String BLM_ATTR_WIDTH = "width";
    private static final String BLM_ATTR_HEIGHT = "height";
    private static final String BLM_ATTR_BITS = "bits";
    private static final String HEADER = "header";
    private static final String TITLE = "title";
    private static final String CREATOR = "creator";
    private static final String AUTHOR = "author";
    private static final String EMAIL = "email";
    private static final String DESCRIPTION = "description";
    private static final String FRAME = "frame";
    private static final String FRAME_ATTR_DURATION = "duration";
    private static final String ROW = "row";

    private XmlPullParser parser;

    public BMLParser(XmlPullParser parser) {
	this.parser = parser;
    }

    public BLM parseBLM(final Reader reader) {

	try {
	    parser.setInput(reader);
	    int eventType = parser.getEventType();
	    BLM blm = null;
	    while (eventType != XmlPullParser.END_DOCUMENT) {
		String name = null;
		switch (eventType) {
		case XmlPullParser.START_DOCUMENT:
		    break;
		case XmlPullParser.START_TAG:
		    name = parser.getName();
		    if (name.equalsIgnoreCase(BLM)) {
			blm = parseBLM(parser);
		    }
		    break;
		case XmlPullParser.END_TAG:
		    break;
		}
		eventType = parser.next();
	    }
	    System.out.println("parsed BML with rows" + blm.frames.size());
	    return blm;
	} catch (Exception x) {
	    throw new RuntimeException(x);
	}
    }

    private BLM parseBLM(final XmlPullParser parser)
	    throws XmlPullParserException, IOException {

	final BLM blm = new BLM();
	blm.header = new BLMHeader();
	blm.header.width = Integer.parseInt(parser.getAttributeValue(null,
		BLM_ATTR_WIDTH));
	blm.header.height = Integer.parseInt(parser.getAttributeValue(null,
		BLM_ATTR_HEIGHT));
	blm.header.bits = Integer.parseInt(parser.getAttributeValue(null,
		BLM_ATTR_BITS));
	blm.frames = new ArrayList<Frame>();
	int eventType = parser.next();
	String name = null;

	while (true) {
	    switch (eventType) {
	    case XmlPullParser.START_TAG:
		if (parser.getName().equalsIgnoreCase(HEADER)) {
		    blm.header = parseHeader(parser, blm.header);
		} else if (parser.getName().equalsIgnoreCase(FRAME)) {
		    blm.frames.add(parseFrame(parser, blm.header.width,
			    blm.header.height));
		}
		break;
	    case XmlPullParser.END_TAG:
		name = parser.getName();
		if (null != name && name.equalsIgnoreCase(BLM))
		    return blm;
	    }
	    eventType = parser.next();
	}
    }

    private BLMHeader parseHeader(final XmlPullParser parser, BLMHeader header)
	    throws XmlPullParserException, IOException {

	int eventType = parser.next();
	String name = null;
	while (true) {
	    switch (eventType) {
	    case XmlPullParser.START_TAG:
		name = parser.getName();
		if (name.equalsIgnoreCase(TITLE)) {
		    if (parser.next() == XmlPullParser.TEXT) {
			header.title = parser.getText();
			parser.next();
		    }
		} else if (name.equalsIgnoreCase(CREATOR)) {
		    if (parser.next() == XmlPullParser.TEXT) {
			header.creator = parser.getText();
			parser.next();
		    }
		} else if (name.equalsIgnoreCase(AUTHOR)) {
		    if (parser.next() == XmlPullParser.TEXT) {
			header.author = parser.getText();
			parser.next();
		    }
		} else if (name.equalsIgnoreCase(EMAIL)) {
		    if (parser.next() == XmlPullParser.TEXT) {
			header.email = parser.getText();
			parser.next();
		    }
		} else if (name.equalsIgnoreCase(DESCRIPTION)) {
		    if (parser.next() == XmlPullParser.TEXT) {
			header.description = parser.getText();
			parser.next();
		    }
		}
		break;
	    case XmlPullParser.END_TAG:
		name = parser.getName();
		if (null != name && name.equalsIgnoreCase(HEADER))
		    return header;
	    }
	    eventType = parser.next();
	}
    }

    private Frame parseFrame(final XmlPullParser parser, final int width,
	    final int height) throws XmlPullParserException, IOException {

	final Frame frame = new Frame();

	frame.matrix = new byte[height][width];
	int row = 0;
	frame.duration = Integer.parseInt(parser.getAttributeValue(null,
		FRAME_ATTR_DURATION));
	int eventType = parser.next();
	String name = null;
	while (true) {
	    switch (eventType) {
	    case XmlPullParser.START_TAG:
		name = parser.getName();
		if (name.equalsIgnoreCase(ROW)) {
		    parser.next();
		    final String rowS = parser.getText();
		    for (int i = 0; i < width; i++) {
			frame.matrix[row][i] = parsePixel(rowS.charAt(i));
		    }
		    parser.next();
		    row++;
		}
		break;
	    case XmlPullParser.END_TAG:
		name = parser.getName();
		if (null != name && name.equalsIgnoreCase(FRAME))
		    return frame;
	    }
	    eventType = parser.next();
	}
    }

    private byte parsePixel(final char c) {
	if (c >= '0' && c <= '9')
	    return (byte) (c - '0');
	else if (c >= 'a' && c <= 'f')
	    return (byte) (c - 'a' + 10);
	else if (c >= 'A' && c <= 'F')
	    return (byte) (c - 'A' + 10);
	else
	    throw new IllegalArgumentException("illegal pixel: " + c);
    }
}
