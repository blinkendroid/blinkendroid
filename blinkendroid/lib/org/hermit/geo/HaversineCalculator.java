
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

import static java.lang.Double.isNaN;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


/**
 * A geographic data calculator based on the Haversine formula.  This is
 * a fast algorithm which is based on a spherical approximation of the
 * Earth.  This should give an accuracy within 0.5% or so.
 *
 * @author	Ian Cameron Smith
 */
public class HaversineCalculator
	extends GeoCalculator
{

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a calculator using the default ellipsoid.
	 */
	public HaversineCalculator() {
		// Haversine only does spheres.
		super(Ellipsoid.SPHERE);
	}


	/**
	 * Create a calculator using a given ellipsoid.
	 * 
	 * @param	ellip		The ellipsoid to use for geodetic calculations.
	 */
	public HaversineCalculator(Ellipsoid ellip) {
		super(ellip);
		if (ellip != Ellipsoid.SPHERE)
			throw new IllegalArgumentException("HaversineCalculator can only work on Ellipsoid.SHPERE");
	}


	// ******************************************************************** //
	// Geodetic Methods.
	// ******************************************************************** //

	/**
	 * Get the algorithm this calculator uses.
	 * 
	 * @return				The algorithm this calculator uses.
	 */
	@Override
	public Algorithm getAlgorithm() {
		return Algorithm.HAVERSINE;
	}


	/**
	 * Calculate the distance between two positions.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	p2			Position to calculate the distance to.
	 * @return				The distance between p1 and p2.
	 */
	@Override
	public Distance distance(Position p1, Position p2) {
		double p1Lat = p1.getLatRads();
		double p1Lon = p1.getLonRads();
		double p2Lat = p2.getLatRads();
		double p2Lon = p2.getLonRads();

		double halfLat = (p2Lat - p1Lat) / 2;
		double halfLon = (p2Lon - p1Lon) / 2;
		
		// Calculate sines squared, without doing sines twice.
		double sin2HalfLat = sin(halfLat);
		sin2HalfLat = sin2HalfLat * sin2HalfLat;
		double sin2HalfLon = sin(halfLon);
		sin2HalfLon = sin2HalfLon * sin2HalfLon;
		
		double a = sin2HalfLat + cos(p1Lat) * cos(p2Lat) * sin2HalfLon;
		double c = 2 * atan2(sqrt(a), sqrt(1 - a));
		
		// Convert the angular distance to metres.
		Ellipsoid ellipsoid = getEllipsoid();
		return new Distance(ellipsoid.axis * c);
	}


	/**
	 * Calculate the distance between a position and a given latitude.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	lat			Latitude in radians to calculate the distance to.
	 * @return				The distance of this Position from lat.
	 */
	@Override
	public Distance latDistance(Position p1, double lat) {
	    // In a spherical model, the angular distance is trivial.
		double c = lat - p1.getLatRads();
		
		// Convert the angular distance to metres.
		Ellipsoid ellipsoid = getEllipsoid();
		return new Distance(ellipsoid.axis * Math.abs(c));
	}


	/**
	 * Calculate the azimuth (bearing) from a position to another.
	 *
	 * @param	p1			Position to calculate the distance from.
	 * @param	p2			Position to calculate the distance to.
	 * @return				The azimuth of pos from this Position.
	 */
	@Override
	public Azimuth azimuth(Position p1, Position p2) {
		double p1Lat = p1.getLatRads();
		double p1Lon = p1.getLonRads();
		double p2Lat = p2.getLatRads();
		double p2Lon = p2.getLonRads();

		double dLon = p2Lon - p1Lon;

		double y = sin(dLon) * cos(p2Lat);
		double x = cos(p1Lat) * sin(p2Lat) - sin(p1Lat) * cos(p2Lat) * cos(dLon);

		// Calculate the azimuth.
		return new Azimuth(atan2(y, x));
	}


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
	@Override
	public Vector vector(Position p1, Position p2) {
		Distance dist = distance(p1, p2);
		Azimuth fwdAz = azimuth(p1, p2);
		return new Vector(dist, fwdAz);
	}


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
	@Override
	public Position offset(Position p1, Distance distance, Azimuth azimuth) {
		double p1Lat = p1.getLatRads();
		double p1Lon = p1.getLonRads();
		Ellipsoid ellipsoid = getEllipsoid();
		
		double angDist = distance.getMetres() / ellipsoid.axis;
		double azRads = azimuth.getRadians();
		
		// Pre-calculate some sines and cosines to save multiple calls.
		double sinLat = sin(p1Lat);
		double cosLat = cos(p1Lat);
		double sinDist = sin(angDist);
		double cosDist = cos(angDist);
		
		// Calculate the result.
		double lat2 = asin(sinLat * cosDist + cosLat * sinDist * cos(azRads));
		double lon2 = p1Lon + atan2(sin(azRads) * sinDist * cosLat, 
					  			  	cosDist - sinLat * sin(lat2));
		
		// Return the result, if we got one.
		if (isNaN(lat2) || isNaN(lon2))
			return null;
		return new Position(lat2, lon2);
	}

}

