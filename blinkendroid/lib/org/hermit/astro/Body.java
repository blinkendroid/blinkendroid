
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
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.log10;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import org.hermit.astro.Observation.OField;
import org.hermit.geo.Position;


/**
 * A celestial body in astronomical calculations; this class calculates
 * and caches all parameters relating to a specific body.  Derived
 * classes represent specific bodies or types of body.
 * 
 * <p>The public enum {@link Name} identifies a particular body.  It also contains
 * all the orbital elements and other static info for each body.
 * 
 * <p>Applications do not create instances of Body; they are obtained by
 * calling {@link Observation#getSun()}, {@link Observation#getMoon()} and
 * {@link Observation#getPlanet(Body.Name which)}.
 * 
 * <p>The core of this class is a database of all the data we have calculated
 * for this body.  Fields in the database are identified by enum {@link Field};
 * clients ask for a particular field by calling {@link #get(Field)}.  All field
 * values are doubles.  Each member of Field has a pointer to the
 * calculation method which calculates the value of that field (and
 * maybe others); if a value is requested which is not in the database,
 * the calculation method is called automatically.  Hence the database
 * acts as a cache of computed data for the body.
 * 
 * <p>The cache must be invalidated, by calling invalidate(), if any of the
 * circumstances of the current observation (such as time) changes.  This
 * is generally done by the controlling {@link Observation} automatically.
 * 
 * <p>Since there may be multiple Observations in existence at one time,
 * there may well be multiple versions of each Body floating around, since
 * a Body -- and its associated cached data -- is associated with the
 * set of circumstances in a particular Observation.  So be sure you keep
 * straight which Sun, for example, is which.
 * 
 * <p>Note that we depart from the usual Java naming conventions here.  To
 * simplify reference to source materials, variables are named according
 * to the original source text, Greek letters included.  So, be careful
 * when looking at names; "Χ" may be the Greek Chi, rather than Roman.
 *
 * @author	Ian Cameron Smith
 */
public abstract class Body
	implements AstroConstants
{
	
	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

	/**
	 * This enumeration defines the celestial bodies we know about.  Each
	 * member of the enum also contains the following information for the body:
	 * <ul>
	 * 	<li>Name
	 * 	<li>Symbol (note not all fonts, e.g. on Android, have all the syms)
	 * 	<li>Periodic terms, for planets
	 * 	<li>Diameter in arcseconds at 1 AU (not semi-diameter!)
	 * 	<li>Base factor in the calculation of magnitude; from AA chapter
	 * 	    41.  Note we use the "since 1984" table.
	 * </ul>
	 * 
	 * <p>Note that not all parameters are relevant to all objects.  The
	 * Moon's diameter and magnitude are calculated in a special way.
	 * 
	 * <p>Note: for Jupiter and Saturn, the apparent diameter and hence
	 * magnitude depend on the angle at which they present themselves
	 * to the Earth.  We ignore this since we only need a rough magnitude.
	 * 
	 * <p>NOTE: the angular data in the definitions is presented in the units
	 * noted.  However we convert to RADIANS for the stored values.
	 */
	public enum Name {
		//		 Name      Sym  Terms           Diam		Mag
		/** The Sun. */
		SUN(	"Sun", 	   '☉', null, 			1919.26,   -26.74),
		/** The Moon. */
		MOON(	"Moon",    '☾', null, 			   0.00,	 0.00),
		/** Mercury. */
		MERCURY("Mercury", '☿', Vsop87.MERCURY,    6.74,	-0.42),
		/** Venus. */
		VENUS(	"Venus",   '♀', Vsop87.VENUS, 	  16.82,	-4.40),
		/** The Earth. */
		EARTH(	"Earth",   '♁', Vsop87.EARTH, 	   0.00,	 0.00),
		/** Mars. */
		MARS(	"Mars",    '♂', Vsop87.MARS, 	   9.36,	-1.52),
		/** Jupiter. */
		JUPITER("Jupiter", '♃', Vsop87.JUPITER,  196.74,	-9.40),
		/** Saturn. */
		SATURN(	"Saturn",  '♄', Vsop87.SATURN, 	 165.6,		-8.88),
		/** Uranus. */
		URANUS(	"Uranus",  '♅', Vsop87.URANUS, 	  68.56,	-7.19),
		/** Neptune. */
		NEPTUNE("Neptune", '♆', Vsop87.NEPTUNE,   73.12,	-6.87);
		
		Name(String n, char sym, Vsop87 v, double d, double m) {
			name = n;
			symbol = sym;
			terms = v;
			θ_o = toRadians(d / 60 / 60);
			V_o = m;
		}
		
		/** Name of this body. */
		public final String name;
		
		/** Symbol of this body. */
		public final char symbol;
		
		/** VSOP Periodic terms; null for the Sun and Moon. */
		public final Vsop87 terms;
		
		/** Apparent diameter in radians at 1 AU (not semi-diameter!). */
		public final double θ_o;
		
		/** Base factor for magnitude, from AA chapter 41. */
		public final double V_o;
	}

    /**
     * The names of all the celestial bodies we have information on.
     */
    public static final Name[] ALL_BODIES = Name.values();

	/**
	 * The number of celestial bodies we have information on.
	 */
	public static final int NUM_BODIES = ALL_BODIES.length;
	

	// Class embodying a calculate method which works out one or more fields.
	private abstract static class Calc {
		abstract void c(Body b) throws AstroError;
	}
	
	
	/**
	 * This enumeration defines the data fields that are stored for
	 * each body.
	 */
	public enum Field {
		/** The heliocentric latitude of the body, in radians. */
		HE_LATITUDE,
		
		/** The heliocentric longitude of the body, in radians. */
		HE_LONGITUDE,
		
		/** The heliocentric radius of the body, in AU. */
		HE_RADIUS,
		
		/** The ecliptic longitude of the body, in radians. */
		EC_LONGITUDE,
		
		/** The ecliptic latitude of the body, in radians. */
		EC_LATITUDE,
		
		/** The right ascension of the body, in radians. */
		RIGHT_ASCENSION_AP,
		
		/** The declination of the body, in radians. */
		DECLINATION_AP,
		
		/**
		 * The equatorial horizontal parallax, in radians.
		 */
		HORIZ_PARALLAX,
		
		/**
		 * The topocentric right ascension of the body, in radians.
		 * This is RIGHT_ASCENSION_AP corrected for parallax.
		 */
		RIGHT_ASCENSION_TOPO,
		
		/**
		 * The topocentric declination of the body, in radians.
		 * This is DECLINATION_AP corrected for parallax.
		 */
		DECLINATION_TOPO,
		
		/**
		 * The azimuth of the body from the observer, in radians, 0=north.
		 */
		LOCAL_AZIMUTH,
		
		/**
		 * The altitude of the body from the observer, in radians.
		 */
		LOCAL_ALTITUDE,
		
		/** The local hour angle of the body from the observer, in radians. */
		LOCAL_HOUR_ANGLE,
		
		/** The distance of the body from the Earth, in AU. */
		EARTH_DISTANCE,
		
		/**
		 * The apparent diameter of the body from the observer's position,
		 * in radians.
		 */
		APPARENT_DIAMETER,
		
		/**
		 * Time at which nautical twilight begins before sunrise (SUN only).
		 */
		RISE_TWILIGHT,
        
        /**
         * Rise time.
         */
		RISE_TIME,
        
        /**
         * Transit time.
         */
		TRANSIT_TIME,
        
        /**
         * Set time.
         */
		SET_TIME,
        
        /**
         * Time at which nautical twilight ends after sunset (SUN only).
         */
		SET_TWILIGHT,
		
		/**
		 * The phase angle, i.e. Sun-Body_Earth angle, in radians.
		 */
		PHASE_ANGLE,
		
		/**
		 * The phase, as the fraction (0-1) of the disc which is illuminated.
		 * This fraction applies to both area and diameter.
		 */
		PHASE,
		
		/**
		 * Parallactic angle of this body as seen from the Earth.
		 */
		PARALLACTIC,
		
		/**
	     * The position angle of the bright limb of this body from North.
	     */
		ABS_BRIGHT_LIMB,
        
        /**
         * The position angle of the bright limb of this body
         * as seen from Earth.
         */
		OBS_BRIGHT_LIMB,
        
        /**
         * The magnitude of this body as seen from Earth.
         */
		MAGNITUDE;

		private static void register(Field field, Calc calc) {
			if (field.calculator != null)
				throw new RuntimeException("Field " + field +
										   " already has a calculator");
			field.calculator = calc;
		}
		
		private void calculate(Body b) throws AstroError {
			if (calculator == null)
				throw new RuntimeException("Field " + this + " has no calculator");
			calculator.c(b);
		}
		
		private Calc calculator = null;
	}
	
	private static final Field[] ALL_FIELDS = Field.values();
	
	private static final int NUM_FIELDS = ALL_FIELDS.length;

	
	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

	/**
	 * Create a Body.  This method is only called from subclasses, and
	 * then only by {@link Observation}.
	 * 
	 * @param	o			The Observation this Body belongs to.  This
	 * 						contains all global configuration, like the
	 * 						current time.
	 * @param	which		Which body this is.
	 */
	protected Body(Observation o, Name which) {
		observation = o;
		whichBody = which;
		
		// Create the data cache.
		dataCache = new Double[NUM_FIELDS];
		invalidate();
	}


    // ******************************************************************** //
	// Body Information.
	// ******************************************************************** //
	
	/**
	 * Get this body's identifier.
	 * 
	 * @return				Which body this is.
	 */
	public Name getId() {
		return whichBody;
	}
	
	
	/**
	 * Get this body's name.
	 * 
	 * @return				The name of this body.
	 */
	public String getName() {
		return whichBody.name;
	}
	
	
	/**
	 * Get the value of one of the data fields of this body.
	 * 
	 * @param	key			The field we want.
	 * @return				The field value.
	 * @throws	AstroError	The request was invalid.
	 */
	public double get(Field key) throws AstroError {
		if (dataCache[key.ordinal()] == null)
			key.calculate(this);
		
		// Get the value.  It has to be there now.
		Double val = dataCache[key.ordinal()];
		if (val == null)
			throw new CalcError("Calculator for field " + key + " failed");
		
		return val;
	}
	

    // ******************************************************************** //
	// Data Calculation.
	// ******************************************************************** //

	/**
	 * Calculate the heliocentric co-ordinates of the body for the currently
	 * configured time.  Result is stored in the cache as HE_LATITUDE,
	 * HE_LONGITUDE and HE_RADIUS.
	 */
	static {
		Calc calcHe = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcHePosition(); }
		};
		Field.register(Field.HE_LATITUDE, calcHe);
		Field.register(Field.HE_LONGITUDE, calcHe);
		Field.register(Field.HE_RADIUS, calcHe);
	}
	abstract void calcHePosition() throws AstroError;


	/**
	 * Calculate the distance in AU, and the ecliptic longitude and
	 * latitude of the body for the currently configured time.
	 * Results are stored in the cache as EARTH_DISTANCE, EC_LONGITUDE
	 * and EC_LATITUDE.
	 */
	static {
		Calc calcEc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcEcPosition(); }
		};
		Field.register(Field.EARTH_DISTANCE, calcEc);
		Field.register(Field.EC_LONGITUDE, calcEc);
		Field.register(Field.EC_LATITUDE, calcEc);
	}
	abstract void calcEcPosition() throws AstroError;


	/**
	 * Calculate the apparent right ascension and
	 * declination of the body for the currently configured time.
	 * Results are stored in the cache as RIGHT_ASCENSION_AP
	 * and DECLINATION_AP.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcEqPosition(); }
		};
		Field.register(Field.RIGHT_ASCENSION_AP, calc);
		Field.register(Field.DECLINATION_AP, calc);
	}
	void calcEqPosition() throws AstroError {
		if (whichBody == Name.EARTH)
			throw new AstroError("Cannot calculate RA and Dec of the Earth");
		
		double λ = get(Field.EC_LONGITUDE);
		double β = get(Field.EC_LATITUDE);
		
		// Now convert to apparent equatorial co-ordinates -- in other
		// words, taking nutation into account.
		double[] pos = new double[2];
		observation.eclipticToApparentEquatorial(λ, β, pos);
		double α = pos[0];
		double δ = pos[1];
	
		put(Field.RIGHT_ASCENSION_AP, α);
		put(Field.DECLINATION_AP, δ);
	}


	/**
	 * Calculate the parallax and topocentric co-ordinates of the
	 * body for the currently configured time.
	 * Results are stored in the cache as RIGHT_ASCENSION_AP
	 * and DECLINATION_AP.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcParallax(); }
		};
		Field.register(Field.HORIZ_PARALLAX, calc);
		Field.register(Field.RIGHT_ASCENSION_TOPO, calc);
		Field.register(Field.DECLINATION_TOPO, calc);
	}
	void calcParallax() throws AstroError {
		if (whichBody == Name.EARTH)
			throw new AstroError("Cannot calculate topocentric position of the Earth");
		
		double α = get(Field.RIGHT_ASCENSION_AP);
		double δ = get(Field.DECLINATION_AP);
		double Δ = get(Field.EARTH_DISTANCE);
		double H = get(Field.LOCAL_HOUR_ANGLE);
	
		double ρsinφ1 = observation.get(OField.RHO_SIN_PHI1);
		double ρcosφ1 = observation.get(OField.RHO_COS_PHI1);
		
		// Calculate the equatorial horizontal parallax in radians.
		double sinπ = 0.0000426345 / Δ;
		double π = asin(sinπ);
		
		// Calculate the adjusted right ascension.
		double y1 = -ρcosφ1 * sinπ * sin(H);
		double x = cos(δ) - ρcosφ1 * sinπ * cos(H);
		double Δα = atan2(y1, x);
		double α1 = α + Δα;
		
		// Calculate the adjusted declination.
		double y2 = (sin(δ) - ρsinφ1 * sinπ) * cos(Δα);
		double δ1 = atan2(y2, x);
		
		put(Field.HORIZ_PARALLAX, π);
		put(Field.RIGHT_ASCENSION_TOPO, α1);
		put(Field.DECLINATION_TOPO, δ1);
	}
	

	/**
	 * Calculate the local hour angle of this body for
	 * the currently configured time.  Results are stored
	 * in the cache as LOCAL_HOUR_ANGLE.
	 * 
	 * <p>From AA chapter 13.
	 */
	static {
		Field.register(Field.LOCAL_HOUR_ANGLE, new Calc() {
			@Override void c(Body b) throws AstroError { b.calcHourAngle(); }
		});
	}
	void calcHourAngle() throws AstroError {
		double α = get(Field.RIGHT_ASCENSION_AP);
		double θo = observation.get(Observation.OField.GAST_INSTANT);
		double L = observation.getObserverPosition().getLonRads();

		// Convert the GST to radians.
		double θor = toRadians(θo * 15.0);
		
		// Get the local hour angle.  Note longitude is positive East,
		// contrary to Meeus.
		double H = modTwoPi(θor + L - α);
		put(Field.LOCAL_HOUR_ANGLE, H);
	}

	
	/**
	 * Calculate the local horizontal co-ordinates -- the altitude and
	 * azimuth -- of this body for the currently configured time.  Results
	 * are stored in the cache as LOCAL_AZIMUTH and LOCAL_ALTITUDE.
	 * 
	 * <p>Note that the azimuth is measured eastwards from north.
	 */
	static {
		Calc calcAltAz = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcAltAzimuth(); }
		};
		Field.register(Field.LOCAL_AZIMUTH, calcAltAz);
		Field.register(Field.LOCAL_ALTITUDE, calcAltAz);
	}
	void calcAltAzimuth() throws AstroError {
		double δ = get(Field.DECLINATION_AP);
		double H = get(Field.LOCAL_HOUR_ANGLE);
		double π = get(Field.HORIZ_PARALLAX);
		
		Position pos = observation.getObserverPosition();
		double φ = pos.getLatRads();

		double Ay = sin(H);
		double Ax = cos(H) * sin(φ) - tan(δ) * cos(φ);
		double A = atan2(Ay, Ax);
		
		// Make azimuth north-based.
		A = (A + PI) % TWOPI;
		
		// Calculate the "local geocentric" altitude.
		double sinh = sin(φ) * sin(δ) + cos(φ) * cos(δ) * cos(H);
		double h = asin(sinh);
		
		// Calculate the parallax in altitude (the parallax of azimuth is
		// very small).
		double ρ = pos.getCentreDistance();
		double p = asin(ρ * sin(π) * cos(h));
		
		put(Field.LOCAL_AZIMUTH, A);
		put(Field.LOCAL_ALTITUDE, h - p);
	}
	

	/**
	 * Calculate the apparent diameter of this body from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * APPARENT_DIAMETER.
	 * 
	 * <p>Note: the routine here works for the Sun and planets; however,
	 * for accuracy, we should calculate the angle of the rings of Saturn.
	 * We don't, because we just want a rough magnitude.
	 *
	 * <p>From AA chapter 55.
	 */
	static {
		Field.register(Field.APPARENT_DIAMETER, new Calc() {
			@Override void c(Body b) throws AstroError { b.calcApparentSize(); }
		});
	}
	void calcApparentSize() throws AstroError {
		if (whichBody == Name.EARTH)
			throw new AstroError("Cannot calculate the apparent size of the Earth");
		if (whichBody == Name.MOON)
			throw new CalcError("calcApparentSize must be overridden for the Moon");
		
		double Δ = get(Field.EARTH_DISTANCE);
		
		// Calculate the angular diameter as seen from Earth.
		double θ = whichBody.θ_o / Δ;
		
		put(Field.APPARENT_DIAMETER, θ);
	}


	/**
	 * Calculate the rise and set times of this body for
	 * the currently configured day.  The UT rise and set times in decimal
	 * hours are stored in the cache as RISE_TIME and SET_TIME; the
	 * azimuths of the rise and set points in radians are stored as
	 * RISE_AZIMUTH and SET_AZIMUTH.
	 * 
	 * <p>From AA chapter 15.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcRiseSet(); }
		};
		Field.register(Field.RISE_TIME, calc);
		Field.register(Field.SET_TIME, calc);
		Field.register(Field.RISE_TWILIGHT, calc);
		Field.register(Field.SET_TWILIGHT, calc);
	}
	void calcRiseSet() throws AstroError {
		if (whichBody == Name.EARTH)
			throw new AstroError("Cannot calculate rise/set for the Earth");
		
		// Figure out the standard altitude for this body's rise or set, h_o.
		// If this is the Moon, calculate h_o based on parallax.  Otherwise,
		// h_o is refraction plus the semi-diameter, in radians.
		// Note that θ_o is the full diameter.
		double h_o;
		if (whichBody == Name.MOON) {
			double π = get(Field.HORIZ_PARALLAX);
			h_o = 0.7275 * π - REFRACTION;
		} else
			h_o = -(REFRACTION + whichBody.θ_o / 2);
		
		// Get the observer's position.  NOTE: my L is positive east,
		// the opposite to Astronomical Algorithms.
		Position pos = observation.getObserverPosition();
		double φ = pos.getLatRads();
		double L = pos.getLonRads();

		// We need the apparent sidereal time for midnight UT, in radians.
		// NOTE: this is midnight Universal Time!
		double Θ0 = observation.get(Observation.OField.GAST_MIDNIGHT) * 15.0;
		Θ0 = toRadians(Θ0);

		// Calculate the Julian day number.  We need observations of the
		// body's positions for jday-1, jday, and jday+1 at 0h TD.
		// NOTE: these times are midnight Dynamical Time!
		Instant when = observation.getTime();
		double ΔT = when.getΔT();
		double jday = round(when.getTd() + 0.5) - 0.5;

		Observation o1 = new Observation(Instant.fromTd(jday - 1));
		Body b1 = o1.getBody(whichBody);
		Observation o2 = new Observation(Instant.fromTd(jday));
		Body b2 = o2.getBody(whichBody);
		Observation o3 = new Observation(Instant.fromTd(jday + 1));
		Body b3 = o3.getBody(whichBody);

		// Get this body's position on each day.
		double α1 = b1.get(Field.RIGHT_ASCENSION_AP);
		double δ1 = b1.get(Field.DECLINATION_AP);
		double α2 = b2.get(Field.RIGHT_ASCENSION_AP);
		double δ2 = b2.get(Field.DECLINATION_AP);
		double α3 = b3.get(Field.RIGHT_ASCENSION_AP);
		double δ3 = b3.get(Field.DECLINATION_AP);
		double[] αn = { α1, α2, α3 };
		double[] δn = { δ1, δ2, δ3 };
		
		// OK!  First, calculate approximate time.
		double cosH0 = (sin(h_o) - sin(φ) * sin(δ2)) / (cos(φ) * cos(δ2));
		if (cosH0  < -1 || cosH0 > 1)
			;  // FIXME: signal no rise or set!
		double H0 = acos(cosH0) % PI;
		if (H0 < 0)
			H0 += PI;

		// Calculate transit, rise and set.  These are in fractions of a day.
		// Note the reversed sign of L relative to Meeus.
		double transit = ((α2 - L - Θ0) / TWOPI) % 1.0;
		if (transit < 0)
			transit += 1.0;
		double rise = (transit - H0 / TWOPI) % 1.0;
		if (rise < 0)
			rise += 1.0;
		double set = (transit + H0 / TWOPI) % 1.0;
		if (set < 0)
			set += 1.0;

		// Refine the values by interpolation.
		transit = refineRiseSet(h_o, Θ0, φ, L, ΔT, αn, null, transit) * 24.0;
		rise = refineRiseSet(h_o, Θ0, φ, L, ΔT, αn, δn, rise) * 24.0;
		set = refineRiseSet(h_o, Θ0, φ, L, ΔT, αn, δn, set) * 24.0;

		put(Field.RISE_TIME, rise);
		put(Field.TRANSIT_TIME, transit);
		put(Field.SET_TIME, set);
		
		// Now, if this is the Sun, calculate the times of twilight.
		if (whichBody == Name.SUN) {
			// Calculate the duration of twilight.
			double t = calculateTwilight(Θ0, φ, L, ΔT, αn, δn,
										 set / 24.0, HALFPI + TWILIGHT);
			put(Field.RISE_TWILIGHT, rise - t);
			put(Field.SET_TWILIGHT, set + t);
		}
	}


	private double refineRiseSet(double h_o, double Θ0, double φ, double L,
								 double ΔT, double[] αn, double[] δn, double m)
	{
		final double[] args = { -1, 0, 1 };
		
		double Θ0_now = modTwoPi(Θ0 + TWOPI * SIDEREAL_RATIO * m);
		double n = m + ΔT / 86400;
		
		// Interpolate for α, and calculate the local hour angle.  Note
		// the reversed sign of L.
		double α = Util.interpolate(args, αn, n);
		double H = Θ0_now + L - α;
		
		// Interpolate for δ, if required.
		double Δm;
		if (δn != null) {
			double δ = Util.interpolate(args, δn, n);
			double sinh = sin(φ) * sin(δ) + cos(φ) * cos(δ) * cos(H);
			double h = asin(sinh);
			Δm = (h - h_o) / (TWOPI * cos(δ) * cos(φ) * sin(H));
		} else
			Δm = -H / TWOPI;

		return m + Δm;
	}
	
	
	private double calculateTwilight(double Θ0, double φ, double L, double ΔT,
			 					     double[] αn, double[] δn, double m, double z)
	{
		double Θ0_now = modTwoPi(Θ0 + TWOPI * SIDEREAL_RATIO * m);
		double n = m + ΔT / 86400;
		
		// Interpolate for α, and calculate the local hour angle.  Note
		// the reversed sign of L.
		final double[] args = { -1, 0, 1 };
		double α = Util.interpolate(args, αn, n);
		double δ = Util.interpolate(args, δn, n);
		double H = Θ0_now + L - α;

		// Convert these to the hour angles of twilight -- TWILIGHT below the
		// horizon.  Then calculate the duration of twilight in hours.
		double cosH1 = (cos(z) - sin(φ) * sin(δ)) / (cos(φ) * cos(δ));
		if (cosH1  < -1 || cosH1 > 1)
			;  // FIXME: signal no twilight!
		double H1 = acos(cosH1);
		double t = toDegrees(H1 - H) / 15.0;
		
		return t;
	}
	

	/**
	 * Calculate the phase of this body as seen from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * PHASE_ANGLE and PHASE.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcPhase(); }
		};
		Field.register(Field.PHASE_ANGLE, calc);
		Field.register(Field.PHASE, calc);
	}
	abstract void calcPhase() throws AstroError;


	/**
	 * Calculate the parallactic angle of this body as
	 * seen from the Earth for the currently configured time.  This is
	 * the angle from the zenith point ("top" of the object as seen by
	 * us) to the North point (direction from the body to the north
	 * celestial pole).  The result is stored in the cache as
	 * PARALLACTIC.
	 * 
	 * <p>If the value is undefined, i.e. if the object is in the
	 * zenith, then zero will be saved.
	 * 
	 * <p>From AA chapter 14.
	 */
	static {
		Field.register(Field.PARALLACTIC, new Calc() {
			@Override void c(Body b) throws AstroError { b.calcParallactic(); }
		});
	}
	void calcParallactic() throws AstroError {
		double H = get(Field.LOCAL_HOUR_ANGLE);
		Position pos = observation.getObserverPosition();
		double φ = pos.getLatRads();
		double δ = get(Field.DECLINATION_AP);

		double y = sin(H);
		double x = tan(φ) * cos(δ) - sin(δ) * cos(H);
		double q = x == 0 ? 0 : atan2(y, x);
		
		put(Field.PARALLACTIC, q);
	}


	/**
	 * Calculate the position angle of the bright limb of this body
	 * from North and as seen from Earth for the currently configured
	 * time.  Results are stored in the cache as ABS_BRIGHT_LIMB and
	 * OBS_BRIGHT_LIMB.
	 *
	 * <p>From AA chapter 48.
	 */
	static {
		Calc calc = new Calc() {
			@Override void c(Body b) throws AstroError { b.calcBrightLimb(); }
		};
		Field.register(Field.ABS_BRIGHT_LIMB, calc);
		Field.register(Field.OBS_BRIGHT_LIMB, calc);
	}
	void calcBrightLimb() throws AstroError {
		if (whichBody == Name.EARTH || whichBody == Name.SUN)
			throw new AstroError("Cannot calculate rise/set for the " +
							   whichBody.name);
		
		double α = get(Field.RIGHT_ASCENSION_AP);
		double δ = get(Field.DECLINATION_AP);
		double q = get(Field.PARALLACTIC);
		
		// We need data for the Sun too.
		Sun sun = observation.getSun();
		double αo = sun.get(Field.RIGHT_ASCENSION_AP);
		double δo = sun.get(Field.DECLINATION_AP);
		
		// Calculate the angle from north of the bright limb.
		double Δα = αo - α;
		double y = cos(δo) * sin(Δα);
		double x = sin(δo) * cos(δ) - cos(δo) * sin(δ) * cos(Δα);
		double Χ = modTwoPi(Math.atan2(y, x));
		
		// Calculate the position as seen by the observer.
		double obs = modTwoPi(Χ - q);

		put(Field.ABS_BRIGHT_LIMB, Χ);
		put(Field.OBS_BRIGHT_LIMB, obs);
	}


	/**
	 * Calculate the magnitude of this body as
	 * seen from the Earth for the currently configured time.  Results
	 * are stored in the cache as MAGNITUDE.
	 *
	 * From AA chapter 41.
	 */
	static {
		Field.register(Field.MAGNITUDE, new Calc() {
			@Override void c(Body b) throws AstroError { b.calcMagnitude(); }
		});
	}
	void calcMagnitude() throws AstroError {
		if (whichBody == Name.EARTH)
			throw new AstroError("Cannot calculate the magnitude of the Earth");
		
		double Δ = get(Field.EARTH_DISTANCE);
		double i = get(Field.PHASE_ANGLE);
		
		// We want i in degrees, and its powers.
		i = toDegrees(i);
		double i2 = i * i;
		double i3 = i2 * i;

		// Get the distance from the Sun... for the Moon, I don't know how
		// to calculate this, so use the Earth's.
		double r;
		if (whichBody == Name.SUN)
			r = 0.0;
		else if (whichBody == Name.MOON) {
			Planet earth = observation.getPlanet(Planet.Name.EARTH);
			r = earth.get(Field.HE_RADIUS);
		} else
			r = get(Field.HE_RADIUS);
		
		// Get the base magnitude.
		double mag = whichBody.V_o + (r == 0.0 ? 0.0 : 5 * log10(r * Δ));
		switch (whichBody) {
		case MOON:
			break;
		case MERCURY:
			mag += 0.0380 * i - 0.000273 * i2 + 0.000002 * i3;
			break;
		case VENUS:
			mag += 0.0009 * i + 0.000239 * i2 - 0.00000065 * i3;
			break;
		case MARS:
			mag += 0.016 * i;
			break;
		case JUPITER:
			mag += 0.005 * i;
			break;
		case SATURN:
			// Note: this is a major approximation.
			mag += 0.044 * abs(i);
			break;
		case SUN:
		case EARTH:
		case URANUS:
		case NEPTUNE:
			break;
		}
		
		put(Field.MAGNITUDE, mag);
	}


    // ******************************************************************** //
	// Global Utilities.
	// ******************************************************************** //

	/**
	 * Iterative method to solve Kepler's equation.  This will complete in
	 * a couple of iterations for for values of e <~ 0.1; larger values
	 * (such as for Pluto, or comets) will take longer.
	 * 
	 * From section 47, Calculating orbits more precisely, routine R2.
	 * 
	 * @param	M			Mean anomaly of the body in radians.
	 * @param	e			Eccentricity of the orbit.
	 * @return				The eccentric anomaly in radians.
	 */
	static double kepler(double M, double e) {
		// Solve E - e * sin(E) = M.
		// First guess at the value: M.
		double E = M;

		int iterations = 0;
		while (true) {
			double δ = E - e * sin(E) - M;
			if (abs(δ) <= KEPLER_TOLERANCE)
				break;
			if (++iterations > KEPLER_MAX_ITER)
				throw new CalcError("Too many iterations in kepler, e=" + e);
			double ΔE = δ / (1 - e * cos(E));
			E -= ΔE;
		}
		
		return E;
	}
	
	
	/**
	 * Calculate the effect of aberration on a body, based on its ecliptical
	 * co-ordinates.
	 *
	 * <p>From AA chapter 23.
	 * 
	 * @param	td			The JDE, i.e. JD in TD, for the observation.
	 * @param	λ			The ecliptic longitude, in radians.
	 * @param	β			The ecliptic latitude, in radians.
	 * @param	Lsun		The true (geometric) longitude of the Sun.
	 * @param	pos			An array { Δλ, Δβ } in which the adjustments
	 * 						for aberration in longitude and latitude in
	 * 						radians will be placed.
	 */
	static void aberrationEc(double td, double λ, double β, double Lsun, double[] pos) {
		// Calculate the eccentricity and longitude of the perihelion of
		// the Earth's orbit.
		double T = (td - J2000) / 36525;
		double T2 = T * T;
		double e = 0.016708634 - 0.000042037 * T - 0.0000001267 * T2;
		double π = 102.93735 + 1.71946 * T + 0.00046 * T2;
		π = toRadians(π);
		
		double Δλ = (-ABERRATION * cos(Lsun - λ) + e * ABERRATION * cos(π - λ)) / cos(β);
		double Δβ = -ABERRATION * sin(β) * (sin(Lsun - λ) - e * sin(π - λ));
		
		pos[0] = Δλ;
		pos[1] = Δβ;
	}
	
	
	/**
	 * Return the given value mod PI, with negative values made positive --
	 * in other words, the value put into the range [0 .. PI).
	 * 
	 * @param	v			Input value.
	 * @return				v % PI, plus PI if negative.
	 */
	static final double modPi(double v) {
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
	static final double modTwoPi(double v) {
		v %= TWOPI;
		return v < 0 ? v + TWOPI : v;
	}
	
	
	/**
	 * Return the given value 360.0, with negative values made positive --
	 * in other words, the value put into the range [0 .. 360.0).
	 * 
	 * @param	v			Input value.
	 * @return				v % 360.0, plus 360.0 if negative.
	 */
	static final double mod360(double v) {
		v %= 360.0;
		return v < 0 ? v + 360.0 : v;
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
	// Cache Management.
	// ******************************************************************** //
	
	/**
	 * Save a specified value in the data cache.
	 * 
	 * @param	key			The name of the value to save.
	 * @param	val			The value.
	 */
	protected void put(Field key, Double val) {
		dataCache[key.ordinal()] = val;
	}
	
	
	/**
	 * Invalidate the data cache.
	 */
	protected void invalidate() {
	    for (int i = 0; i < NUM_FIELDS; ++i)
	        dataCache[i] = null;
	}
	
	
	// ******************************************************************** //
	// Class Data.
	// ******************************************************************** //
    
	// The tolerance allowed in the answer when solving Kepler's equation.
	private static final double KEPLER_TOLERANCE = 10E-6;
	
	// Maximum number of iterations allowed in the Kepler function.
	private static final int KEPLER_MAX_ITER = 10;


	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The observation this body belongs to.
	private Observation observation;
	
	// Which body this is.  This gives us access to all its orbital data.
	private final Name whichBody;

	// Cache of values calculated for this body at the currently
	// configured date / time.
	private Double[] dataCache;

}

