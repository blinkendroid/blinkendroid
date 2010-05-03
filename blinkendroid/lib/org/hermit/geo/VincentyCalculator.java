
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
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;


/**
 * A geographic data calculator based on Vincenty's formulae.
 * 
 * <p>The geodetic operations in this class are based on an ellipsoidal
 * model of the Earth, using Vincenty's formulae; this is slow, but
 * very accurate.  Be aware, however, that accuracy is compromised in
 * extreme cases, such as nearly-antipodal points.
 * 
 * <p>The user needs to specify the ellipsoid to use for the geodetic
 * calculations; this is done in the constructor.  The Ellipsoid enum
 * defines a range of reference ellipsoids.  The default is the widely-used
 * WGS-84 standard.
 * 
 * <p>The code in this class was converted from the Fortran implementations
 * developed by the National Geodetic Survey.  No license is specified for
 * this code, but I believe that it is public domain, since it was created
 * by an agency of the US Government.
 * 
 * References:
 * 
 * <ul>
 * <li><a href="http://www.ngs.noaa.gov/PC_PROD/Inv_Fwd/">Fortran
 *     implementations of Vincenty's formulae,</a> from the National
 *     Geodetic Survey</li>
 * <li><a href="http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf">Vincenty's
 *     paper describing his formulae</a>, from NOAA</li>
 * <li><a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">JS
 *     implementations of Vincenty's inverse formula</a>, from Movable Type
 *     Ltd</li>
 * <li><a href="http://www.movable-type.co.uk/scripts/latlong-vincenty-direct.html">JS
 *     implementations of Vincenty's direct formula</a>, from Movable Type
 *     Ltd</li>
 * </ul>
 *
 * @author	Ian Cameron Smith
 */
public class VincentyCalculator
	extends GeoCalculator
{

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a calculator using the default ellipsoid.
	 */
	public VincentyCalculator() {
		super();
	}


	/**
	 * Create a calculator using a given ellipsoid.
	 * 
	 * @param	ellip		The ellipsoid to use for geodetic calculations.
	 */
	public VincentyCalculator(Ellipsoid ellip) {
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
		return Algorithm.VINCENTY;
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
		// Compute the geodetic inverse.  This gets the distance,
		// forward azimuth, and back azimuth.
		Ellipsoid ellipsoid = getEllipsoid();
		double[] ret = gpnhri(ellipsoid.axis, ellipsoid.flat,
							  p1.getLatRads(), p1.getLonRads(),
							  p2.getLatRads(), p2.getLonRads());
		
		return new Distance(ret[0]);
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
	 * @param	p1			Position to calculate the distance from.
	 * @param	p2			Position to calculate the distance to.
	 * @return				The azimuth of pos from this Position.
	 */
	@Override
	public Azimuth azimuth(Position p1, Position p2) {
		// Compute the geodetic inverse.  This gets the distance,
		// forward azimuth, and back azimuth.
		Ellipsoid ellipsoid = getEllipsoid();
		double[] ret = gpnhri(ellipsoid.axis, ellipsoid.flat,
							  p1.getLatRads(), p1.getLonRads(),
							  p2.getLatRads(), p2.getLonRads());
		
		return new Azimuth(ret[1]);
	}
	

	/**
	 * Calculate the azimuth and distance from a position to another.
	 * 
	 * This function is significantly faster than calling azimuth(p1, p2)
	 * and distance(p1, p2), if both parts are required.
	 *
	 * @param	p1			Position to calculate the vector from.
	 * @param	p2			Position to calculate the vector to.
	 * @return				The Vector from p1 to p2.
	 */
	@Override
	public Vector vector(Position p1, Position p2) {
		// Compute the geodetic inverse.  This gets the distance,
		// forward azimuth, and back azimuth.
		Ellipsoid ellipsoid = getEllipsoid();
		double[] ret = gpnhri(ellipsoid.axis, ellipsoid.flat,
							  p1.getLatRads(), p1.getLonRads(),
							  p2.getLatRads(), p2.getLonRads());

		return new Vector(new Distance(ret[0]), new Azimuth(ret[1]));
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
		// Do the calculation.
		Ellipsoid ellipsoid = getEllipsoid();
		double[] res = dirct1(p1.getLatRads(), p1.getLonRads(),
							  azimuth.getRadians(),
							  distance.getMetres(),
							  ellipsoid.axis, ellipsoid.flat);

		// Create a position from the calculated latitudeR and long,
		// if we got one.  (We really should.)
		if (isNaN(res[0]) || isNaN(res[1]))
			return null;
		return new Position(res[0], res[1]);
	}
	

	// ******************************************************************** //
	// The Vincenty Direct Solution.
	// ******************************************************************** //

	/**
	 * Solution of the geodetic direct problem after T. Vincenty.
	 * Modified Rainsford's method with Helmert's elliptical terms.
	 * Effective in any azimuth and at any distance short of antipodal.
	 *
	 * Programmed for the CDC-6600 by lcdr L. Pfeifer, NGS Rockville MD,
	 * 20 Feb 1975.
	 *
	 * @param	glat1		The latitude of the starting point, in radians,
	 * 						positive north.
	 * @param	glon1		The latitude of the starting point, in radians,
	 * 						positive east.
	 * @param	azimuth		The azimuth to the desired location, in radians
	 * 						clockwise from north.
	 * @param	dist		The distance to the desired location, in meters.
	 * @param	axis		The semi-major axis of the reference ellipsoid,
	 * 						in meters.
	 * @param	flat		The flattening of the reference ellipsoid.
	 * @return				An array containing the latitude and longitude
	 * 						of the desired point, in radians, and the
	 * 						azimuth back from that point to the starting
	 * 						point, in radians clockwise from north.
	 */
	private static double[] dirct1(double glat1, double glon1,
								   double azimuth, double dist,
								   double axis, double flat)
	{
		double r = 1.0 - flat;

		double tu = r * sin(glat1) / cos(glat1);

		double sf = sin(azimuth);
		double cf = cos(azimuth);

		double baz = 0.0;

		if (cf != 0.0)
			baz = atan2(tu, cf) * 2.0;

		double cu = 1.0 / sqrt(tu * tu + 1.0);
		double su = tu * cu;
		double sa = cu * sf;
		double c2a = -sa * sa + 1.0;

		double x = sqrt((1.0 / r / r - 1.0) * c2a + 1.0) + 1.0;
		x = (x - 2.0) / x;
		double c = 1.0 - x;
		c = (x * x / 4.0 + 1) / c;
		double d = (0.375 * x * x - 1.0) * x;
		tu = dist / r / axis / c;
		double y = tu;

		double sy, cy, cz, e;
		do {
			sy = sin(y);
			cy = cos(y);
			cz = cos(baz + y);
			e = cz * cz * 2.0 - 1.0;

			c = y;
			x = e * cy;
			y = e + e - 1.0;
			y = (((sy * sy * 4.0 - 3.0) * y * cz * d / 6.0 + x) *
					d / 4.0 - cz) * sy * d + tu;
		} while (abs(y - c) > PRECISION_LIMIT);

		baz = cu * cy * cf - su * sy;
		c = r * sqrt(sa * sa + baz * baz);
		d = su * cy + cu * sy * cf;
		double glat2 = atan2(d, c);
		c = cu * cy - su * sy * cf;
		x = atan2(sy * sf, c);
		c = ((-3.0 * c2a + 4.0) * flat + 4.0) * c2a * flat / 16.0;
		d = ((e * cy * c + cz) * sy * c + y) * sa;
		double glon2 = glon1 + x - (1.0 - c) * d * flat;
		baz = atan2(sa, baz) + PI;

		double[] ret = new double[3];
		ret[0] = glat2;
		ret[1] = glon2;
		ret[2] = baz;
		return ret;
	}


	// ******************************************************************** //
	// The Vicenty Inverse Solution.
	// ******************************************************************** //

	/**
	 * Solution of the geodetic inverse problem after T. Vincenty.
	 * Modified rainsford's method with helmert's elliptical terms.
	 * Effective in any azimuth and at any distance short of antipodal;
	 * from/to stations must not be the geographic pole.
	 *               
	 * Programmed by Robert (Sid) Safford; released for field use 5 Jul 1975.
	 * 
	 * @param	a			Semi-major axis of reference ellipsoid in meters.
	 * @param	f			Flattening (0.0033528...).
	 * @param	p1			Lat station 1, in radians, positive north.
	 * @param	e1			Lon station 1, in radians, positive east.
	 * @param	p2			Lat station 2, in radians, positive north.
	 * @param	e2			Lon station 2, in radians, positive east.
	 * @return				An array of doubles, containing: the geodetic
	 * 						distance between the stations, in meters; the
	 * 						azimuth at station 1 to station 2; and the
	 * 						azimuth at station 2 to station 1.  Azimuths are
	 * 						in radians, clockwise from north, and may not
	 * 						be normalized.
	 */
	private static double[] gpnhri(double a, double f,
							       double p1, double e1, double p2, double e2)
	{
        //  aa               constant from subroutine gpnloa                    
        //  alimit           equatorial arc distance along the equator   (radians)
        //  arc              meridional arc distance latitude p1 to p2 (in meters)      
        //  az1              azimuth forward                          (in radians)
        //  az2              azimuth back                             (in radians)
        //  bb               constant from subroutine gpnloa                    
        //  dlon             temporary value for difference in longitude (radians)   
        //  equ              equatorial distance                       (in meters)
        //  r1,r2            temporary variables    
        //  s                ellipsoid distance                        (in meters)
        //  sms              equatorial - geodesic distance (s - s) "sms"       
        //  ss               temporary variable     

		// Calculate the eccentricity squared.
		double esq = f * (2.0 - f);

		// Normalize the longitudes to be positive.
		if (e1 < 0.0)
			e1 += TWO_PI;
		if (e2 < 0.0)
			e2 += TWO_PI;

		// Test the longitude difference; if it's next to zero, then we
		// have to calculate this as a meridional arc.
		double dlon = e2 - e1;
		if (abs(dlon) < GEO_TOLERANCE)
			return gpnarc(a, f, esq, p1, p2);

		// Normalize the longitude difference to -PI .. PI.
		if (dlon >= PI  && dlon < TWO_PI)
			dlon = dlon - TWO_PI;
		if (dlon <= -PI && dlon > -TWO_PI)
			dlon = dlon + TWO_PI;

		// If the longitude difference is over 180 degrees, turn it around.
		double absDlon = abs(dlon);
		if (absDlon > PI)
			absDlon = TWO_PI - absDlon;
	
		// Compute the limit in longitude (alimit): it is equal 
		// to twice the distance from the equator to the pole,
		// as measured along the equator (east/west).
		double alimit = PI * (1.0 - f);

		// If the longitude difference is beyond the lift-off point, see if
		// our points are anti-nodal.  If so, we need to use the lift-off
		// algorithm.
		if (absDlon >= alimit && abs(p1) < NODAL_LIMIT && abs(p2) < NODAL_LIMIT)
			return gpnloa(a, f, esq, dlon);

		// 
		// 
		double f0   = 1.0 - f;
		double b    = a * f0;
		double epsq = esq / (1.0 - esq);
		double f2   = f * f;
		double f3   = f * f2;
		double f4   = f * f3;
		// 
//		the longitude difference 
		// 
		dlon  = e2 - e1;
		double ab    = dlon;

		// 
//		the reduced latitudes    
		// 
		double u1    = f0 * sin(p1) / cos(p1);
		double u2    = f0 * sin(p2) / cos(p2);
		// 
		u1    = atan(u1);
		u2    = atan(u2);
		// 
		double su1   = sin(u1);
		double cu1   = cos(u1);
		// 
		double su2   = sin(u2);
		double cu2   = cos(u2);
		// 
//		counter for the iteration operation
		// 
		double clon = 0, slon = 0, sinalf = 0;
                double sig = 0, csig = 0, ssig = 0, w = 0;
		double q2 = 0, q4 = 0, q6 = 0, r2 = 0, r3 = 0;
		for (int i = 0; i < 8; ++i) {
			clon  = cos(ab);
			slon  = sin(ab);
			// 
			csig  = su1 * su2 + cu1 * cu2 * clon;
			double k1 = slon * cu2;
			double k2 = su2 * cu1 - su1 * cu2 * clon;
			ssig  = sqrt(k1 * k1 + k2 * k2);
			// 
			sig   = atan2(ssig, csig);
			sinalf = cu1 * cu2 * slon / ssig;
			// 
			w   = 1.0 - sinalf * sinalf;
			double t4  = w * w;
			double t6  = w * t4;
			// 
//			the coefficients of type a      
			// 
			double ao  = f - f2 * (1.0 + f + f2) * w / 4.0 + 3.0 * f3 *
			(1.0 + 9.0 * f / 4.0) * t4 / 16.0 - 25.0 * f4 * t6 / 128.0;
			double a2  = f2 * (1 + f + f2) * w / 4.0 - f3 * (1 + 9.0 * f / 4.0) * t4 / 4.0 +
			75.0 * f4 * t6 / 256.0;
			double a4  = f3 * (1.0 + 9.0 * f / 4.0) * t4 / 32.0 - 15.0 * f4 * t6 / 256.0;
			double a6  = 5.0 * f4 * t6 / 768.0;
			// 
//			the multiple angle functions    
			// 
			double qo  = 0.0;
			if (w > FP_TOLERANCE)
				qo = -2.0 * su1 * su2 / w;

			// 
			q2  = csig + qo;
			q4  = 2.0 * q2 * q2 - 1.0;
			q6  = q2 * (4.0 * q2 * q2 - 3.0);
			r2  = 2.0 * ssig * csig;
			r3  = ssig * (3.0 - 4.0 * ssig * ssig);
			// 
//			the longitude difference 
			// 
			double s   = sinalf * (ao * sig + a2 * ssig * q2 + a4 * r2 * q4 + a6 * r3 * q6);
			double xz  = dlon + s;
			// 
			double xy  = abs(xz - ab);
			ab  = dlon + s;
			// 
			if (xy < PRECISION_LIMIT)
				break;
		}
		// 
//		the coefficients of type b      
		// 
		double z   = epsq * w;
		// 
		double bo  = 1.0 + z * (1.0 / 4.0 + z * (-3.0 / 64.0 + z * (5.0 / 256.0 - z * 175.0 / 16384.0)));
		double b2  = z * (-1.0 / 4.0 + z * (1.0 / 16.0 + z * (-15.0 / 512.0 + z * 35.0 / 2048.0)));
		double b4  = z * z * (-1.0 / 128.0 + z * (3.0 / 512.0 - z * 35.0 / 8192.0));
		double b6  = z * z * z * (-1.0 / 1536.0 + z * 5.0 / 6144.0);
		//
//		the distance in meters   
		// 
		double s = b * (bo * sig + b2 * ssig * q2 + b4 * r2 * q4 + b6 * r3 * q6);
		
		// Check for a non-distance ... p1,e1 & p2,e2 equal zero?  If so,
		// set the azimuths to zero.  Otherwise calculate them.
		double az1, az2;
		if (s < 0.00005) {
			az1 = 0.0;
			az2 = 0.0;
		} else {
			// First compute the az1 & az2 for along the equator.
			if (dlon > PI)
				dlon -= TWO_PI;
			else if (dlon < -PI)
				dlon += TWO_PI;
			az1 = dlon < 0 ? PI * 3.0 / 2.0 : PI / 2.0;
			az2 = dlon < 0 ? PI / 2.0 : PI * 3.0 / 2.0;

			// Now compute the az1 & az2 for latitudes not on the equator.
			if (!(abs(su1) < FP_TOLERANCE && abs(su2) < FP_TOLERANCE)) {
				double tana1 =  slon * cu2 / (su2 * cu1 - clon * su1 * cu2);
				double tana2 =  slon * cu1 / (su1 * cu2 - clon * su2 * cu1);
				double sina1 =  sinalf / cu1;
				double sina2 = -sinalf / cu2;

				az1 = atan2(sina1, sina1 / tana1);
				az2 = PI - atan2(sina2, sina2 / tana2);
			}
		}
		
		return new double[] { s, az1, az2 };
	}


	// ******************************************************************** //
	// Utility Functions.
	// ******************************************************************** //

	/**
	 * Compute the length of a meridional arc between two latitudes.
	 *               
	 * Programmed by Robert (Sid) Safford; released for field use 5 Jul 1975.
	 * 
	 * @param	amax		The semi-major axis of the reference ellipsoid,
	 * @param	flat		The flattening (0.0033528 ... ).
	 * @param	esq			Eccentricity squared for reference ellipsoid.
	 * @param	p1			The latitude of station 1.
	 * @param	p2			The latitude of station 2.
	 * @return				An array of doubles, containing: the geodesic
	 * 						distance between the stations, in meters;
	 * 						the azimuth at station 1 to station 2;
	 * 						and the azimuth at station 2 to station 1.
	 */
	private static double[] gpnarc(double amax, double flat, double esq,
							       double p1, double p2)
	{
		// Check for a 90 degree lookup.
		boolean ninety = abs(p1) < FP_TOLERANCE &&
						 abs(abs(p2) - PI / 2) < FP_TOLERANCE;

		double da = p2 - p1;
		double s1 = 0.0;
		double s2 = 0.0;

		// Compute the length of a meridional arc between two latitudes.
		double e2 = esq;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e6 * e2;
		double ex = e8 * e2;
		// 
		double t1 = e2 * (003.0 / 4.0);
		double t2 = e4 * (015.0 / 64.0);
		double t3 = e6 * (035.0 / 512.0);
		double t4 = e8 * (315.0 / 16384.0);
		double t5 = ex * (693.0 / 131072.0);
		// 
		double a = 1.0 + t1 + 3.0 * t2 + 10.0 * t3 + 35.0 * t4 + 126.0 * t5;
		// 
		if (!ninety) {
			// 
			double b  = t1 + 4.0 * t2 + 15.0 * t3 + 56.0 * t4 + 210.0 * t5;
			double c  = t2 + 06.0 * t3 + 28.0 * t4 + 120.0 * t5;
			double d  = t3 + 08.0 * t4 + 045.0 * t5;
			double e  = t4 + 010.0 * t5;
			double f  = t5;
			// 
			double db = sin(p2 *  2.0) - sin(p1 *  2.0);
			double dc = sin(p2 *  4.0) - sin(p1 *  4.0);
			double dd = sin(p2 *  6.0) - sin(p1 *  6.0);
			double de = sin(p2 *  8.0) - sin(p1 *  8.0);
			double df = sin(p2 * 10.0) - sin(p1 * 10.0);
			// 
//			compute the s2 part of the series expansion
			// 
			s2 = -db * b / 2 + dc * c / 4 - dd * d / 6 + de * e / 8 - df * f / 10;
		}
		// 
//		compute the s1 part of the series expansion
		// 
		s1 = da * a;
		// 
//		compute the arc length
		double arc = amax * (1.0 - esq) * (s1 + s2);
		
		// Make the return array.
		double[] ret = new double[3];
		ret[0] = abs(arc);
		
		// Calculate the forward and back azimuths, which will be
		// north and south or vice versa.
		if (p2 > p1) {
			ret[1] = 0.0;
			ret[2] = PI;
		} else {
			ret[1] = PI;
			ret[2] = 0.0;
		}

		return ret;
	}


	/**
	 * Subroutine to compute the lift-off-azimuth constants.
	 *               
	 * Programmed by Robert (Sid) Safford; released for field use 10 Jun 1985.
	 * 
	 * @param	a			The semi-major axis of the reference ellipsoid,
	 * @param	f			The flattening (0.0033528 ... ).
	 * @param	esq			Eccentricity squared for reference ellipsoid.
	 * @param	dlon		The longitude difference.
	 * @return				An array of doubles, containing: the geodesic
	 * 						distance between the stations, in meters;
	 * 						the azimuth at station 1 to station 2;
	 * 						and the azimuth at station 2 to station 1.
	 */
	private static double[] gpnloa(double a, double f, double esq, double dlon)
	{
		double absDlon = abs(dlon);
		double cons = (PI - absDlon) / (PI * f);
		// 
//		compute an approximate az
		// 
		double az = asin(cons);
		// 
		double t1   =    1.0;
		double t2   =  (-1.0 / 4.0) * f * (1.0 + f + f * f);
		double t4   =    3.0 / 16.0 * f * f * (1.0 + (9.0 / 4.0) * f);
		double t6   = (-25.0 / 128.0) * f * f * f;
		// 
		double ao = 0;
		double s = 0;
		for (int iter = 0; iter < 7; ++iter) {
			s    = cos(az);
			double c2   = s * s;
			// 
//			compute new ao
			// 
			ao = t1 + t2 * c2 + t4 * c2 * c2 + t6 * c2 * c2 * c2;
			double cs = cons / ao;
			s = asin(cs);
			if (abs(s - az) < PRECISION_LIMIT)
				break;
			// 
			az = s;
		}
		// 
		double az1 = s;
		if (dlon < 0.0)
			az1 = 2.0 * PI - az1;
		// 
		double az2 = 2.0 * PI - az1;

		// 
//		equatorial - geodesic  (s - s)   "sms"
		// 
		double esqp = esq / (1.0 - esq);
		s = cos(az1);
		// 
		double u2   = esqp * s * s;
		double u4   = u2 * u2;
		double u6   = u4 * u2;
		double u8   = u6 * u2;
		// 
		t1   =     1.0;
		t2   =    (1.0 / 4.0) * u2;
		t4   =   (-3.0 / 64.0) * u4;
		t6   =    (5.0 / 256.0) * u6;
		double t8   = (-175.0 / 16384.0) * u8;
		// 
		double bo   = t1 + t2 + t4 + t6 + t8;
		s    = sin(az1);
		
		// Compute s - s: the equatorial - geodesic distance between the
		// stations, in meters.
		double sms  = a * PI * (1.0 - f * abs(s) * ao - bo * (1.0 - f));

		// And now compute the geodesic distance, which is the equatorial
		// distance (calculated from the axis) minus sms.
		double equDist = a * absDlon;
		double geoDist = equDist - sms;
		
		return new double[] { geoDist, az1, az2 };
	}
	

	// ******************************************************************** //
	// Private Constants.
	// ******************************************************************** //

	// Two times PI (handy sometimes).
	private static final double TWO_PI = 2 * PI;

	// Floating-point tolerance.  Two values closer than this can be
	// considered to be the same.
	private static final double FP_TOLERANCE = 5.0e-15;
	
	// Tolerance used when comparing values against meridians or the
	// equator.
	private static final double GEO_TOLERANCE = 5.0e-14;
	
	// A looser version of FP_TOLERANCE.
	private static final double PRECISION_LIMIT = 0.5e-13;
	
	// Points closer to the equator than this are candidates to be
	// consider anti-nodal.
	private static final double NODAL_LIMIT = 7.0e-03;
	
}

