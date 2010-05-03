
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


import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import org.hermit.geo.GeoConstants;
import org.hermit.geo.Position;


/**
 * This class represents a particular set of circumstances for a
 * calculation, for example at a particular location and moment in time.
 * It provides methods to access information about all the celestial
 * bodies relating to this observation.
 * 
 * <p>There may be multiple Observations in existence at one time,
 * each one representing a particular set of circumstances -- for example,
 * a particular time of observation.  Each Observation has its own set of
 * Body objects associated with it.
 */
public class Observation
	implements AstroConstants
{
	
	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

	// Class embodying a calculate method which works out one or more fields.
	private abstract static class Calc {
		abstract void c(Observation o);
	}
	
	
	/**
	 * This enumeration defines the data fields that are stored for
	 * each body.
	 */
	public enum OField {
		
		/**
		 * The quantity ρ sin φ' is used for the calculation of parallax.
		 */
		RHO_SIN_PHI1,
		
		/**
		 * The quantity ρ cos φ' is used for the calculation of parallax.
		 */
		RHO_COS_PHI1,
		
		/**
		 * Greenwich mean sidereal time at midnight UT on the day of
		 * the observation.
		 */
		GMST_MIDNIGHT,
		
		/**
		 * Greenwich mean sidereal time at the moment of the observation.
		 */
		GMST_INSTANT,
		
		/**
		 * Greenwich apparent sidereal time at midnight UT on the day of
		 * the observation.
		 */
		GAST_MIDNIGHT,
		
		/**
		 * Greenwich apparent sidereal time at the moment of the observation.
		 */
		GAST_INSTANT,
		
		/**
		 * Local mean sidereal time at midnight UT on the day of
		 * the observation.
		 */
		LMST_MIDNIGHT,
		
		/**
		 * Local mean sidereal time at the moment of the observation.
		 */
		LMST_INSTANT,
		
		/**
		 * Local apparent sidereal time at midnight UT on the day of
		 * the observation.
		 */
		LAST_MIDNIGHT,
		
		/**
		 * Local apparent sidereal time at the moment of the observation.
		 */
		LAST_INSTANT,
       
        /**
         * Nutation in longitude.
         */
		NUTATION_IN_LONGITUDE,
        
        /**
         * Nutation in obliquity.
         */
		NUTATION_IN_OBLIQUITY,
        
        /**
         * Mean obliquity.
         */
		MEAN_OBLIQUITY,
        
        /**
         * True obliquity.
         */
		TRUE_OBLIQUITY,
        
        /**
         * Apparent LST.
         */
		APPARENT_LST;

		private static void register(OField field, Calc calc) {
			if (field.calculator != null)
				throw new RuntimeException("Obs Field " + field +
										   " already has a calculator");
			field.calculator = calc;
		}
		
		private void calculate(Observation o) {
			if (calculator == null)
				throw new RuntimeException("Obs Field " + this + " has no calculator");
			calculator.c(o);
		}
		
		private Calc calculator = null;
	}
    
    private static final OField[] ALL_FIELDS = OField.values();
    
    private static final int NUM_FIELDS = ALL_FIELDS.length;


	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

	/**
	 * Create an observation for a given Julian day.
	 * 
	 * @param	i			The Instant of time for this observation.
	 */
	public Observation(Instant i) {
		this(i, new Position(0, 0));
	}


    /**
     * Create an observation for a given Julian day.
     * 
     * @param   i           The Instant of time for this observation.
     * @param   pos         The observer's geographical position.
     */
    public Observation(Instant i, Position pos) {
        observationTime = i;
        observerPos = pos;
        
        // Create the table where we will store bodies as we create them.
        celestialBodies = new Body[Body.NUM_BODIES];
        
        // Create the data cache.
        dataCache = new Double[NUM_FIELDS];
        invalidate();
    }


	/**
	 * Create an observation for a given Java time.
	 * 
	 * @param	time		Java-style time in milliseconds since 1 Jan,
	 *						1970.
	 */
	public Observation(long time) {
		this(new Instant(time));
	}


    /**
     * Create an observation for a given Java time.
     * 
     * @param   time        Java-style time in milliseconds since 1 Jan,
     *                      1970.
     * @param   pos         The observer's geographical position.
     */
    public Observation(long time, Position pos) {
        this(new Instant(time), pos);
    }


	/**
	 * Create an observation for right now.
	 */
	public Observation() {
		this(System.currentTimeMillis());
	}


	/**
	 * Create an observation for a given Julian day.
	 * 
	 * @param	jd			The date to set as a Julian date relative
	 *						to the astronomical epoch of 4713 BC UTC.
	 */
	public Observation(double jd) {
		this(new Instant(jd));
	}

	   
    // ******************************************************************** //
	// Time Setup.
	// ******************************************************************** //
	
	/**
	 * Get the time of this Observation.
	 * 
	 * @return				The time of this Observation.
	 */
	public Instant getTime() {
		return observationTime;
	}
	
	
	/**
	 * Get the UT1 Julian date of this Observation.
	 * 
	 * @return				The configured Julian date relative
	 *						to the astronomical epoch of 4713 BC UT1.
	 */
	public double getUt() {
		return observationTime.getUt();
	}
	
	
	/**
	 * Get the Julian date in TD of this Observation.
	 * 
	 * <p>Note that we don't distinguish between TDT(TT) and TDB, which
	 * are always within 0.0017 seconds.
	 * 
	 * @return				The Julian date in TD, in days.
	 */
	public double getTd() {
		return observationTime.getTd();
	}


	/**
	 * Set the time for calculations.  This clears out any cached data we
	 * may have calculated.
	 * 
	 * @param	time		The time to use for all calculations on this
	 * 						Observation.
	 */
	public void setTime(Instant time) {
		observationTime = time;
		
		// Invalidate the data caches.
		invalidate();
	}
	

	/**
	 * Set the date / time of this Observation.  This clears out any
	 * cached data we may have calculated.
	 * 
	 * @param	y			Year number; BC years in astronomical form.
	 * @param	m			Month number; January = 1.
	 * @param	d			Day of the month, including the fraction of
	 * 						the day; e.g. 0.25 = 6 a.m.
	 */
	public void setDate(int y, int m, double d) {
		setTime(new Instant(y, m, d));
	}
	

	/**
	 * Set the time of this Observation.  This clears out any cached data we
	 * may have calculated.
	 * 
	 * @param	time		Java-style time in milliseconds since 1 Jan,
	 *						1970.
	 */
	public void setJavaTime(long time) {
		setTime(new Instant(time));
	}
	

	/**
	 * Set the time for calculations as a UTC Julian date.  This clears
	 * out any cached data we may have calculated.
	 * 
	 * @param	jd			The date to set as a Julian date relative
	 *						to the astronomical epoch of 4713 BC UTC.
	 */
	public void setJulian(double jd) {
		setTime(new Instant(jd));
	}
	
	
	/**
	 * Get the number of days since a given epoch.
	 * 
	 * @param	epoch		Epoch of interest (e.g. Instant.JD_1990).
	 * @return				The number of days since the 1990 Jan 0.0 epoch.
	 */
	public double getDaysSince(double epoch) {
		return getUt() - epoch;
	}


    // ******************************************************************** //
	// Observer Setup.
	// ******************************************************************** //
	
	/**
	 * Get the observer's geographical position.
	 * 
	 * @return				The observer's geographical position.
	 */
	public Position getObserverPosition() {
		return observerPos;
	}


	/**
	 * Set the observer's position for this observation.  This clears
	 * out any cached data we may have calculated.
	 * 
	 * @param	pos			The observer's geographical position.
	 */
	public void setObserverPosition(Position pos) {
		observerPos = pos;

		// Invalidate the data caches.
		invalidate();
	}
	
	
	/**
	 * Get the observer's altitude.
	 * 
	 * @return				The observer's altitude above sea level in metres.
	 */
	public double getObserverAltitude() {
		return observerAlt;
	}


	/**
	 * Set the observer's altitude for this observation.  This clears
	 * out any cached data we may have calculated.
	 * 
	 * @param	alt			The observer's altitude above sea level in metres.
	 */
	public void setObserverAltitude(double alt) {
		observerAlt = alt;

		// Invalidate the data caches.
		invalidate();
	}
	

    // ******************************************************************** //
	// Celestial Bodies.
	// ******************************************************************** //

	/**
	 * Get the named celestial body.  There is a single instance of each body
	 * associated with the set of observing circumstances represented by
	 * this Observation; we return a handle to that instance.
	 * 
	 * <p>Bear in mind that there may be multiple Observations in use;
	 * each one has its own set of associated Body objects.
	 * 
	 * @param	which		Which body to get.
	 * @return				A handle on the instance of that body for this
	 * 						Observation.
	 */
	public Body getBody(Body.Name which) {
		int ord = which.ordinal();
		if (celestialBodies[ord] == null) {
			Body bod;
			if (which == Body.Name.SUN)
				bod = new Sun(this);
			else if (which == Body.Name.MOON)
				bod = new Moon(this);
			else
				bod = new Planet(this, which);
			celestialBodies[ord] = bod;
		}
		
		return celestialBodies[ord];
	}


	/**
	 * Get the Sun.  There is a single instance of Sun
	 * associated with the set of observing circumstances represented by
	 * this Observation; we return a handle to that instance.
	 * 
	 * <p>This is essentially a convenience routine which calls getBody()
	 * and casts the return to the correct type.
	 * 
	 * @return				A handle on the instance of the Sun for this
	 * 						Observation.
	 */
	public Sun getSun() {
		try {
			return (Sun) getBody(Body.Name.SUN);
		} catch (ClassCastException e) {
			throw new CalcError("SUN object is not an instance of Sun");
		}
	}


	/**
	 * Get the Moon.  There is a single instance of Moon
	 * associated with the set of observing circumstances represented by
	 * this Observation; we return a handle to that instance.
	 * 
	 * <p>This is essentially a convenience routine which calls getBody()
	 * and casts the return to the correct type.
	 * 
	 * @return				A handle on the instance of the Moon for this
	 * 						Observation.
	 */
	public Moon getMoon() {
		try {
			return (Moon) getBody(Body.Name.MOON);
		} catch (ClassCastException e) {
			throw new CalcError("MOON object is not an instance of Moon");
		}
	}


	/**
	 * Get a planet.  There is a single instance of each planet
	 * associated with the set of observing circumstances represented by
	 * this Observation; we return a handle to that instance.
	 * 
	 * <p>This is essentially a convenience routine which calls getBody()
	 * and casts the return to the correct type.
	 * 
	 * @param	which		Which planet to get.
	 * @return				A handle on the instance of the given planet
	 * 						for this Observation.
	 */
	public Planet getPlanet(Body.Name which) {
		try {
			return (Planet) getBody(which);
		} catch (ClassCastException e) {
			throw new CalcError(which.toString() +
								 " object is not an instance of Planet");
		}
	}


    // ******************************************************************** //
	// Circumstances Data.
	// ******************************************************************** //
	
	/**
	 * Get the value of one of the data fields of this Observation.
	 * 
	 * @param	key			The field we want.
	 * @return				The field value.
	 */
	public double get(OField key) {
        if (dataCache[key.ordinal()] == null)
            key.calculate(this);
        
        // Get the value.  It has to be there now.
        Double val = dataCache[key.ordinal()];
        if (val == null)
            throw new CalcError("Calculator for observation field " +
                                key + " failed");

		return val;
	}
	
	
	/**
	 * Save a specified value in the data cache.
	 * 
	 * @param	key			The name of the value to save.
	 * @param	val			The value.
	 */
	protected void put(OField key, Double val) {
        dataCache[key.ordinal()] = val;
	}
	
	
	/**
	 * Invalidate all of the data caches associated with this Observation.
	 */
	protected void invalidate() {
		// Clear the data calculated for this Observation.
        for (int i = 0; i < NUM_FIELDS; ++i)
            dataCache[i] = null;
		
		// Clear the caches in all the bodies.
		for (Body b : celestialBodies)
			if (b != null)
				b.invalidate();
	}

	
    // ******************************************************************** //
	// Data Calculation.
	// ******************************************************************** //

	/**
	 * Calculate the values of ρ sin φ' and ρ cos φ' for the observer on
	 * the currently configured date.
	 * The results are stored in the cache as RHO_SIN_PHI1, RHO_COS_PHI1.
	 *
	 * <p>From AA chapter 11.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Observation o) { o.calcRhoPhiPrime(); }
		};
		OField.register(OField.RHO_SIN_PHI1, calc);
		OField.register(OField.RHO_COS_PHI1, calc);
	}
	private void calcRhoPhiPrime() {
		double φ = observerPos.getLatRads();

		// Inverse of the Earth's flattening.
		double f1 = GeoConstants.POLAR_RADIUS / GeoConstants.EQUATORIAL_RADIUS;
		
		// Observer's altitude as a fraction of the equatorial radius.
		double Hf= observerAlt / GeoConstants.EQUATORIAL_RADIUS;
		
		// Compute the results.
		double u = atan(f1 * tan(φ));
		double ρsinφ1 = f1 * sin(u) + Hf * sin(φ);
		double ρcosφ1 = cos(u) + Hf * cos(φ);
		
		put(OField.RHO_SIN_PHI1, ρsinφ1);
		put(OField.RHO_COS_PHI1, ρcosφ1);
	}
	

	/**
	 * Calculate the nutation in both longitude and obliquity for
	 * the currently configured date.  The results are stored in the cache
	 * as NUTATION_IN_LONGITUDE and NUTATION_IN_OBLIQUITY.
	 *
	 * From AA chapter 22.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Observation o) { o.calcNutation(); }
		};
		OField.register(OField.NUTATION_IN_LONGITUDE, calc);
		OField.register(OField.NUTATION_IN_OBLIQUITY, calc);
	}
	private void calcNutation() {
		// Note: we need Dynamical Time here.
		double T = (observationTime.getTd() - J2000) / 36525.0;
		double T2 = T * T;
		double T3 = T2 * T;
		
		// Calculate angles in degrees.  Mean elongation of the Moon:
		double D = 297.85036 + 445267.111480 * T - 0.0019142 * T2 + T3 / 189474;
		
		// Mean anomaly of the Sun:
		double M = 357.52772 + 35999.050340 * T - 0.0001603 * T2 - T3 / 300000;
		
		// Mean anomaly of the Moon:
		double M1 = 134.96298 + 477198.867398 * T + 0.0086972 * T2 + T3 / 56250;
		
		// Moon's argument of latitude:
		double F = 93.27191 + 483202.017538 * T - 0.0036825 * T2 + T3 / 327270;
		
		// Longitude of the ascending node of the moon's mean orbit:
		double Ω = 125.04452 - 1934.136261 * T + 0.0020708 * T2 + T3 / 450000;

		// Calculate Δψ and Δε in arcseconds.  Convert to radians.
		double Δψ = 0;
		double Δε = 0;
		for (NutationTerm term : nutationTerms) {
			double a = term.D * D + term.M * M + 
					   term.M1 * M1 + term.F * F + term.Ω * Ω;
			Δψ += (term.Δψ0 + term.Δψ1 * T) * sin(toRadians(a)) * 0.0001; 
			Δε += (term.Δε0 + term.Δε1 * T) * cos(toRadians(a)) * 0.0001; 
		}
		Δψ = toRadians(Δψ / 3600);
		Δε = toRadians(Δε / 3600);
		
		put(OField.NUTATION_IN_LONGITUDE, Δψ);
		put(OField.NUTATION_IN_OBLIQUITY, Δε);
	}
	

	/**
	 * Calculate the mean obliquity of the ecliptic (angle between the
	 * ecliptic and the equator, not including nutation) for the currently
	 * configured date.  The result is stored in the cache as MEAN_OBLIQUITY.
	 *
	 * From AA chapter 22.
	 */
	static {
		OField.register(OField.MEAN_OBLIQUITY, new Calc() {
			@Override void c(Observation o) { o.calcMeanObliquity(); }
		});
	}
	private void calcMeanObliquity() {
		double j = getTd() - J2000;
		double T = j / 36525.0;
		double T2 = T * T;
		double T3 = T2 * T;
		
		// Calculate the delta, and convert to radians.
		double delta = 46.8150 * T + 0.00059 * T2 - 0.001813 * T3;
		delta = toRadians(delta / 3600.0);
		
		// Get the obliquity.
		double ε0 = ε_2000 - delta;
		
		put(OField.MEAN_OBLIQUITY, ε0);
	}


	/**
	 * Calculate the true obliquity of the ecliptic (angle between the
	 * ecliptic and the equator, including the effect of nutation) for
	 * the currently configured date.  The result is stored in the cache
	 * as TRUE_OBLIQUITY.
	 *
	 * From AA chapter 22.
	 */
	static {
		OField.register(OField.TRUE_OBLIQUITY, new Calc() {
			@Override void c(Observation o) { o.calcTrueObliquity(); }
		});
	}
	private void calcTrueObliquity() {
		double ε0 = get(OField.MEAN_OBLIQUITY);
		double Δε = get(OField.NUTATION_IN_OBLIQUITY);
		
		// Now we get the true obliquity.
		double ε = ε0 + Δε;
		
		put(OField.TRUE_OBLIQUITY, ε);
	}
	

	/**
	 * Calculate the mean sidereal time for the currently configured date.
	 *  The results are stored in the cache as GMST_MIDNIGHT, GMST_INSTANT.
	 *
	 * From AA chapter 12.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Observation o) { o.calcMeanSidereal(); }
		};
		OField.register(OField.GMST_MIDNIGHT, calc);
		OField.register(OField.GMST_INSTANT, calc);
		OField.register(OField.LMST_MIDNIGHT, calc);
		OField.register(OField.LMST_INSTANT, calc);
	}
	private void calcMeanSidereal() {
		// Get The Julian date of midnight on the day of the given time.
		// This is a day number ending in .5.  And get the decimal hour
		// since midnight.
		double jd = getUt();
		double midnight = floor(jd + 0.5) - 0.5;
		double hour = (jd - midnight) * 24.0;
		
		double T = (midnight - J2000) / 36525.0;
		double T2 = T * T;
		double T3 = T2 * T;
		
		// Calculate the GMST in hours.
		double GMSTMidnight = 24110.54841 + 8640184.812866 * T +
											0.093104 * T2 - 6.2E-6 * T3;
		GMSTMidnight = (GMSTMidnight / 3600.0) % 24.0;
		if (GMSTMidnight < 0)
			GMSTMidnight += 24;
		
		double GMST = (hour * 1.00273790935 + GMSTMidnight) % 24.0;
		
		put(OField.GMST_MIDNIGHT, GMSTMidnight);
		put(OField.GMST_INSTANT, GMST);
		
		double offset = observerPos.getLonDegs() / 15.0;
		double LMSTMidnight = (GMSTMidnight + offset) % 24.0;
		if (LMSTMidnight < 0)
			LMSTMidnight += 24;
		double LMST = (GMST + offset) % 24.0;
		if (LMST < 0)
			LMST += 24;
		
		put(OField.LMST_MIDNIGHT, LMSTMidnight);
		put(OField.LMST_INSTANT, LMST);
	}
	

	/**
	 * Calculate the apparent sidereal time for the currently configured date.
	 * The results are stored in the cache as GAST_MIDNIGHT, GAST_INSTANT.
	 *
	 * From AA chapter 12.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Observation o) { o.calcApparentSidereal(); }
		};
		OField.register(OField.GAST_MIDNIGHT, calc);
		OField.register(OField.GAST_INSTANT, calc);
		OField.register(OField.LAST_MIDNIGHT, calc);
		OField.register(OField.LAST_INSTANT, calc);
	}
	private void calcApparentSidereal() {
		double ε = get(OField.TRUE_OBLIQUITY);
		double Δψ = get(OField.NUTATION_IN_LONGITUDE);
		double GMSTMidnight = get(OField.GMST_MIDNIGHT);
		double GMST = get(OField.GMST_INSTANT);
		double LMSTMidnight = get(OField.LMST_MIDNIGHT);
		double LMST = get(OField.LMST_INSTANT);
	
		// Calculate the correction as a decimal hour fraction.
		double corr = toDegrees(Δψ) * cos(ε) / 15.0;
		
		double GASTMidnight = (GMSTMidnight + corr) % 24.0;
		double GAST = (GMST + corr) % 24.0;
		double LASTMidnight = (LMSTMidnight + corr) % 24.0;
		double LAST = (LMST + corr) % 24.0;
		
		put(OField.GAST_MIDNIGHT, GASTMidnight);
		put(OField.GAST_INSTANT, GAST);
		put(OField.LAST_MIDNIGHT, LASTMidnight);
		put(OField.LAST_INSTANT, LAST);
	}
	
	
    // ******************************************************************** //
	// Co-Ordinate Utilities.
	// ******************************************************************** //
	
	/**
	 * Convert ecliptic co-ordinates to mean equatorial co-ordinates
	 * (right ascension and declination) for the circumstances of this
	 * Observation.  This does not take nutation into account.
	 * 
	 * <p>Note the returned ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	λ			The ecliptic longitude, in radians.
	 * @param	β			The ecliptic latitude, in radians.
	 * @param	pos			An array { α, δ } in which the right ascension
	 * 						in radians and the declination in radians will
	 * 						be placed.
	 */
	public void eclipticToMeanEquatorial(double λ, double β, double[] pos) {
		double ε0 = get(OField.MEAN_OBLIQUITY);
		ecToEq(ε0, λ, β, pos);
	}


	/**
	 * Convert ecliptic co-ordinates to apparent equatorial co-ordinates
	 * (right ascension and declination) for the circumstances of this
	 * Observation.  This takes nutation into account -- so don't apply
	 * nutation to the returned values.
	 * 
	 * <p>Note the returned ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	λ			The ecliptic longitude, in radians.
	 * @param	β			The ecliptic latitude, in radians.
	 * @param	pos			An array { α, δ } in which the right ascension
	 * 						in radians and the declination in radians will
	 * 						be placed.
	 */
	public void eclipticToApparentEquatorial(double λ, double β, double[] pos) {
		double Δψ = get(OField.NUTATION_IN_LONGITUDE);
		double ε = get(OField.TRUE_OBLIQUITY);
		ecToEq(ε, λ + Δψ, β, pos);
	}


	/**
	 * Convert ecliptic co-ordinates to equatorial co-ordinates
	 * (right ascension and declination) for a given value of the
	 * obliquity of the ecliptic.
	 * 
	 * <p>Note the returned ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	ε			The value to use for the obliquity of the ecliptic.
	 * @param	λ			The ecliptic longitude, in radians.
	 * @param	β			The ecliptic latitude, in radians.
	 * @param	pos			An array { α, δ } in which the right ascension
	 * 						in radians and the declination in radians will
	 * 						be placed.
	 */
	private static void ecToEq(double ε, double λ, double β, double[] pos) {
		double y = sin(λ) * cos(ε) - tan(β) * sin(ε);
		double x = cos(λ);
		double α = atan2(y, x);
		if (α < 0)
			α += TWOPI;
		
		double sinδ = sin(β) * cos(ε) + cos(β) * sin(ε) * sin(λ);
		double δ = asin(sinδ);
		
		pos[0] = α;
		pos[1] = δ;
	}

	
	/**
	 * Convert mean equatorial co-ordinates (right ascension and
	 * declination) to ecliptic co-ordinates for the circumstances of this
	 * Observation.  This does not take nutation into account.
	 * 
	 * <p>Note the given ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	α			The mean right ascension, in radians.
	 * @param	δ			The mean declination, in radians.
	 * @param	pos			An array { λ, β } in which the ecliptic longitude
	 * 						and the ecliptic latitude in radians will
	 * 						be placed.
	 */
	public void meanEquatorialToEcliptic(double α, double δ, double[] pos) {
		double ε0 = get(OField.MEAN_OBLIQUITY);
		eqToEc(ε0, α, δ, pos);
	}

	
	/**
	 * Convert apparent equatorial co-ordinates (right ascension and
	 * declination) to ecliptic co-ordinates for the circumstances of this
	 * Observation.  This takes nutation into account.
	 * 
	 * <p>Note the given ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	α			The mean right ascension, in radians.
	 * @param	δ			The mean declination, in radians.
	 * @param	pos			An array { λ, β } in which the ecliptic longitude
	 * 						and the ecliptic latitude in radians will
	 * 						be placed.
	 */
	public void apparentEquatorialToEcliptic(double α, double δ, double[] pos) {
		double Δψ = get(OField.NUTATION_IN_LONGITUDE);
		double ε = get(OField.TRUE_OBLIQUITY);
		eqToEc(ε, α, δ, pos);
		
		// This is assumed based on eclipticToApparentEquatorial().
		pos[0] -= Δψ;
	}


	/**
	 * Convert equatorial co-ordinates to ecliptic co-ordinates
	 * (ecliptic longitude and latitude) for a given value of the
	 * obliquity of the ecliptic.
	 * 
	 * <p>Note the given ascension is in radians, not hours, for internal
	 * consistency.
	 *
	 * <p>From AA chapter 13.
	 * 
	 * @param	ε			The value to use for the obliquity of the ecliptic.
	 * @param	α			The right ascension, in radians.
	 * @param	δ			The declination, in radians.
	 * @param	pos			An array { λ, β } in which the ecliptic longitude
	 * 						and the ecliptic latitude in radians will
	 * 						be placed.
	 */
	private static void eqToEc(double ε, double α, double δ, double[] pos) {
		double y = sin(α) * cos(ε) + tan(δ) * sin(ε);
		double x = cos(α);
		double λ = modTwoPi(atan2(y, x));
		
		double sinβ = sin(δ) * cos(ε) - cos(δ) * sin(ε) * sin(α);
		double β = asin(sinβ);
		
		pos[0] = λ;
		pos[1] = β;
	}


	/**
	 * Convert co-ordinates in the VSOP87 system to the FK5 system.
	 *
	 * <p>From AA chapter 32.
	 * 
	 * @param	L			The longitude in radians.
	 * @param	B			The latitude in radians.
	 * @param	pos			An array { L, B } in which the corrected
	 * 						longitude and latitude in radians will
	 * 						be placed.
	 */
	void vsopToFk5(double L, double B, double[] pos) {
		double T = (getTd() - J2000) / 36525.0;
		double T2 = T * T;

		double L1 = L - toRadians(1.397) * T - toRadians(0.00031) * T2;
		double ΔL = secsToRads(-0.09033) +
						secsToRads(0.03916) * (cos(L1) + sin(L1)) * tan(B);
		double ΔB = secsToRads(0.03916) * (cos(L1) - sin(L1));
		
		pos[0] = L + ΔL;
		pos[1] = B + ΔB;
	}


    // ******************************************************************** //
	// Formatting Utilities.
	// ******************************************************************** //
	
	/**
	 * Format an angle as a string in degrees, minutes and seconds.
	 * 
	 * From section 21 / 8.
	 * 
	 * @param	ar			The angle to format, in radians.
	 * @return				The angle formatted in degrees, minutes and seconds.
	 */
	@Deprecated
	public static String angleAsDms(Double ar) {
		if (ar == null)
			return "--";
		
		double a = toDegrees(ar);

		int s = a < 0 ? -1 : 1;
		a *= s;
		
		int deg = (int) a * s;
		int min = (int) (a * 60.0) % 60;
		float sec = (float) (a * 3600.0) % 60f;
		
		return String.format("%3d°%02d'%04.1f\"", deg, min, sec);
	}
	
	
	/**
	 * Format an angle as a string in hours, minutes and seconds.  This is
	 * appropriate for a right ascension.
	 * 
	 * From section 8.
	 * 
	 * @param	ar			The angle to format, in radians.
	 * @return				The angle formatted in hours, minutes and seconds.
	 */
	@Deprecated
	public static String angleAsHms(Double ar) {
		if (ar == null)
			return "--";
		
		double a = toDegrees(ar) % 360.0;
		if (a < 0)
			a += 360.0;
		
		// Convert to hours.
		double h = a / 15.0;
		
		int hour = (int) h;
		int min = (int) (h * 60.0) % 60;
		float sec = (float) (h * 3600.0) % 60f;
		
		return String.format("%3dh%02d'%04.1f\"", hour, min, sec);
	}
	
	
	/**
	 * Return the given value mod 2*PI, with negative values made positive --
	 * in other words, the value put into the range [0 .. TWOPI).
	 * 
	 * @param	v			Input value.
	 * @return				v % TWOPI, plus TWOPI if negative.
	 */
	static final double modTwoPi(double v) {
		v %= TWOPI;
		return v < 0 ? v + TWOPI : v;
	}

	
	/**
	 * Convert an angle in arcseconds to radians.
	 * 
	 * @param	v			An angle in arcseconds.
	 * @return				The same angle converted to radians.
	 */
	static final double secsToRads(double v) {
		return toRadians(v / 3600.0);
	}


	// ******************************************************************** //
	// Class Data.
	// ******************************************************************** //

    // Debugging tag.
	@SuppressWarnings("unused")
	private static final String TAG = "onwatch";
    
	// Monster table of coefficients for the calculation of nutation.
	private static final class NutationTerm {
		NutationTerm(int D, int M, int M1, int F, int Ω,
					 double Δψ0, double Δψ1, double Δε0, double Δε1)
		{
			this.D = D;
			this.M = M;
			this.M1 = M1;
			this.F = F;
			this.Ω = Ω; 
			this.Δψ0 = Δψ0;
			this.Δψ1 = Δψ1;
			this.Δε0 = Δε0;
			this.Δε1 = Δε1;
		}
		final int D;
		final int M;
		final int M1;
		final int F;
		final int Ω; 
		final double Δψ0;
		final double Δψ1;
		final double Δε0;
		final double Δε1;
	};
	private static final NutationTerm nutationTerms[] = { 
        new NutationTerm( 0,  0,  0,  0,  1, -171996,  -174.2,  92025,  8.9),
        new NutationTerm(-2,  0,  0,  2,  2,  -13187,    -1.6,   5736, -3.1),
        new NutationTerm( 0,  0,  0,  2,  2,   -2274,    -0.2,    977, -0.5),
        new NutationTerm( 0,  0,  0,  0,  2,    2062,     0.2,   -895,  0.5),
        new NutationTerm( 0,  1,  0,  0,  0,    1426,    -3.4,     54, -0.1),
        new NutationTerm( 0,  0,  1,  0,  0,     712,     0.1,     -7,    0),
        new NutationTerm(-2,  1,  0,  2,  2,    -517,     1.2,    224, -0.6),
        new NutationTerm( 0,  0,  0,  2,  1,    -386,    -0.4,    200,    0),
        new NutationTerm( 0,  0,  1,  2,  2,    -301,       0,    129, -0.1),
        new NutationTerm(-2, -1,  0,  2,  2,     217,    -0.5,    -95,  0.3),
        new NutationTerm(-2,  0,  1,  0,  0,    -158,       0,      0,    0),
        new NutationTerm(-2,  0,  0,  2,  1,     129,     0.1,    -70,    0),
        new NutationTerm( 0,  0, -1,  2,  2,     123,       0,    -53,    0),
        new NutationTerm( 2,  0,  0,  0,  0,      63,       0,      0,    0),
        new NutationTerm( 0,  0,  1,  0,  1,      63,     0.1,    -33,    0),
        new NutationTerm( 2,  0, -1,  2,  2,     -59,       0,     26,    0),
        new NutationTerm( 0,  0, -1,  0,  1,     -58,    -0.1,     32,    0),
        new NutationTerm( 0,  0,  1,  2,  1,     -51,       0,     27,    0),
        new NutationTerm(-2,  0,  2,  0,  0,      48,       0,      0,    0),
        new NutationTerm( 0,  0, -2,  2,  1,      46,       0,    -24,    0),
        new NutationTerm( 2,  0,  0,  2,  2,     -38,       0,     16,    0),
        new NutationTerm( 0,  0,  2,  2,  2,     -31,       0,     13,    0),
        new NutationTerm( 0,  0,  2,  0,  0,      29,       0,      0,    0),
        new NutationTerm(-2,  0,  1,  2,  2,      29,       0,    -12,    0),
        new NutationTerm( 0,  0,  0,  2,  0,      26,       0,      0,    0),
        new NutationTerm(-2,  0,  0,  2,  0,     -22,       0,      0,    0),
        new NutationTerm( 0,  0, -1,  2,  1,      21,       0,    -10,    0),
        new NutationTerm( 0,  2,  0,  0,  0,      17,    -0.1,      0,    0),
        new NutationTerm( 2,  0, -1,  0,  1,      16,       0,     -8,    0),
        new NutationTerm(-2,  2,  0,  2,  2,     -16,     0.1,      7,    0),
        new NutationTerm( 0,  1,  0,  0,  1,     -15,       0,      9,    0),
        new NutationTerm(-2,  0,  1,  0,  1,     -13,       0,      7,    0),
        new NutationTerm( 0, -1,  0,  0,  1,     -12,       0,      6,    0),
        new NutationTerm( 0,  0,  2, -2,  0,      11,       0,      0,    0),
        new NutationTerm( 2,  0, -1,  2,  1,     -10,       0,      5,    0),
        new NutationTerm( 2,  0,  1,  2,  2,      -8,       0,      3,    0),
        new NutationTerm( 0,  1,  0,  2,  2,       7,       0,     -3,    0),
        new NutationTerm(-2,  1,  1,  0,  0,      -7,       0,      0,    0),
        new NutationTerm( 0, -1,  0,  2,  2,      -7,       0,      3,    0),
        new NutationTerm( 2,  0,  0,  2,  1,      -7,       0,      3,    0),
        new NutationTerm( 2,  0,  1,  0,  0,       6,       0,      0,    0),
        new NutationTerm(-2,  0,  2,  2,  2,       6,       0,     -3,    0),
        new NutationTerm(-2,  0,  1,  2,  1,       6,       0,     -3,    0),
        new NutationTerm( 2,  0, -2,  0,  1,      -6,       0,      3,    0),
        new NutationTerm( 2,  0,  0,  0,  1,      -6,       0,      3,    0),
        new NutationTerm( 0, -1,  1,  0,  0,       5,       0,      0,    0),
        new NutationTerm(-2, -1,  0,  2,  1,      -5,       0,      3,    0),
        new NutationTerm(-2,  0,  0,  0,  1,      -5,       0,      3,    0),
        new NutationTerm( 0,  0,  2,  2,  1,      -5,       0,      3,    0),
        new NutationTerm(-2,  0,  2,  0,  1,       4,       0,      0,    0),
        new NutationTerm(-2,  1,  0,  2,  1,       4,       0,      0,    0),
        new NutationTerm( 0,  0,  1, -2,  0,       4,       0,      0,    0),
        new NutationTerm(-1,  0,  1,  0,  0,      -4,       0,      0,    0),
        new NutationTerm(-2,  1,  0,  0,  0,      -4,       0,      0,    0),
        new NutationTerm( 1,  0,  0,  0,  0,      -4,       0,      0,    0),
        new NutationTerm( 0,  0,  1,  2,  0,       3,       0,      0,    0),
        new NutationTerm( 0,  0, -2,  2,  2,      -3,       0,      0,    0),
        new NutationTerm(-1, -1,  1,  0,  0,      -3,       0,      0,    0),
        new NutationTerm( 0,  1,  1,  0,  0,      -3,       0,      0,    0),
        new NutationTerm( 0, -1,  1,  2,  2,      -3,       0,      0,    0),
        new NutationTerm( 2, -1, -1,  2,  2,      -3,       0,      0,    0),
        new NutationTerm( 0,  0,  3,  2,  2,      -3,       0,      0,    0),
        new NutationTerm( 2, -1,  0,  2,  2,      -3,       0,      0,    0),
	};


	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The current Julian date relative to the 4713 BC epoch.  Null if
	// not yet set.
	private Instant observationTime = null;
	
	// The observer's geographical position.
	private Position observerPos = null;
	
	// The observer's altitude above sea level in metres.
	private double observerAlt = 0.0;

	// The celestial bodies in this Universe.
	private Body[] celestialBodies;

	// Cache of values calculated for this body at the currently
	// configured date / time.
	private Double[] dataCache;

}

