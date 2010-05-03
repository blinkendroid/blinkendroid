
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
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


/**
 * This class represents the Sun, and provides all known information
 * about it.
 * 
 * This subclass of {@link Body} basically provides custom calculation
 * routines relevant to the Sun.
 * 
 * Note that we depart from the usual Java naming conventions here.  To
 * simplify reference to source materials, variables are named according
 * to the original source text, Greek letters included.  So, be careful
 * when looking at names; "Χ" may be the Greek Chi, rather than Roman.
 */
public class Sun
	extends Body
{

	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //
	
	/**
	 * Create a Sun.  Note that this constructor is non-public; the
	 * only way to get a Sun is by calling Observation.getSun().
	 * 
	 * @param	o			The Observation this Sun belongs to.  This contains
	 * 						all global configuration, like the current time.
	 */
	Sun(Observation o) {
		super(o, Name.SUN);
		
		observation = o;
	}


    // ******************************************************************** //
	// Data Calculation.
	// ******************************************************************** //

	/**
	 * Calculate the heliocentric co-ordinates of the body for the currently
	 * configured time.  Result is stored in the cache as HE_LATITUDE,
	 * HE_LONGITUDE and HE_RADIUS.
	 *
	 * @throws	AstroError	Invalid request.
	 */
	@Override
	void calcHePosition() throws AstroError {
		throw new AstroError("Cannot calculate heliocentric position of the Sun");
	}


	/**
	 * Calculate the right ascension and declination of the Sun for
	 * the currently configured time -- fine method.  Results are stored
	 * in the cache as EC_LONGITUDE and EC_LATITUDE, for ecliptic co-ordinates,
	 * and RIGHT_ASCENSION and DECLINATION for equatorial co-ordinates.
	 *
	 * <p>From AA chapter 25.
	 * 
	 * @throws	AstroError		Invalid request.
	 */
	@Override
	void calcEcPosition() throws AstroError {
		// We need data for the Earth too.
		Planet earth = observation.getPlanet(Planet.Name.EARTH);
		
		// Get the heliocentric co-ordinates of the Earth, and the nutation
		// in longitude.
		double Lo = earth.get(Field.HE_LONGITUDE);
		double Bo = earth.get(Field.HE_LATITUDE);
		double Ro = earth.get(Field.HE_RADIUS);

		// Calculate the geocentric lon and lat of the Sun.
		double λ = Lo < PI ? Lo + PI : Lo - PI;
		double β = -Bo;
		
		// Convert to the FK5 system.
		double T = (observation.getTd() - J2000) / 36525.0;
		double T2 = T * T;
		double λ1 = λ - toRadians(1.397) * T - toRadians(0.00031) * T2;
		λ += secsToRads(-0.09033);
		β += secsToRads(0.03916) * (cos(λ1) - sin(λ1));
		
		// Correct for nutation and  aberration.
		// NOTE: Meeus says to add Δψ here; but it gets added when we
		// convert to equatorial co-ordinates.  Which is right?
		// Seems doing both nutation bits in the same place makes sense
		// (i.e. in eclipticToApparentEquatorial()).
		λ = λ - secsToRads(20.4898) / Ro;

		put(Field.EARTH_DISTANCE, Ro);
		put(Field.EC_LONGITUDE, λ);
		put(Field.EC_LATITUDE, β);
	}


	/**
	 * Calculate the phase of this body as seen from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * PHASE_ANGLE and PHASE.
	 */
	@Override
	void calcPhase() {
		// The Sun is always full.
		put(Field.PHASE_ANGLE, PI);
		put(Field.PHASE, 1.0);
	}


	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The observation this Sun belongs to.
	private Observation observation;

}

