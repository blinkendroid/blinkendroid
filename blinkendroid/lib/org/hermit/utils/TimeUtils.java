
/**
 * utils: general utility functions.
 * <br>Copyright 2004-2009 Ian Cameron Smith
 *
 * <p>This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation (see COPYING).
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */

package org.hermit.utils;

import java.util.TimeZone;


/**
 * Utilities for handling and formatting dates and times.
 *
 * @author	Ian Cameron Smith
 */
public class TimeUtils
{

	// ************************************************************************ //
	// Public Constructors.
	// ************************************************************************ //

	/**
	 * Constructor -- disallow instantiation.
	 */
	private TimeUtils() { }


	// ************************************************************************ //
	// Formatting.
	// ************************************************************************ //
	   
	/**
	 * Format a time of day in ms as hours and minutes, in 24-hour clock format.
	 *
	 * @param	time		The time in ms to format.
	 * @return				The formatted value.
	 */
    public static String timeMsToHm(long time) {
    	// Convert to minutes.
    	time /= 60 * 1000;
		int hour = (int) time / 60;
		int min = (int) time % 60;

    	StringBuilder hm = new StringBuilder(5);
    	hm.setLength(5);
    	
		hm.setCharAt(0, (char) ('0' + hour / 10));
		hm.setCharAt(1, (char) ('0' + hour % 10));
		hm.setCharAt(2, ':');
		hm.setCharAt(3, (char) ('0' + min / 10));
		hm.setCharAt(4, (char) ('0' + min % 10));
    	
    	return hm.toString();
    }
    
    
	/**
	 * Format a time offset as hours, minutes and seconds, only including
	 * the relevant components.  If the offset is less than 60 sec, only
	 * the seconds will be included; if the offset is an exact number of
	 * hours, only hours; etc.
	 *
	 * @param	off			The time offset in ms to format.
	 * @return				The formatted value.
	 */
    public static String intervalMsToHmsShort(long off) {
    	if (off == 0)
    		return "";

    	String hms = "";

    	off /= 1000;
    	if (off >= 0) {
    		hms += "+";
    	} else {
    		hms += "-";
    		off = -off;
    	}

    	if (off >= 3600) {
    		hms += (off / 3600) + "h";
    		off = off % 3600;
    	}
    	if (off >= 60) {
    		hms += (off / 60) + "m";
    		off = off % 60;
    	}
    	if (off > 0)
    		hms += off + "s";

    	return hms;
    }
    
    
    /**
     * Produce a string describing the offset of the given time zone from
     * UTC, including the DST offset if there is one.
     * 
     * @param	zone			TimeZone whose offset we want.
     * @return					Formatted offset.
     */
    public static String formatOffset(TimeZone zone) {
    	String fmt = "";
    	
    	int base = zone.getRawOffset();
    	fmt += "UTC" + intervalMsToHmsShort(base);
    	
    	int dst = zone.getDSTSavings();
    	if (dst != 0)
        	fmt += " (UTC" + intervalMsToHmsShort(base + dst) + ")";
    	
    	return fmt;
    }

    
    /**
     * Produce a string describing the offset of the given time zone from
     * UTC, including the DST offset if there is one.
     * 
     * @param	zone			TimeZone whose offset we want.
     * @return					Formatted offset.
     */
    public static String formatOffsetFull(TimeZone zone) {
    	String fmt = zone.getID() + ": ";
    	
    	int base = zone.getRawOffset();
    	fmt += zone.getDisplayName(false, TimeZone.LONG) + "=UTC" + intervalMsToHmsShort(base);
    	
    	int dst = zone.getDSTSavings();
    	if (dst != 0)
    		fmt += " (" + zone.getDisplayName(true, TimeZone.LONG) + "=UTC" + intervalMsToHmsShort(base + dst) + ")";
   	
    	return fmt;
    }

}

