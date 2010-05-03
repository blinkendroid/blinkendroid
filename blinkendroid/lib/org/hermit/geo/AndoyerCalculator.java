
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
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


/**
 * A geographic data calculator based on the Andoyer formula.  This is
 * a good algorithm which takes into account the flattening of the Earth.
 * Accuracy should be around the flattening squared; however, some extreme
 * cases can yield bad results.
 * 
 * <p>Note that currently we only have the Andoyer algorithm for distance;
 * other functions use simpler spherical methods.  Hence, this class should
 * be used for reasonably fast but fairly accurate distance calculations,
 * where edge cases (e.g. near-antipodean points) are not encountered.
 *
 * @author	Ian Cameron Smith
 */
public class AndoyerCalculator
	extends GeoCalculator
{

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a calculator using the default ellipsoid.
	 */
	public AndoyerCalculator() {
		super();
	}


	/**
	 * Create a calculator using a given ellipsoid.
	 * 
	 * @param	ellip		The ellipsoid to use for geodetic calculations.
	 */
	public AndoyerCalculator(Ellipsoid ellip) {
		super(ellip);
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
		return Algorithm.ANDOYER;
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
		
		Ellipsoid ellipsoid = getEllipsoid();
		double a = ellipsoid.axis;
		double f = ellipsoid.flat;

		double F = (p1Lat + p2Lat) / 2;
		double sinF = sin(F);
		double sin2F = sinF * sinF;
		double cosF = cos(F);
		double cos2F = cosF * cosF;
		double G = (p1Lat - p2Lat) / 2;
		double sinG = sin(G);
		double sin2G = sinG * sinG;
		double cosG = cos(G);
		double cos2G = cosG * cosG;
		double λ = (p1Lon - p2Lon) / 2;
		double sinλ = sin(λ);
		double sin2λ = sinλ * sinλ;
		double cosλ = cos(λ);
		double cos2λ = cosλ * cosλ;
		
		double S = sin2G * cos2λ + cos2F * sin2λ;
		double C = cos2G * cos2λ + sin2F * sin2λ;
		
		double ω = atan(sqrt(S / C));
		double R = sqrt(S * C) / ω;
		
		double D = 2 * ω * a;
		double H1 = (3 * R - 1) / (2 * C);
		double H2 = (3 * R + 1) / (2 * S);
		
		double s = D * (1 + f * H1 * sin2F * cos2G - f * H2 * cos2F * sin2G);
		return new Distance(s);
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
		// Seems like this could be simplified.
		Position p2 = new Position(lat, p1.getLonRads());
		return distance(p1, p2);
	}
	

	/**
	 * Calculate the azimuth (bearing) from a position to another.
	 * 
	 * <p>NOTE: this is the Haversine version of this algorithm.
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
	 * <p>NOTE: this is the Haversine version of this algorithm.
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
	 * <p>NOTE: this is the Haversine version of this algorithm.
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

