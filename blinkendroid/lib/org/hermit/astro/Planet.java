	
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
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


/**
 * This class represents a planet, and provides all known information
 * about it.
 * 
 * This subclass of {@link Body} basically provides custom calculation
 * routines relevant to planets.
 * 
 * Note that we depart from the usual Java naming conventions here.  To
 * simplify reference to source materials, variables are named according
 * to the original source text, Greek letters included.  So, be careful
 * when looking at names; "Χ" may be the Greek Chi, rather than Roman.
 */
public class Planet
	extends Body
{

	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //
	
	/**
	 * Create a Planet.  Note that this constructor is non-public; the
	 * only way to get a Planet is by calling Observation.getPlanet().
	 * 
	 * @param	o			The Observation this Planet belongs to.  This
	 * 						contains all global configuration, like the
	 * 						current time.
	 * @param	which		Which planet this is.
	 */
	Planet(Observation o, Name which) {
		super(o, which);
		
		observation = o;
		whichPlanet = which;
	}

 	   
    // ******************************************************************** //
	// Accessors.
	// ******************************************************************** //

	/**
	 * Determine whether this is an inner planet (i.e. orbits the Sun inside
	 * the Earth's orbit).
	 * 
	 * @return				true if this is an inner planet.
	 */
	public boolean isInner() {
		return whichPlanet == Name.MERCURY || whichPlanet == Name.VENUS;
	}
	

    // ******************************************************************** //
	// Data Calculation.
	// ******************************************************************** //

	/**
	 * Calculate the heliocentric co-ordinates of the body for the currently
	 * configured time.  Result is stored in the cache as HE_LATITUDE,
	 * HE_LONGITUDE and HE_RADIUS.
	 *
	 * <p>From AA chapter 32.
	 */
	@Override
	void calcHePosition() {
		// Calculate the Julian centuries elapsed since J2000 in
		// dynamical time.  We also need the time in millenia.
		double T = (observation.getTd() - J2000) / 36525;
		double Tm = T / 10.0;
		
		// Calculate the heliocentric ecliptical longitude in radians.
		double L = whichPlanet.terms.calculateL(Tm) % TWOPI;
		if (L < 0)
			L += TWOPI;

		// Calculate the heliocentric latitude in radians.
		double B = whichPlanet.terms.calculateB(Tm) % PI;
		if (B < -HALFPI)
			B += PI;

		// Calculate the radius vector.
		double R = whichPlanet.terms.calculateR(Tm);
		
		put(Field.HE_LONGITUDE, L);
		put(Field.HE_LATITUDE, B);
		put(Field.HE_RADIUS, R);
	}


	/**
	 * Calculate the distance in AU, and the ecliptic longitude and
	 * latitude of the planet for the currently configured time.
	 * Results are stored in the cache as EARTH_DISTANCE, EC_LONGITUDE
	 * and EC_LATITUDE.
	 *
	 * From AA chapter 33.
	 * 
	 * @throws	AstroError			Something went wrong.
	 */
	@Override
	void calcEcPosition() throws AstroError {
		if (whichPlanet == Planet.Name.EARTH)
			throw new AstroError("Can't calculate the ecliptic position of the Earth");
		
		// We need data for the Earth too.
		Planet earth = observation.getPlanet(Planet.Name.EARTH);
		
		// Get the heliocentric co-ordinates of this planet,
		// and of the Earth.
		double L = get(Field.HE_LONGITUDE);
		double B = get(Field.HE_LATITUDE);
		double R = get(Field.HE_RADIUS);
		double Lo = earth.get(Field.HE_LONGITUDE);
		double Bo = earth.get(Field.HE_LATITUDE);
		double Ro = earth.get(Field.HE_RADIUS);

		double x = R * cos(B) * cos(L) - Ro * cos(Bo) * cos(Lo);
		double y = R * cos(B) * sin(L) - Ro * cos(Bo) * sin(Lo);
		double z = R * sin(B) - Ro * sin(Bo);

		// Calculate the distance to the planet, and its light-time in days.
		double Δ = sqrt(x * x + y * y + z * z);
		double τ = 0.0057755183 * Δ;

		// OK, now correct for light-time.  Figure out where the planet
		// was at time now - τ.
		Instant then = Instant.fromTd(observation.getTd() - τ);
		Observation o = new Observation(then);
		Planet p = o.getPlanet(whichPlanet);
		L = p.get(Field.HE_LONGITUDE);
		B = p.get(Field.HE_LATITUDE);
		R = p.get(Field.HE_RADIUS);

		// Now re-do the rectangular co-ordinates.
		x = R * cos(B) * cos(L) - Ro * cos(Bo) * cos(Lo);
		y = R * cos(B) * sin(L) - Ro * cos(Bo) * sin(Lo);
		z = R * sin(B) - Ro * sin(Bo);
		Δ = sqrt(x * x + y * y + z * z);
		τ = 0.0057755183 * Δ;

		// Note: we could iterate until the change in Δ becomes insignificant,
		// but in reality two passes should do.

		// Calculate the geometric co-ordinates.
		double λ = modTwoPi(atan2(y, x));
		double β = atan2(z, sqrt(x * x + y * y));

		// Calculate and correct for the aberration.
		double[] pos = new double[2];
		double Lsun = Lo < PI ? Lo + PI : Lo - PI;
		aberrationEc(observation.getTd(), λ, β, Lsun, pos);
		λ = modTwoPi(λ + pos[0]);
		β += pos[1];
		
		// Now convert to the FK5 system.  This is a tiny correction,
		// but what the heck.
		observation.vsopToFk5(λ, β, pos);
		λ = pos[0];
		β = pos[1];
	
		put(Field.EARTH_DISTANCE, Δ);
		put(Field.EC_LONGITUDE, λ);
		put(Field.EC_LATITUDE, β);
	}


	/**
	 * Calculate the phase of this body as seen from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * PHASE_ANGLE and PHASE.
	 * 
	 * <p>From AA chapter 41.
	 * 
	 * @throws	AstroError		Invalid request.
	 */
	@Override
	void calcPhase() throws AstroError {
		if (whichPlanet == Name.EARTH)
			throw new AstroError("Can't calcluate the phase of the Earth");
		
		// We need data for the Sun too.
		Sun sun = observation.getSun();
		
		double r = get(Field.HE_RADIUS);
		double Δ = get(Field.EARTH_DISTANCE);
		double R = sun.get(Field.EARTH_DISTANCE);
	
		// Calculate the phase angle of the planet.
		double cosi = (r * r + Δ * Δ - R * R) / (2 * r * Δ);
		double i = acos(cosi);

		// Calculate the elongation of the Moon.
//		double rΔ = r + Δ;
//		double k = (rΔ * rΔ - R * R) / (4 * r * Δ);

		// And now the phase.
		double k = (1 + cosi) / 2;

		put(Field.PHASE_ANGLE, i);
		put(Field.PHASE, k);
	}


	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The observation this Planet belongs to.
	private Observation observation;
	
	// Which planet this is.  This gives us access to all its orbital data.
	private final Name whichPlanet;

}

