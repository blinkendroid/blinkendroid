package org.cbase.blinkendroid.player.bml;

import org.cbase.blinkendroid.Player;
import org.cbase.blinkendroid.R;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class BMLParser {
	static final String BLM= "blm";
	static final String ATTRIBUTE_WIDTH = "width";
	static final String ATTRIBUTE_HEIGHT = "height";
	static final String HEADER = "header";
	static final String FRAME= "frame";
	static final String ROW= "row";
	static final String TITLE = "title";
	static final String DURATION = "duration";
	Player player;
	public BMLParser(Player player) {
		this.player=player;
	}

	public BLM parseBLM(){
		XmlPullParser parser = Xml.newPullParser();
        try {
        	parser.setInput(player.getResources().openRawResource(R.raw.allyourbase),"utf-8");
        	 int eventType = parser.getEventType();
        	 BLM blm=null;
             while (eventType != XmlPullParser.END_DOCUMENT){
                 String name = null;
                 switch (eventType){
                     case XmlPullParser.START_DOCUMENT:
                         break;
                     case XmlPullParser.START_TAG:
                         name = parser.getName();
                         if (name.equalsIgnoreCase(BLM)){
                             blm=new BLM(parser);
                         }
                         break;
                     case XmlPullParser.END_TAG:
                         break;
                 }
                 eventType = parser.next();
             }
             Log.i("BMLParser", "parsed BML with rows"+blm.frames.size());
             return blm;
        } catch (Exception e) {
            Log.e("BMLParser", "could not parse",e);
            return null;
        }
    }
}
