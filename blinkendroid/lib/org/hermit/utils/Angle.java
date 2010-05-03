
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


import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import java.text.NumberFormat;


/**
 * Utilities for handling and formatting angles, including latitudes
 * and longitudes.
 *
 * @author	Ian Cameron Smith
 */
public class Angle
{

	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

	/**
	 * Half pi; a quarter circle in radians; same as 90 degrees.
	 */
	public static final double HALFPI = PI / 2;


	/**
	 * Two times pi; a circle in radians; same as 360 degrees.
	 */
	public static final double TWOPI = PI * 2;


	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create an Angle from an angle given in radians.
	 * 
	 * @param	radians		Source angle in radians.
	 */
	public Angle(double radians) {
		angleR = radians;
	}

	
	// ******************************************************************** //
	// Accessors and Converters.
	// ******************************************************************** //

	/**
	 * Create a Angle from an angle given in degrees.
	 * 
	 * @param	degrees		Source angle in degrees.
     * @return              The corresponding Angle.
	 */
	public static Angle fromDegrees(double degrees) {
		return new Angle(toRadians(degrees));
	}


	/**
	 * Create a Angle from an angle given in degrees, minutes
	 * and seconds.
	 * 
	 * <p>If any of the parameters is negative, the result is negative.
	 * 
	 * @param	d		Whole degrees.
	 * @param	m		Minutes.
	 * @param	s		Seconds.
	 * @return          The corresponding Angle.
	 */
	public static Angle fromDegrees(int d, int m, double s) {
		boolean neg = d < 0 || m < 0 || s < 0;
		double df = ((abs(s) / 60.0 + abs(m)) / 60.0 + abs(d));
		return new Angle(toRadians(neg ? -df : df));
	}
	

	/**
	 * Create a Angle from a right ascension given in hours, minutes
	 * and seconds.
	 * 
	 * <p>If any of the parameters is negative, the result is negative
	 * (though they really shouldn't be).
	 * 
	 * @param	rh		Hours of right ascension.
	 * @param	rm		Minutes of right ascension.
	 * @param	rs		Seconds of right ascension.
     * @return          The corresponding Angle.
	 */
	public static Angle fromRightAscension(int rh, int rm, double rs) {
		boolean neg = rh < 0 || rm < 0 || rs < 0;
		double ra = ((abs(rs) / 60.0 + abs(rm)) / 60.0 + abs(rh)) * 15.0;
		return new Angle(toRadians(neg ? -ra : ra));
	}
	

	/**
	 * Get the angle in radians.
	 *
	 * @return				The angle in radians.
	 */
	public final double getRadians() {
		return angleR;
	}


	/**
	 * Get the azimuth in degrees.
	 *
	 * @return				The azimuth in degrees, clockwise from north.
	 * 						This will be in the range 0 <= degrees < 360.0.
	 */
	public final double getDegrees() {
		return toDegrees(angleR);
	}


	// ******************************************************************** //
	// Azimuth Arithmetic.
	// ******************************************************************** //

	/**
	 * Calculate the azimuth which is the given angular offset from this one.
	 * 
	 * @param	radians		Offset to add to this Azimuth, in radians;
	 * 						positive is clockwise from north, may be
	 * 						negative.
	 * @return				Azimuth which is equal to this Azimuth plus
	 * 						the given offset.  Overflow is taken care of.
	 */
	public Angle add(double radians) {
		return new Angle(angleR + radians);
	}


    // ******************************************************************** //
    // Conversions.
    // ******************************************************************** //

	/**
	 * Return the given value mod PI, with negative values made positive --
	 * in other words, the value put into the range [0 .. PI).
	 * 
	 * @param	v			Input value.
	 * @return				v % PI, plus PI if negative.
	 */
	public static final double modPi(double v) {
		v %= PI;
		return v < 0 ? v + PI : v;
	}
	
	
	/**
	 * Return the given value mod 2*PI, with negative values made positive --
	 * in other words, the value put into the range [0 .. TWOPI).
	 * 
	 * @param	v			Input value.
	 * @return				v % TWOPI, plus TWOPI if negative.
	 */
	public static final double modTwoPi(double v) {
		v %= TWOPI;
		return v < 0 ? v + TWOPI : v;
	}
	

    // ******************************************************************** //
    // Formatting.
    // ******************************************************************** //

    /**
     * Format this azimuth for user display in degrees.
     *
     * @return              The formatted azimuth.
     */
    public String formatDeg() {
        return String.format("%d°", round(toDegrees(angleR)));
    }


    /**
     * Format this azimuth for user display in degrees and minutes.
     *
     * @return              The formatted azimuth.
     */
    public String formatDegMin() {
        return Angle.formatDegMin(toDegrees(angleR)) + '°';
    }


    /**
     * Format this azimuth for user display in degrees and minutes.
     *
     * @return              The formatted azimuth.
     */
    public String formatDegMinSec() {
        return Angle.formatDegMinSec(toDegrees(angleR)) + '°';
    }

 
    /**
     * Format this azimuth as a String.
     * 
     * @return          This azimuth as a string, in degrees.
     */
    @Override
    public String toString() {
        return formatDeg();
    }
    

	// ************************************************************************ //
	// Static Formatting Utilities.
	// ************************************************************************ //

	/**
	 * Format a floating-point value.
	 *
	 * @param	val			The value to format.
	 * @param	frac		Maximum number of digits after the point.
	 * @return				The formatted value.
	 */
	public static String formatFloat(double val, int frac) {
		floatFormat.setMaximumFractionDigits(frac);
		return floatFormat.format(val);
	}


	/**
	 * Format an angle as a bearing.
	 *
	 * @param	val			The value to format.
	 * @return				The formatted value.
	 */
	public static String formatBearing(double val) {
		return Math.round(val) + "°";
	}


	/**
	 * Format an angle for user display in degrees and minutes.
	 * Negative angles are formatted with a "-" sign.
	 *
	 * @param	angle		The angle to format.
	 * @return				The formatted angle.
	 */
	public static String formatDegMin(double angle) {
		return formatDegMin(angle, ' ', '-');
	}


	/**
	 * Format a latitude or longitude angle as a string in the format
	 * "W171° 15.165'".
	 *
	 * @param	angle		Angle to format.
	 * @param	pos			Sign character to use if positive.
	 * @param	neg			Sign character to use if negative.
	 * @return				The formatted angle.
	 */
	public static String formatDegMin(double angle, char pos, char neg) {
		StringBuilder sb = new StringBuilder(12);
		formatDegMin(angle, pos, neg, sb);
		return sb.toString();
	}


	/**
	 * Format a latitude or longitude angle as a string in the format
	 * "W171°15.165'".  Place the result in a supplied StringBuilder.
	 * 
	 * The StringBuilder will be set to the required length, 12.  For 
	 * best efficiency, leave it at that length.
	 * 
	 * @param	angle		Angle to format.
	 * @param	pos			Sign character to use if positive.
	 * @param	neg			Sign character to use if negative.
	 * @param	sb			StringBuilder to write the result into.
	 */
	public static void formatDegMin(double angle,
									char pos, char neg, StringBuilder sb)
	{
		if (sb.length() != 12)
			sb.setLength(12);
		
		if (angle < 0) {
			sb.setCharAt(0, neg);
			angle = -angle;
		} else
			sb.setCharAt(0, pos);
	
		int deg = (int) angle;
		int min = (int) (angle * 60.0 % 60.0);
		int frac = (int) (angle * 60000.0 % 1000.0);

		sb.setCharAt( 1, deg < 100 ? ' ' : (char) ('0' + deg / 100));
		sb.setCharAt( 2, deg < 10 ? ' ' : (char) ('0' + deg / 10 % 10));
		sb.setCharAt( 3, (char) ('0' + deg % 10));
		sb.setCharAt( 4, '°');
		sb.setCharAt( 5, (char) ('0' + min / 10));
		sb.setCharAt( 6, (char) ('0' + min % 10));
		sb.setCharAt( 7, '.');
		sb.setCharAt( 8, (char) ('0' + frac / 100));
		sb.setCharAt( 9, (char) ('0' + frac / 10 % 10));
		sb.setCharAt(10, (char) ('0' + frac % 10));
		sb.setCharAt(11, '\'');
	}
	

	/**
	 * Format an angle for user display in degrees and minutes.
	 *
	 * @param	angle		The angle to format.
	 * @return				The formatted angle.
	 */
	public static String formatDegMinSec(double angle) {
		return formatDegMinSec(angle, ' ', '-');
	}
	
	
	/**
	 * Format an angle for user display in degrees and minutes.
	 *
	 * @param	angle		The angle to format.
	 * @param	posSign		Sign to use for positive values; none if null.
	 * @param	negSign		Sign to use for negative values; none if null.
	 * @return				The formatted angle.
	 */
	public static String formatDegMinSec(double angle, char posSign, char negSign) {
		char sign = angle >= 0 ? posSign : negSign;
		angle = Math.abs(angle);

		int deg = (int) angle;
		angle = (angle - deg) * 60.0;
		int min = (int) angle;
		angle = (angle - min) * 60.0;
		double sec = angle;

		// Rounding errors?
		if (sec >= 60.0) {
			sec = 0;
			++min;
		}
		if (min >= 60) {
			min -= 60;
			++deg;
		}
		
		return String.format("%s%3d° %2d' %8.5f\"", sign, deg, min, sec);
		//return sign + deg + "° " + min + "' " + angleFormat.format(sec) + "\"";
	}


	/**
	 * Format a latitude and longitude for user display in degrees and
	 * minutes.
	 *
	 * @param	lat			The latitude.
	 * @param	lon			The longitude.
	 * @return				The formatted angle.
	 */
	public static String formatLatLon(double lat, double lon) {
		return formatDegMin(lat, 'N', 'S') + " " + formatDegMin(lon, 'E', 'W');
	}

	
	/**
	 * Format an angle for user display as a right ascension.
	 *
	 * @param	angle		The angle to format.
	 * @return				The formatted angle.
	 * TODO: units!
	 */
	public static String formatRightAsc(double angle) {
		if (angle < 0)
			angle += 360.0;
		double hours = angle / 15.0;
		
		int h = (int) hours;
		hours = (hours - h) * 60.0;
		int m = (int) hours;
		hours = (hours - m) * 60.0;
		double s = hours;

		// Rounding errors?
		if (s >= 60.0) {
			s = 0;
			++m;
		}
		if (m >= 60) {
			m -= 60;
			++h;
		}
		if (h >= 24) {
			h -= 24;
		}
		
		return String.format("%02dh %02d' %08.5f\"", h, m, s);
	}


	// ************************************************************************ //
	// Class Data.
	// ************************************************************************ //

	// Number formatter for integer values.
	private static NumberFormat intFormat = null;
	static {
		// Set up the number formatter for integer values.
		intFormat = NumberFormat.getInstance();
		intFormat.setMinimumIntegerDigits(3);
		intFormat.setMaximumIntegerDigits(3);
		intFormat.setMaximumFractionDigits(0);
	}

	// Number formatter for floating-point values.
	private static NumberFormat floatFormat = null;
	static {
		// Set up the number formatter for floating-point values.
		floatFormat = NumberFormat.getInstance();
		floatFormat.setMinimumFractionDigits(0);
		floatFormat.setMaximumFractionDigits(7);
	}
	
	// Number formatter for formatAngle.
	private static NumberFormat angleFormat = null;
	static {
		// Set up the number formatter for angleFormat.
		angleFormat = NumberFormat.getInstance();
		angleFormat.setMinimumFractionDigits(0);
		angleFormat.setMaximumFractionDigits(3);
	}
    
	
	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //

	/**
	 * The azimuth, in radians, clockwise from north.
	 */
	private double angleR;

}

