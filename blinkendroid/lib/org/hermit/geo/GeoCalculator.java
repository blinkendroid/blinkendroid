
/**
 * geo: geographical utilities.
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

package org.hermit.geo;


/**
 * Base class for geographic data calculators.  Subclasses of this class
 * provide functions like distance and azimuth calculation; since there are
 * multiple algorithms with very different accuracy and complexity, the user
 * can select which subclass they wish to use.
 *
 * @author	Ian Cameron Smith
 */
public abstract class GeoCalculator
	implements GeoConstants
{

    // ******************************************************************** //
    // Public Constants.
    // ******************************************************************** //
	
	/**
	 * Definition of the algorithm to be used.
	 */
	public enum Algorithm {
		/** The Haversine algorithm -- assumes a spherical Earth. */
		HAVERSINE,
		
		/** As per Haversine, but uses the better Andoyer method for distance. */
		ANDOYER,
		
		/** The very accurate but slow Vincenty method. */
		VINCENTY;
		
		// The instantiated calculator for this algorithm, once we have
		// created it.
		private GeoCalculator calculator = null;
	}
	

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a calculator using the WGS84 ellipsoid.
	 */
	public GeoCalculator() {
		this(Ellipsoid.WGS84);
	}


	/**
	 * Create a calculator using a given ellipsoid.
	 * 
	 * @param	ellip		The ellipsoid to use for geodetic calculations.
	 */
	public GeoCalculator(Ellipsoid ellip) {
		ellipsoid = ellip;
	}


	// ******************************************************************** //
	// Algorithm Selection.
	// ******************************************************************** //

	/**
	 * Get the geodetic calculator in use.
	 * 
	 * @return					The algorithm currently in use.
	 */
	public static Algorithm getCurrentAlgorithm() {
		return defaultCalculator.getAlgorithm();
	}


	/**
	 * Set the geodetic calculator to use for future calculations.
	 * The default is HAVERSINE.
	 * 
	 * @param	algorithm		The algorithm to use.  If it has a selectable
	 * 							ellipsoid, then the WGS84 ellipsoid will
	 * 							be used.
	 */
	public static void setAlgorithm(Algorithm algorithm) {
		defaultCalculator = getCalculator(algorithm, Ellipsoid.WGS84);
	}


	/**
	 * Set the geodetic calcualtor to use for future calculations.
	 * The default is HAVERSINE.
	 * 
	 * @param	algorithm		The algorithm to use.
	 * @param	ellipsoid		If the algorithm has a selectable
	 * 							ellipsoid, then this ellipsoid will
	 * 							be used.
	 */
	public static void setAlgorithm(Algorithm algorithm, Ellipsoid ellipsoid) {
		defaultCalculator = getCalculator(algorithm, ellipsoid);
	}

	
	/**
	 * Get the default geodetic calcualtor.
	 * 
	 * @return					The default geodetic calcualtor.
	 */
	public static GeoCalculator getCalculator() {
		return defaultCalculator;
	}

	
	/**
	 * Get a geodetic calcualtor based on the indicated algorithm.
	 * 
	 * @param	algorithm		The algorithm to use.  If it has a selectable
	 * 							ellipsoid, then the WGS84 ellipsoid will
	 * 							be used.
	 * @return					A calculator for the given algorithm.
	 */
	private static GeoCalculator getCalculator(Algorithm algorithm) {
		return getCalculator(algorithm, Ellipsoid.WGS84);
	}
	
	
	/**
	 * Get a geodetic calculator based on the indicated algorithm.
	 * 
	 * @param	algorithm		The algorithm to use.
	 * @param	ellipsoid		If the algorithm has a selectable
	 * 							ellipsoid, then this ellipsoid will
	 * 							be used.
	 * @return					A calculator for the given algorithm.
	 */
	private static GeoCalculator getCalculator(Algorithm algorithm,
											  Ellipsoid ellipsoid)
	{
		switch (algorithm) {
		case HAVERSINE:
			algorithm.calculator = new HaversineCalculator(Ellipsoid.SPHERE);
			break;
		case ANDOYER:
			algorithm.calculator = new AndoyerCalculator(ellipsoid);
			break;
		case VINCENTY:
			algorithm.calculator = new VincentyCalculator(ellipsoid);
			break;
		}

		return algorithm.calculator;
	}


	// ******************************************************************** //
	// Geodetic Methods.
	// ******************************************************************** //

	/**
	 * Get the algorithm this calculator uses.
	 * 
	 * @return				The algorithm this calculator uses.
	 */
	public abstract Algorithm getAlgorithm();


	/**
	 * Get the ellipsoid for this calculator.
	 * 
	 * @return				The ellipsoid this calculator was configured
	 * 						with.
	 */
	Ellipsoid getEllipsoid() {
		return ellipsoid;
	}
	

	/**
	 * Calculate the distance between two positions.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	p2			Position to calculate the distance to.
	 * @return				The distance between p1 and p2.
	 */
	public abstract Distance distance(Position p1, Position p2);


	/**
	 * Calculate the distance between a position and a given latitude.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	lat			Latitude in radians to calculate the distance to.
	 * @return				The distance of this Position from lat.
	 */
	public abstract Distance latDistance(Position p1, double lat);


	/**
	 * Calculate the azimuth (bearing) from a position to another.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	p2			Position to calculate the distance to.
	 * @return				The azimuth of pos from this Position.
	 */
	public abstract Azimuth azimuth(Position p1, Position p2);


	/**
	 * Calculate the azimuth and distance from a position to another.
	 * 
	 * This function may be faster than calling azimuth(p1, p2)
	 * and distance(p1, p2), if both parts are required.
	 *
	 * @param	p1			Position to calculate the vector from.
	 * @param	p2			Position to calculate the vector to.
	 * @return				The Vector from p1 to p2.
	 */
	public abstract Vector vector(Position p1, Position p2);


	/**
	 * Calculate a second position given its offset from a given position.
	 * 
	 * @param	p1			Position to calculate from.
	 * @param	distance	The Distance to the desired position.
	 * @param	azimuth		The Azimuth to the desired position.
	 * @return				The position given by the azimuth and distance
	 * 						from p1.  Returns null if the result
	 * 						could not be computed.
	 */
	public abstract Position offset(Position p1, Distance distance, Azimuth azimuth);

	
	// ******************************************************************** //
	// Private Class Data.
	// ******************************************************************** //

	// The default GeoCalculator used for geodetic calculations.
	private static GeoCalculator defaultCalculator =
									getCalculator(Algorithm.HAVERSINE);

	
	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //
	
	// The ellipsoid to use for the geodetic computations.
	private Ellipsoid ellipsoid = null;

}

