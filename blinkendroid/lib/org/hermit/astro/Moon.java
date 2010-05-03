
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

import static java.lang.Math.acos;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;


/**
 * This class represents the Moon, and provides all known information
 * about it.
 * 
 * This subclass of {@link Body} basically provides custom calculation
 * routines relevant to the Moon.
 * 
 * Note that we depart from the usual Java naming conventions here.  To
 * simplify reference to source materials, variables are named according
 * to the original source text, Greek letters included.  So, be careful
 * when looking at names; "Χ" may be the Greek Chi, rather than Roman.
 */
public class Moon
	extends Body
{

	// ******************************************************************** //
    // Constructors.
    // ******************************************************************** //
	
	/**
	 * Create a Moon.  Note that this constructor is non-public; the
	 * only way to get a Moon is by calling Observation.getSun().
	 * 
	 * @param	o			The Observation this Moon belongs to.  This contains
	 * 						all global configuration, like the current time.
	 */
	Moon(Observation o) {
		super(o, Name.MOON);
		
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
	 * @throws	AstroError		Invalid request.
	 */
	@Override
	void calcHePosition() throws AstroError {
		throw new AstroError("Cannot calculate heliocentric position of the Moon");
	}


	/**
	 * Calculate the distance in AU, and the ecliptic longitude and
	 * latitude of the planet for the currently configured time.
	 * Results are stored in the cache as EARTH_DISTANCE, EC_LONGITUDE
	 * and EC_LATITUDE.
	 *
	 * <p>From AA chapter 47.
	 */
	@Override
	void calcEcPosition() {
		// Calculate the Julian centuries elapsed since J2000 in
		// dynamical time.  We also need the time in millenia.
		double T = (observation.getTd() - J2000) / 36525;
		double T2 = T * T;
		double T3 = T2 * T;
		double T4 = T3 * T;

		// Moon's mean longitude, including the effect of light-time:
		double L1 = 218.3164477 + 481267.88123421 * T -
							0.0015786 * T2 + T3 / 538841 - T4 / 65194000;
		L1 = mod360(L1);
		
		// Mean elongation of the Moon:
		double D = 297.8501921 + 445267.1114034 * T -
							0.0018819 * T2 + T3 / 545868 - T4 / 113065000;
		D = mod360(D);
		
		// Sun's mean anomaly:
		double M = 357.5291092 + 35999.0502909 * T -
							0.0001536 * T2 + T3 / 24490000;
		M = mod360(M);
		
		// Moon's mean anomaly:
		double M1 = 134.9633964 + 477198.8675055 * T +
							0.0087414 * T2 + T3 / 69699 - T4 / 14712000;
		M1 = mod360(M1);
		
		// Moon's argument of latitude:
		double F = 93.2720950 + 483202.0175233 * T -
							0.0036539 * T2 - T3 / 3526000 + T4 / 863310000;
		F = mod360(F);
		
		// Auxiliary terms:
		double A1 = 119.75 + 131.849 * T;
		double A2 = 53.09 + 479264.290 * T;
		double A3 = 313.45 + 481266.484 * T;
		double E = 1 - 0.002516 * T - 0.0000074 * T2;
		double E2 = E * E;
		
		// Calculate the sums of the periodic terms for longitude and distance.
		double Σl = 0;
		double Σr = 0;
		for (int[] term : longDistTerms) {
			double a = term[0] * D + term[1] * M + 
					   term[2] * M1 + term[3] * F;
			if (term[1] == 2 || term[1] == -2) {
				Σl += E2 * term[4] * sin(toRadians(a)); 
				Σr += E2 * term[5] * cos(toRadians(a));
			} else if (term[1] == 1 || term[1] == -1) {
				Σl += E * term[4] * sin(toRadians(a)); 
				Σr += E * term[5] * cos(toRadians(a));
			} else {
				Σl += term[4] * sin(toRadians(a)); 
				Σr += term[5] * cos(toRadians(a));
			}
		}
		
		// Do the same for latitude.
		double Σb = 0;
		for (int[] term : latitudeTerms) {
			double a = term[0] * D + term[1] * M + 
					   term[2] * M1 + term[3] * F;
			if (term[1] == 2 || term[1] == -2)
				Σb += E2 * term[4] * sin(toRadians(a)); 
			else if (term[1] == 1 || term[1] == -1)
				Σb += E * term[4] * sin(toRadians(a)); 
			else
				Σb += term[4] * sin(toRadians(a)); 
		}
		
		// Now the additive terms for Jupiter, Venus, and the Earth's
		// flattening.
		Σl += 3958 * sin(toRadians(A1)) +
			  1962 * sin(toRadians(L1 - F)) +
			   318 * sin(toRadians(A2));
		Σb += -2235 * sin(toRadians(L1)) +
			    382 * sin(toRadians(A3)) +
			    175 * sin(toRadians(A1 - F)) +
			    175 * sin(toRadians(A1 + F)) +
			    127 * sin(toRadians(L1 - M1)) -
			    115 * sin(toRadians(L1 + M1));
		
		// And finally, calculate the co-ordinates.
		double Δ = (385000.56 + Σr / 1000.0) / AU;
		double λ = modTwoPi(toRadians(L1 + Σl / 1000000.0));
		double β = toRadians(Σb / 1000000.0);
				
		put(Field.EARTH_DISTANCE, Δ);
		put(Field.EC_LONGITUDE, λ);
		put(Field.EC_LATITUDE, β);
	}


	/**
	 * Calculate the apparent diameter of this body from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * APPARENT_DIAMETER.
	 *
	 * <p>From AA chapter 55.
	 */
	@Override
	void calcApparentSize() throws AstroError {
		double Δ = get(Field.EARTH_DISTANCE);
		double h = get(Field.LOCAL_ALTITUDE);
		double Δkm = Δ * AU;
		
		// Calculate the geocentric semidiameter in arcseconds.
		double s = 358473400.0 / Δkm;
	
		// Calculate the equatorial horizontal parallax, and
		// use this to get the topocentric semidiameter.
		double sinπ = 6378.14 / Δkm;
		double s1 = s * (1 + sin(h) * sinπ);
		
		// Convert to radians.
		s1 = toRadians(s1 / 3600.0);
		
		put(Field.APPARENT_DIAMETER, s1 * 2);
	}


	/**
	 * Calculate the phase of this body as seen from the Earth for the
	 * currently configured time.  Results are stored in the cache as
	 * PHASE_ANGLE and PHASE.
	 * 
	 * <p>From AA chapter 48.
	 * 
	 * @throws	AstroError		Invalid request.
	 */
	@Override
	void calcPhase() throws AstroError {
		// We need data for the Sun too.
		Sun sun = observation.getSun();
		
		double λ = get(Field.EC_LONGITUDE);
		double β = get(Field.EC_LATITUDE);
		double Δ = get(Field.EARTH_DISTANCE);
		double λo = sun.get(Field.EC_LONGITUDE);
		double R = sun.get(Field.EARTH_DISTANCE);
	
		// Calculate the elongation of the Moon.
		double cosψ = cos(β) * cos(λ - λo);
		double ψ = modPi(acos(cosψ));
		
		// Calculate the phase angle of the moon.
		double tani = (R * sin(ψ)) / (Δ - R * cosψ);
		double i = modPi(atan(tani));
		
		// And now the phase.
		double k = (1 + cos(i)) / 2;
			
		put(Field.PHASE_ANGLE, i);
		put(Field.PHASE, k);
	}


	// ******************************************************************** //
	// Class Data.
	// ******************************************************************** //

    // Debugging tag.
	@SuppressWarnings("unused")
	private static final String TAG = "onwatch";
    
    
	// Monster table of coefficients for the calculation of the longitude
	// and distance.
	private static final int[][] longDistTerms = { 
		{ 0,  0,  1,  0,    6288774,    -20905355 },
		{ 2,  0, -1,  0,    1274027,     -3699111 },
		{ 2,  0,  0,  0,     658314,     -2955968 },
		{ 0,  0,  2,  0,     213618,      -569925 },
		{ 0,  1,  0,  0,    -185116,        48888 },
		{ 0,  0,  0,  2,    -114332,        -3149 },
		{ 2,  0, -2,  0,      58793,       246158 },
		{ 2, -1, -1,  0,      57066,      -152138 },
		{ 2,  0,  1,  0,      53322,      -170733 },
		{ 2, -1,  0,  0,      45758,      -204586 },
		{ 0,  1, -1,  0,     -40923,      -129620 },
		{ 1,  0,  0,  0,     -34720,       108743 },
		{ 0,  1,  1,  0,     -30383,       104755 },
		{ 2,  0,  0, -2,      15327,        10321 },
		{ 0,  0,  1,  2,     -12528,            0 },
		{ 0,  0,  1, -2,      10980,        79661 },
		{ 4,  0, -1,  0,      10675,       -34782 },
		{ 0,  0,  3,  0,      10034,       -23210 },
		{ 4,  0, -2,  0,       8548,       -21636 },
		{ 2,  1, -1,  0,      -7888,        24208 },
		{ 2,  1,  0,  0,      -6766,        30824 },
		{ 1,  0, -1,  0,      -5163,        -8379 },
		{ 1,  1,  0,  0,       4987,       -16675 },
		{ 2, -1,  1,  0,       4036,       -12831 },
		{ 2,  0,  2,  0,       3994,       -10445 },
		{ 4,  0,  0,  0,       3861,       -11650 },
		{ 2,  0, -3,  0,       3665,        14403 },
		{ 0,  1, -2,  0,      -2689,        -7003 },
		{ 2,  0, -1,  2,      -2602,            0 }, 
		{ 2, -1, -2,  0,       2390,        10056 },
		{ 1,  0,  1,  0,      -2348,         6322 },
		{ 2, -2,  0,  0,       2236,        -9884 },
		{ 0,  1,  2,  0,      -2120,         5751 },
		{ 0,  2,  0,  0,      -2069,            0 },
		{ 2, -2, -1,  0,       2048,        -4950 },
		{ 2,  0,  1, -2,      -1773,         4130 },
		{ 2,  0,  0,  2,      -1595,            0 },
		{ 4, -1, -1,  0,       1215,        -3958 },
		{ 0,  0,  2,  2,      -1110,            0 },
		{ 3,  0, -1,  0,       -892,         3258 },
		{ 2,  1,  1,  0,       -810,         2616 },
		{ 4, -1, -2,  0,        759,        -1897 },
		{ 0,  2, -1,  0,       -713,        -2117 },
		{ 2,  2, -1,  0,       -700,         2354 },
		{ 2,  1, -2,  0,        691,            0 },
		{ 2, -1,  0, -2,        596,            0 },
		{ 4,  0,  1,  0,        549,        -1423 },
		{ 0,  0,  4,  0,        537,        -1117 },
		{ 4, -1,  0,  0,        520,        -1571 },
		{ 1,  0, -2,  0,       -487,        -1739 },
		{ 2,  1,  0, -2,       -399,            0 },
		{ 0,  0,  2, -2,       -381,        -4421 },
		{ 1,  1,  1,  0,        351,            0 },
		{ 3,  0, -2,  0,       -340,            0 },
		{ 4,  0, -3,  0,        330,            0 } ,
		{ 2, -1,  2,  0,        327,            0 },
		{ 0,  2,  1,  0,       -323,         1165 },
		{ 1,  1, -1,  0,        299,            0 },
		{ 2,  0,  3,  0,        294,            0 },
		{ 2,  0, -1, -2,          0,         8752 },            
	};                         

	
	// Monster table of coefficients for the calculation of the latitude.
	private static final int[][] latitudeTerms = { 
		{ 0,  0,  0,  1,      5128122 },
		{ 0,  0,  1,  1,       280602 },
		{ 0,  0,  1, -1,       277693 },
		{ 2,  0,  0, -1,       173237 },
		{ 2,  0, -1,  1,        55413 },
		{ 2,  0, -1, -1,        46271 },
		{ 2,  0,  0,  1,        32573 },
		{ 0,  0,  2,  1,        17198 },
		{ 2,  0,  1, -1,         9266 },
		{ 0,  0,  2, -1,         8822 },
		{ 2, -1,  0, -1,         8216 },
		{ 2,  0, -2, -1,         4324 },
		{ 2,  0,  1,  1,         4200 },
		{ 2,  1,  0, -1,        -3359 },
		{ 2, -1, -1,  1,         2463 },
		{ 2, -1,  0,  1,         2211 },
		{ 2, -1, -1, -1,         2065 },
		{ 0,  1, -1, -1,        -1870 },
		{ 4,  0, -1, -1,         1828 },
		{ 0,  1,  0,  1,        -1794 },
		{ 0,  0,  0,  3,        -1749 },
		{ 0,  1, -1,  1,        -1565 },
		{ 1,  0,  0,  1,        -1491 },
		{ 0,  1,  1,  1,        -1475 },
		{ 0,  1,  1, -1,        -1410 },
		{ 0,  1,  0, -1,        -1344 },
		{ 1,  0,  0, -1,        -1335 },
		{ 0,  0,  3,  1,         1107 },
		{ 4,  0,  0, -1,         1021 },
		{ 4,  0, -1,  1,          833 },
		{ 0,  0,  1, -3,          777 },
		{ 4,  0, -2,  1,          671 },
		{ 2,  0,  0, -3,          607 },
		{ 2,  0,  2, -1,          596 },
		{ 2, -1,  1, -1,          491 },
		{ 2,  0, -2,  1,         -451 },
		{ 0,  0,  3, -1,          439 },
		{ 2,  0,  2,  1,          422 },
		{ 2,  0, -3, -1,          421 },
		{ 2,  1, -1,  1,         -366 },
		{ 2,  1,  0,  1,         -351 },
		{ 4,  0,  0,  1,          331 },
		{ 2, -1,  1,  1,          315 },
		{ 2, -2,  0, -1,          302 },
		{ 0,  0,  1,  3,         -283 },
		{ 2,  1,  1, -1,         -229 },
		{ 1,  1,  0, -1,          223 },
		{ 1,  1,  0,  1,          223 },
		{ 0,  1, -2, -1,         -220 },
		{ 2,  1, -1, -1,         -220 },
		{ 1,  0,  1,  1,         -185 },
		{ 2, -1, -2, -1,          181 },
		{ 0,  1,  2,  1,         -177 },
		{ 4,  0, -2, -1,          176 },
		{ 4, -1, -1, -1,          166 },
		{ 1,  0,  1, -1,         -164 },
		{ 4,  0,  1, -1,          132 },
		{ 1,  0, -1, -1,         -119 },
		{ 4, -1,  0, -1,          115 },
		{ 2, -2,  0,  1,          107 },
	};

	  
	// ******************************************************************** //
	// Private Data.
	// ******************************************************************** //
	
	// The observation this Moon belongs to.
	private Observation observation;

}

