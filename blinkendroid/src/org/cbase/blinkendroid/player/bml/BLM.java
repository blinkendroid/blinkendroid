package org.cbase.blinkendroid.player.bml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class BLM {
	public int width;
	public int height;
	Header header;
	public List<Frame> frames;

	public class Header{
		String title;
		String description;
		String creator;
		String author;
		String email;
		boolean loop;
		int duration;
		
		public Header(XmlPullParser parser) throws XmlPullParserException, IOException {
			int eventType = parser.next();
			String name = null;
			while (true){
	            switch (eventType){
	                case XmlPullParser.START_TAG:
	                	 name = parser.getName();
	                    if (name.equalsIgnoreCase(BMLParser.TITLE)){
	                    	parser.next();
	                    	title=parser.getText();
	                    	parser.next();
	                    }else  if (name.equalsIgnoreCase(BMLParser.DURATION)){
	                    	parser.next();
	                    	duration=Integer.parseInt(parser.getText());
	                    	parser.next();
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	 name = parser.getName();
	                    if(null!=name && name.equalsIgnoreCase(BMLParser.HEADER))
	                    	return;
	            }
	            eventType = parser.next();
	        }
		}
	}
	
	public class Frame{
		public int duration;
		public int matrix[][];
		
		public Frame(XmlPullParser parser) throws XmlPullParserException, IOException {
			matrix = new int[height][width];
			int row=0;
			duration=Integer.parseInt(parser.getAttributeValue(null, BMLParser.DURATION));
			Log.i("BMLParser", "parsed frame with duration "+duration);
			int eventType = parser.next();
        	String name = null;
			while (true){
	            switch (eventType){
	                case XmlPullParser.START_TAG:
	                	 name = parser.getName();
	                    if (name.equalsIgnoreCase(BMLParser.ROW)){
	                    	parser.next();
	                    	String rowS=parser.getText();
	                    	for (int i = 0; i < width; i++) {
								matrix[row][i]=Integer.parseInt(rowS.charAt(i)+"",16);
							}
	                    	parser.next();
	                    	row++;
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                	 name = parser.getName();
	                    if(null!=name && name.equalsIgnoreCase(BMLParser.FRAME))
	                    	return;
	            }
	            eventType = parser.next();
	        }
		}

	}
	
	public BLM(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		width=Integer.parseInt(parser.getAttributeValue(null, BMLParser.ATTRIBUTE_WIDTH));
		height=Integer.parseInt(parser.getAttributeValue(null, BMLParser.ATTRIBUTE_HEIGHT));
		frames=new ArrayList<Frame>();
		int eventType = parser.next();
		String name = null;
		while (eventType != XmlPullParser.END_TAG){
            switch (eventType){
                case XmlPullParser.START_TAG:
                    if (parser.getName().equalsIgnoreCase(BMLParser.HEADER)){
                        header=new Header(parser);
                    }else if (parser.getName().equalsIgnoreCase(BMLParser.FRAME)){
                        frames.add(new Frame(parser));
                    }
                    break;
                case XmlPullParser.END_TAG:
               	 name = parser.getName();
                   if(null!=name && name.equalsIgnoreCase(BMLParser.BLM))
                   	return;
            }
            eventType = parser.next();
        }
	}

}
