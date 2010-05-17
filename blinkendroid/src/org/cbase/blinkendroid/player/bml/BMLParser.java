package org.cbase.blinkendroid.player.bml;

import java.io.Reader;

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

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class BMLParser {

    static final String BLM = "blm";

    public BLM parseBLM(final Reader reader) {

	XmlPullParser parser = Xml.newPullParser();
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
			blm = new BLM(parser);
		    }
		    break;
		case XmlPullParser.END_TAG:
		    break;
		}
		eventType = parser.next();
	    }
	    Log.i("BMLParser", "parsed BML with rows" + blm.frames.size());
	    return blm;
	} catch (Exception e) {
	    Log.e("BMLParser", "could not parse", e);
	    return null;
	}
    }
}
