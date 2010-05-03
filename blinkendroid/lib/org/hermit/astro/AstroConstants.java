
/**
 * astro: astronomical functions, utilities and data
 * <br>Copyright 2009 Ian Cameron Smith
 * 
 * <p>References:
 * <dl>
 * <dt>PAC</dt>
 * <dd>"Practical Astronomy with your Calculator", by Peter Duffett-Smith,
 * ISBN-10: 0521356997.</dd>
 * <dt>ESAA</dt>
 * <dd>"Explanatory Supplement to the Astronomical Almanac", edited
 * by Kenneth Seidelmann, ISBN-13: 978-1-891389-45-0.</dd>
 * <dt>AA</dt>
 * <dd>"Astronomical Algorithms", by Jean Meeus, ISBN-10: 0-943396-61-1.</dd>
 * </dl>
 * The primary reference for this version of the software is AA.
 * 
 * <p>Note that the formulae have been converted to work in radians, to
 * make it easier to work with java.lang.Math.
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


package org.hermit.astro;


import static java.lang.Math.PI;
import static java.lang.Math.toRadians;


/**
 * Definitions of useful global constants related to astronomical calculations.
 */
public interface AstroConstants
{

	/**
	 * Half pi; a quarter circle in radians; same as 90 degrees.
	 */
	public static final double HALFPI = PI / 2;


	/**
	 * Two times pi; a circle in radians; same as 360 degrees.
	 */
	public static final double TWOPI = PI * 2;

	
	/**
	 * The number of seconds in a day.
	 */
	public static final double SECS_PER_DAY = 60 * 60 * 24;

	
	/**
	 * The Julian date of the Unix/Java 1970 Jan 1.0 epoch relative to the
	 * astronomical epoch of 4713 BC.
	 */
	public static final double JD_UNIX = 2440587.5;
	

	/**
	 * The Julian date of the 1900 Jan 0.5 epoch (which is actually noon,
	 * 31 Dec 1989) relative to the astronomical epoch of 4713 BC.
	 */
	public static final double J1900 = 2415020.0;


	/**
	 * The Julian date of the 1990 Jan 0.0 epoch (which is actually 31 Dec
	 * 1989) relative to the astronomical epoch of 4713 BC.
	 */
	public static final double J1990 = 2447891.5;


	/**
	 * The Julian date of the 2000 Jan 1.5 epoch (which is actually 1 Jan
	 * 2000 at noon) relative to the astronomical epoch of 4713 BC.
	 */
	public static final double J2000 = 2451545.0;

	
	/**
	 * The obliquity of the ecliptic (angle between the ecliptic and the
	 * equator) at epoch 2000 Jan 1.5, in radians.
	 */
	public static final double ε_2000 = toRadians(23.4392911);
	
	
	/**
	 * The length of the sidereal year in mean solar days.
	 */
	public static final double SIDEREAL_RATIO = 1.00273790935;

	
	/**
	 * The length of the sidereal year in mean solar days.
	 */
	public static final double SIDEREAL_YEAR = 365.2564;

	
	/**
	 * The length of the tropical year in mean solar days.
	 */
	public static final double TROPICAL_YEAR = 365.242191;
	

	/**
	 * The vertical displacement of an object due to atmospheric
	 * refraction -- 24 arcmins.
	 */
	public static final double REFRACTION = toRadians(34.0 / 60.0);
	

	/**
	 * The constant of aberration, κ.
	 */
	public static final double ABERRATION = toRadians(20.49552 / 3600.0);
	

	/**
	 * The angle of the Sun below the horizon at the start / end
	 * of twilight, in radians.
	 */
	public static final double TWILIGHT = toRadians(18);
	

	/**
	 * One AU, in km.
	 */
	public static final double AU = 149597870;

}

