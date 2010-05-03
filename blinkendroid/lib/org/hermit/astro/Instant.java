
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


import static java.lang.Math.floor;


/**
 * A representation of a particular moment in time, with
 * methods to convert between the numerous time systems used in astronomy.
 *
 * @author	Ian Cameron Smith
 */
public class Instant
	implements AstroConstants
{

	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

	/**
	 * Create an instant from a Julian day number in UT.
	 * 
	 * @param	jd			The date to set as a fractional Julian day
	 *                      number relative to the astronomical epoch
	 *                      of 4713 BC UT.
	 */
	public Instant(double jd) {
		julianDateUt = jd;
		double[] ymd = julianToYmd(julianDateUt);
		deltaT = calculateDeltaT((int) ymd[0], ymd[1] + ymd[2] / 31.0);
		julianDateTd = julianDateUt + deltaT / SECS_PER_DAY;
	}


	/**
	 * Create an instant from a Java time in ms since 1 Jan 1970 UTC.  This
	 * is the Unix time * 1000.
	 * 
	 * @param	time		Java-style time in milliseconds since 1 Jan,
	 *						1970 UTC.
	 */
	public Instant(long time) {
		this(javaToJulian(time));
	}


	/**
	 * Create an instant from a date / time in UT.
	 * 
	 * @param	y			Year number; BC years are in astronomical form,
	 *                      so 1 BC = 0, 2 BC = -1, ...
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month, including the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 */
	public Instant(int y, int m, double d) {
		this(ymdToJulian(y, m, d));
	}
	

	/**
	 * Create an instant from a date / time in UT.
	 * 
	 * @param	y			Year number; BC years in astronomical form.
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month.
	 * @param	ho			Hour.
	 * @param	mn			Minute.
	 * @param	se			Second.
	 */
	public Instant(int y, int m, int d, int ho, int mn, int se) {
		this(ymdToJulian(y, m, d, ho, mn, se));
	}
	

	/**
	 * Create an instant from a Julian day in TD.
	 * 
	 * @param	td			The date to set as a Julian day number relative
	 *						to the astronomical epoch of 4713 BC UT, in
	 *						TD.
	 * @return				The new Instant.
	 */
	public static Instant fromTd(double td) {
		return new Instant(tdToUt(td));
	}
	

	/**
	 * Create an instant from a date / time in TD.  THis is mainly useful
	 * for running test cases, some of which specify time in TD.
	 * 
	 * @param	y			Year number; BC years in astronomical form.
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month, including the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 * @return				The new Instant.
	 */
	public static Instant fromTd(int y, int m, double d) {
		double td = ymdToJulian(y, m, d);
		return new Instant(tdToUt(td));
	}
	

    // ******************************************************************** //
	// Getters.
	// ******************************************************************** //

	/**
	 * Get the Julian day number in UT represented by this Instant.
	 * 
	 * @return				The Julian day number in UT.
	 */
	public double getUt() {
		return julianDateUt;
	}


	/**
	 * Convert this instant to year / month / day.
	 *
	 * From AA chapter 7.
	 * 
	 * @return				An array containing year, month, day, where
	 * 						the last value includes the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 */
	public double[] getYmd() {
		return julianToYmd(julianDateUt);
	}
	
	
	/**
	 * Get the Julian date in TD represented by this Instant.
	 * 
	 * <p>Note that we don't distinguish between TDT(TT) and TDB, which
	 * are always within 0.0017 seconds.
	 * 
	 * @return				The Julian date in TD, in days.
	 */
	public double getTd() {
		return julianDateTd;
	}


	/**
	 * Get the Greenwich mean sideral time represented by this Instant.
	 * 
	 * @return				The Greenwich mean sideral time in decimal hours.
	 */
	public double getGmst() {
		return utToGmst(julianDateUt);
	}


	/**
	 * Get the Java time in ms since 1 Jan 1970 UTC represented by this
	 * Instant.  This is the Unix time * 1000.
	 * 
	 * @return				The time in ms since 1 Jan 1970 UTC.
	 */
	public long getJavaTime() {
		return julianToJava(julianDateUt);
	}


	/**
	 * Get the ΔT value for this Instant.
	 * 
	 * @return				ΔT in seconds.
	 */
	public double getΔT() {
		return deltaT;
	}


    // ******************************************************************** //
	// Time System Conversions.
	// ******************************************************************** //

	/**
	 * Convert a given Julian date from UT to TD.
	 * 
	 * <p>Note that we don't distinguish between TDT(TT) and TDB, which
	 * are always within 0.0017 seconds.
	 *
	 * <p>From AA chapter 10.
	 * 
	 * @param	jd			The Julian date in UT.
	 * @return				The Julian date in TD, based on an
	 * 						approximation of ΔT.
	 */
	public static double utToTd(double jd) {
		double[] ymd = julianToYmd(jd);
		
		// ΔT is TDT - UT1 in seconds.
		double ΔT = calculateDeltaT((int) ymd[0], ymd[1] + ymd[2] / 31.0);
		return jd + ΔT / SECS_PER_DAY;
	}
	

	/**
	 * Convert a given Julian date from TD to UT.
	 * 
	 * <p>Note that we don't distinguish between TDT(TT) and TDB, which
	 * are always within 0.0017 seconds.
	 *
	 * <p>From AA chapter 10.
	 * 
	 * @param	jd			The Julian date in TD.
	 * @return				The Julian date in UT, based on an
	 * 						approximation of ΔT.
	 */
	public static double tdToUt(double jd) {
		double[] ymd = julianToYmd(jd);
		
		// ΔT is TDT - UT1 in seconds.
		double ΔT = calculateDeltaT((int) ymd[0], ymd[1] + ymd[2] / 31.0);
		return jd - ΔT / SECS_PER_DAY;
	}


    // ******************************************************************** //
	// Date and Time Utilities.
	// ******************************************************************** //

	/**
	 * Convert a given year / month / day to the Julian day relative
	 * to the astronomical epoch of 4713 BC.
	 *
	 * From AA chapter 7.
	 * 
	 * @param	y			Year number; BC years in astronomical form.
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month, including the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 * @return				The Julian date of the given Y/M/D, relative
	 *						to the astronomical epoch of 4713 BC.
	 */
	public static double ymdToJulian(int y, int m, double d) {
		if (m <= 2) {
			--y;
			m += 12;
		}
		
		int a = (int) floor((float) y / 100f);
		int b = 2 - a + (int) floor((float) a / 4f);
		
		return (int) floor(365.25 * (y + 4716)) +
			   (int) floor(30.6001 * (m + 1)) +
			   d + b - 1524.5;
	}


	/**
	 * Convert a given year / month / day to the Julian day relative
	 * to the astronomical epoch of 4713 BC.
	 *
	 * From AA chapter 7.
	 * 
	 * @param	y			Year number; BC years in astronomical form.
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month.
	 * @param	ho			Hour.
	 * @param	mn			Minute.
	 * @param	se			Second.
	 * @return				The Julian date of the given Y/M/D, relative
	 *						to the astronomical epoch of 4713 BC.
	 */
	public static double ymdToJulian(int y, int m, double d,
									 int ho, int mn, int se)
	{
		double df = ((double) se / 3600.0 + (double) mn / 60.0 + ho) / 24.0;
		return ymdToJulian(y, m, (double) d + df);
	}


	/**
	 * Convert a given Julian day relative to the astronomical epoch
	 * of 4713 BC to year / month / day.
	 *
	 * From AA chapter 7.
	 * 
	 * @param	jd			The Julian date to convert, relative
	 *						to the astronomical epoch of 4713 BC.
	 * @return				An array containing year, month, day, where
	 * 						the last value includes the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 */
	public static double[] julianToYmd(double jd) {
		jd += 0.5;
		double z = floor(jd);
		double f = jd - z;
		
		double a;
		if (z < 2299161)
			a = z;
		else {
			double α = floor((z - 1867216.25) / 36524.25);
			a = z + 1 + α - floor(α / 4.0);
		}
			
		double b = a + 1524;
		double c = floor((b - 122.1) / 365.25);
		double d = floor(365.25 * c);
		double e = floor((b - d) / 30.6001);
		
		double day = b - d - floor(30.6001 * e) + f;
		double month = e < 14 ? e - 1 : e - 13;
		double year = month > 2 ? c - 4716 : c - 4715;
		
		return new double[] { year, month, day };
	}


	/**
	 * Convert a date/time in Java notation -- milliseconds since 1 Jan,
	 * 1970 -- to the Julian day relative to the astronomical epoch
	 * of 4713 BC.
	 * 
	 * @param	time		Java-style time in milliseconds since 1 Jan,
	 *						1970.
	 * @return				The equivalent Julian date relative
	 *						to the astronomical epoch of 4713 BC.
	 */
	public static double javaToJulian(long time) {
		// Convert the time to days since 1 Jan 1970 (with a fractional
		// part).
		double days = (double) time / 1000.0 / 3600.0 / 24.0;
		
		// And add the julian date of the Unix epoch.
		return days + JD_UNIX;
	}


	/**
	 * Convert a Julian day relative to the astronomical epoch
	 * of 4713 BC to the date/time in Java notation -- milliseconds
	 * since 1 Jan, 1970.
	 * 
	 * @param	julian		A Julian date relative to the astronomical
	 * 						epoch of 4713 BC.
	 * @return				The equivalent Java-style time in milliseconds
	 * 						since 1 Jan, 1970.
	 */
	public static long julianToJava(double julian) {
		// Subtract the julian date of the Unix epoch.
		double ubase = julian - JD_UNIX;

		// Convert to a time in ms.
		return (long) (ubase * 1000 * 3600 * 24);
	}
	

	/**
	 * Convert a UT time to Greenwich mean sideral time.
	 *
	 * From AA chapter 12.
	 * 
	 * @param	jd			The Julian day number, including fraction.
	 * @return				The Greenwich mean sideral time in decimal hours.
	 */
	@Deprecated
	public static double utToGmst(double jd) {
		// Get The Julian date of midnight on the day of the given time.
		// This is a day number ending in .5.  And get the decimal hour
		// since midnight.
		double midnight = floor(jd + 0.5) - 0.5;
		double hour = (jd - midnight) * 24.0;
		
		double d_u = midnight - J2000;
		double T_u = d_u / 36525.0;
		double T_u2 = T_u * T_u;
		double T_u3 = T_u2 * T_u;
		
		// Calculate the GMST in hours.
		double T0 = 24110.54841 + 8640184.812866 * T_u +
											0.093104 * T_u2 - 6.2E-6 * T_u3;
		T0 /= 3600.0;
		
		double GMST = (hour * 1.00273790935 + T0) % 24;
		if (GMST < 0)
			GMST += 24;
		return GMST;
	}
	
	
	/**
	 * Convert a Greenwich sideral time to UT.
	 * 
	 * NOTE: the sidereal day is shorter than the solar day, so some
	 * sidereal times (about the first 4 minutes) occur twice in a given day.
	 * This routine assumes that the GST is in the first interval, if
	 * it is ambiguous.
	 *
	 * From PAC section 13, GST to UT.
	 * 
	 * @param	JD			The Julian date of midnight on the day of
	 * 						the given time.
	 * @param	GST			The Greenwich sideral time in decimal hours.
	 * @return				The UT time in decimal hours.
	 */
	@Deprecated
	public static double gstToUt(double JD, double GST) {
		double S = JD - J2000;
		double T = S / 36525.0;
		double T0 = 6.697374558 + 2400.051336 * T + 0.000025862 * T * T;
		T0 = T0 % 24;
		if (T0 < 0)
			T0 += 24;
		
		double UT = (GST - T0) % 24;
		if (UT < 0)
			UT += 24;
		UT *= 0.9972695663;
		
		return UT;
	}
	

	/**
	 * Convert a Greenwich sidereal time to local sideral time.
	 *
	 * From PAC section 14, LST.
	 * 
	 * @param	GST			The Greenwich sidereal time in decimal hours.
	 * @param	Λ			The observer's geographical longitude in radians;
	 * 						west longitudes negative, east positive.
	 * @return				The local sideral time in decimal hours.
	 */
	@Deprecated
	public static double gstToLst(double GST, double Λ) {
		double H = Math.toDegrees(Λ) / 15;
		double L = (GST + H) % 24;
		if (L < 0)
			L += 24;
		return L;
	}
	

	/**
	 * Convert a local sidereal time to Greenwich sideral time.
	 *
	 * From PAC section 15, LST to GST.
	 * 
	 * @param	LST			The local sidereal time in decimal hours.
	 * @param	Λ			The observer's geographical longitude in radians;
	 * 						west longitudes negative, east positive.
	 * @return				The Greenwich sideral time in decimal hours.
	 */
	@Deprecated
	public static double lstToGst(double LST, double Λ) {
		double H = Math.toDegrees(Λ) / 15;
		double G = (LST - H) % 24;
		if (G < 0)
			G += 24;
		return G;
	}
	

    // ******************************************************************** //
	// Formatting Utilities.
	// ******************************************************************** //
	
	/**
	 * Format a decimal time as a string in hours and minutes.
	 * 
	 * @param	hv			The time to format, as fractional hours.
	 * @return				The angle formatted in hours and minutes.
	 */
	public static String timeAsHm(Double hv) {
		if (hv == null)
			return "--";
		
		double h = hv % 24.0;
		if (h < 0)
			h += 24;
			
		int hour = (int) h;
		int min = (int) (h * 60.0) % 60;

		return String.format("%02d:%02d", hour, min);
	}
	
	
	/**
	 * Format a decimal time as a string in hours, minutes and seconds.
	 * 
	 * @param	hv			The time to format, as decimal hours.
	 * @return				The angle formatted in hours, minutes and seconds.
	 */
	public static String timeAsHms(Double hv) {
		if (hv == null)
			return "--";
		
		double h = hv % 24.0;
		if (h < 0)
			h += 24;
			
		int hour = (int) h;
		int min = (int) (h * 60.0) % 60;
		float sec = (float) (h * 3600.0) % 60f;

		return String.format("%02d:%02d:%04.1f", hour, min, sec);
	}
	

	// ******************************************************************** //
	// ΔT.
	// ******************************************************************** //

	/**
	 * Calculate an estimate of the value of ΔT, ie TD - UT in seconds,
	 * for a given moment in time.
	 * 
	 * This method gives a reasonably good value over the period
	 * -1999 to +3000.
	 * 
	 * Reference:	Polynomial Expressions for Delta T (ΔT)
	 *				Espenak and Meeus
	 *				http://eclipse.gsfc.nasa.gov/SEcat5/deltatpoly.html
	 * 
	 * @param	year		The year for which we want ΔT.
	 * @param	month		The fractional month; 0.5 = mid-Jan.  Does not
	 * 						have to be terribly accurate, given the precision
	 * 						to which the variation in ΔT is known.
	 * @return				The value of ΔT for the given year and month.
	 */
	public static double calculateDeltaT(int year, double month) {
		double y = (double) year + month / 12.0;
		double ΔT;
		
		if (year < -500) {
			double u = (y - 1820.0) / 100.0;
			ΔT = -20.0 + 32.0 * u * u;
		} else if (year < 500) {
			// Between years -500 and +500, we use the data from Table 1,
			// except that for the year -500 we changed the value 17190 to
			// 17203.7 in order to avoid a discontinuity with the previous
			// formula at that epoch.  The value for ΔT is given by a
			// polynomial of the 6th degree, which reproduces the values in
			// Table 1 with an error not larger than 4 seconds:
			double u = y / 100.0;
			double u2 = u * u;
			double u3 = u2 * u;
			double u4 = u3 * u;
			double u5 = u4 * u;
			double u6 = u5 * u;
			ΔT = 10583.6 - 1014.41 * u + 33.78311 * u2 - 5.952053 * u3
					- 0.1798452 * u4 + 0.022174192 * u5 + 0.0090316521 * u6;
		} else if (year < 1600) {
			// Between years +500 and +1600, we again use the data from Table 1 to derive a polynomial of the 6th degree.
			double u = (y - 1000.0) / 100.0;
			double u2 = u * u;
			double u3 = u2 * u;
			double u4 = u3 * u;
			double u5 = u4 * u;
			double u6 = u5 * u;
			ΔT = 1574.2 - 556.01 * u + 71.23472 * u2 + 0.319781 * u3
					- 0.8503463 * u4 - 0.005050998 * u5 + 0.0083572073 * u6;
		} else if (year < 1700) {
			double t = y - 1600.0;
			double t2 = t * t;
			double t3 = t2 * t;
			ΔT = 120.0 - 0.9808 * t - 0.01532 * t2 + t3 / 7129.0;
		} else if (year < 1800) {
			double t = y - 1700.0;
			double t2 = t * t;
			double t3 = t2 * t;
			double t4 = t3 * t;
			ΔT = 8.83 + 0.1603 * t - 0.0059285 * t2
					+ 0.00013336 * t3 - t4 / 1174000.0;
		} else if (year < 1860) {
			double t = y - 1800.0;
			double t2 = t * t;
			double t3 = t2 * t;
			double t4 = t3 * t;
			double t5 = t4 * t;
			double t6 = t5 * t;
			double t7 = t6 * t;
			ΔT = 13.72 - 0.332447 * t + 0.0068612 * t2 + 0.0041116 * t3
					- 0.00037436 * t4 + 0.0000121272 * t5
					- 0.0000001699 * t6 + 0.000000000875 * t7;
		} else if (year < 1900) {
			double t = y - 1860.0;
			double t2 = t * t;
			double t3 = t2 * t;
			double t4 = t3 * t;
			double t5 = t4 * t;
			ΔT = 7.62 + 0.5737 * t - 0.251754 * t2 + 0.01680668 * t3
					-0.0004473624 * t4 + t5 / 233174.0;
		} else if (year < 1920) {
			double t = y - 1900.0;
			double t2 = t * t;
			double t3 = t2 * t;
			double t4 = t3 * t;
			ΔT = -2.79 + 1.494119 * t - 0.0598939 * t2
					+ 0.0061966 * t3 - 0.000197 * t4;
		} else if (year < 1941) {
			double t = y - 1920.0;
			double t2 = t * t;
			double t3 = t2 * t;
			ΔT = 21.20 + 0.84493 * t - 0.076100 * t2 + 0.0020936 * t3;
		} else if (year < 1961) {
			double t = y - 1950.0;
			double t2 = t * t;
			double t3 = t2 * t;
			ΔT = 29.07 + 0.407 * t - t2 / 233.0 + t3 / 2547.0;
		} else if (year < 1986) {
			double t = y - 1975.0;
			double t2 = t * t;
			double t3 = t2 * t;
			ΔT = 45.45 + 1.067 * t - t2 / 260.0 - t3 / 718.0;
		} else if (year < 2005) {
			double t = y - 2000.0;
			double t2 = t * t;
			double t3 = t2 * t;
			double t4 = t3 * t;
			double t5 = t4 * t;
			ΔT = 63.86 + 0.3345 * t - 0.060374 * t2 + 0.0017275 * t3
					+ 0.000651814 * t4 + 0.00002373599 * t5;
		} else if (year < 2050) {
			// This expression is derived from estimated values of ΔT
			// in the years 2010 and 2050.  The value for 2010 (66.9 seconds)
			// is based on a linearly extrapolation from 2005 using 0.39
			// seconds/year (average from 1995 to 2005).  The value for
			// 2050 (93 seconds) is linearly extrapolated from 2010 using
			// 0.66 seconds/year (average rate from 1901 to 2000).
			double t = y - 2000.0;
			double t2 = t * t;
			ΔT = 62.92 + 0.32217 * t + 0.005589 * t2;
		} else if (year < 2150) {
			// The last term is introduced to eliminate the discontinuity
			// at 2050.
			double u = (y - 1820.0) / 100.0;
			double u2 = u * u;
			ΔT = -20.0 + 32.0 * u2 - 0.5628 * (2150.0 - y);
		} else {
			double u = (y - 1820.0) / 100.0;
			double u2 = u * u;
			ΔT = -20.0 + 32.0 * u2;
		}
		
		return ΔT;
	}


	// ******************************************************************** //
	// Class Data.
	// ******************************************************************** //

    // Debugging tag.
	@SuppressWarnings("unused")
	private static final String TAG = "astro";
    

	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The Julian date in days of this instant relative to the
	// 4713 BC epoch in UT.
	private final double julianDateUt;
	
	// The Julian date in days of this instant relative to the
	// 4713 BC epoch in TD.
	private final double julianDateTd;
	
	// The value for ΔT at this instant, in seconds.
	private final double deltaT;

}

