
/**
 * geo: geographical utilities.
 * <br>Copyright 2004-2009 Ian Cameron Smith
 * 
 * <p>References:
 * <dl>
 * <dt>AA</dt>
 * <dd>"Astronomical Algorithms", by Jean Meeus, ISBN-10: 0-943396-61-1.</dd>
 * </dl>
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


import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import org.hermit.utils.Angle;


/**
 * This class represents a geographic position -- ie. a latitude and
 * longitude.  It provides utility methods for the common geodetic
 * operations of finding distance and azimuth between points, or projecting
 * to a point given a distance and azimuth.
 * 
 * The geodetic operations in this class are based on a simple spherical
 * model of the Earth, using the haversine formulae; this is fast, and
 * provides accuracy of about 0.5%, which will be adequate for many
 * purposes.  Users desiring greater accuracy should use the
 * {@link VincentyCalculator} subclass.
 * 
 * References:
 * 
 * <ul>
 * <li><a href="http://www.movable-type.co.uk/scripts/gis-faq-5.1.html">GIS
 *     FAQ Q5.1: Great circle distance between 2 points</a></li>
 * <li><a href="http://www.movable-type.co.uk/scripts/latlong.html">JS
 *     implementations of the haversine formulae</a>, from Movable
 *     Type Ltd</li>
 * <li><a href="http://williams.best.vwh.net/avform.htm">Aviation
 *     Formulary</a>, by Ed Williams</li>
 * </ul>
 *
 * @author	Ian Cameron Smith
 */
public class Position
{
    
	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

    /**
     * Constant representing an unknown position.
     */
    public static final Position UNKNOWN = new Position(Double.NaN,
                                                        Double.NaN, true);
    
    
    // ******************************************************************** //
    // Public Constructors.
    // ******************************************************************** //

	/**
	 * Create a Position from a geographic latitude and longitude.
	 * 
	 * @param	latRadians	Geographic latitude in radians of the desired
	 * 						position, positive north.
	 * @param	lonRadians	Longitude in radians of the desired position,
	 * 						positive east.
	 */
	public Position(double latRadians, double lonRadians) {
		this(latRadians, lonRadians, false);
	}


    /**
     * Create a Position from a geographic latitude and longitude.  This
     * private constructor allows NaN, in order to create the constant
     * UNKNOWN.
     * 
     * @param   latRadians  Geographic latitude in radians of the desired
     *                      position, positive north.
     * @param   lonRadians  Longitude in radians of the desired position,
     *                      positive east.
     * @param   allowNan    Iff true, allow the lat and long to be NaN.
     */
    private Position(double latRadians, double lonRadians, boolean allowNan) {
        if (!allowNan) {
            if (Double.isNaN(latRadians) || Double.isInfinite(latRadians) ||
                    Double.isNaN(lonRadians) || Double.isInfinite(lonRadians))
                throw new IllegalArgumentException("Components of a Position" +
                                                   " must be finite");
        }
        
        init(latRadians, lonRadians);
    }

	
	// GeoPoint missing in my ROM????
//	/**
//	 * Create a Position from a Google Maps Point.  Point represents
//	 * latitude and longitude in microdegrees (degrees * 1E6).
//	 * 
//	 * @param	mapPoint	The Google Maps representation of the
//	 * 						desired position.
//	 */
//	public Position(GeoPoint mapPoint) {
//		double latR = toRadians(mapPoint.getLatitudeE6() / MILLION);
//		double lonR = toRadians(mapPoint.getLongitudeE6() / MILLION);
//		init(latR, lonR);
//	}

	
//	/**
//	 * Create a Position from an Android Location.
//	 * 
//	 * @param	loc			The Android Location of the desired position.
//	 */
//	public Position(Location loc) {
//		double latR = toRadians(loc.getLatitude());
//		double lonR = toRadians(loc.getLongitude());
//		init(latR, lonR);
//	}


	/**
	 * Create a Position from a Position.  This is useful for converting
	 * between subclasses and superclasses.
	 * 
	 * @param	pos			The Position to copy.
	 */
	public Position(Position pos) {
		init(pos.latitudeR, pos.longitudeR);
	}


	/**
	 * Set this Position up with a given geographic latitude and longitude.
	 * Carry out any necessary normalization.
	 * 
	 * This method should be used by all constructors, to make sure that
	 * the resulting lat and long are normalized.
	 * 
	 * @param	latRadians	Geographic latitude in radians of the desired
	 * 						position, positive north.
	 * @param	lonRadians	Longitude in radians of the desired position,
	 * 						positive east.
	 */
	private void init(double latRadians, double lonRadians) {
	    if (!Double.isNaN(latRadians) && !Double.isNaN(lonRadians)) {
	        if (latRadians < -PI / 2)
	            latRadians = -PI / 2;
	        else if (latRadians > PI / 2)
	            latRadians = PI / 2;

	        // Normalize the longitude to -PI .. +PI.
	        while (lonRadians < 0)
	            lonRadians += 2 * PI;
	        lonRadians = (lonRadians + PI) % (2 * PI) - PI;
	    }
	    
        latitudeR = latRadians;
        longitudeR = lonRadians;
	}


	// ******************************************************************** //
	// Accessors and Converters.
	// ******************************************************************** //

	/**
	 * Create a Position from a geographic latitude and longitude given
	 * in degrees.
	 * 
	 * @param	latDegrees	Geographic latitude in degrees of the desired
	 * 						position, positive north.
	 * @param	lonDegrees	Longitude in degrees of the desired position,
	 * 						positive east.
	 * @return				The new Position.
	 */
	public static Position fromDegrees(double latDegrees, double lonDegrees) {
        if (Double.isNaN(latDegrees) || Double.isInfinite(latDegrees) ||
                Double.isNaN(lonDegrees) || Double.isInfinite(lonDegrees))
            throw new IllegalArgumentException("Components of a Position" +
                                               " must be finite");
        
		return new Position(toRadians(latDegrees), toRadians(lonDegrees));
	}

	
	/**
	 * Get the geographic latitude of this Position in radians.
	 *
	 * @return				The geographic latitude of this Position in radians,
	 * 						positive north.  NaN if this is UNKNOWN.
	 */
	public double getLatRads() {
		return latitudeR;
	}

	
	/**
	 * Get the geocentric latitude of this Position in radians.
	 * 
	 * <p>From AA chapter 11.
	 *
	 * @return				The geocentric latitude of this Position in radians,
	 * 						positive north.  NaN if this is UNKNOWN.
	 */
	public double getGeocentricLat() {
	    if (Double.isNaN(latitudeR))
	        return Double.NaN;
	    
		final double f1 = toRadians(692.73 / 3600.0);
		final double f2 = toRadians(1.16 / 3600.0);
		double Δφ = f1 * sin(2 * latitudeR) - f2 * sin(4 * latitudeR);
		return latitudeR - Δφ;
	}


	/**
	 * Get the longitude of this Position in radians.
	 *
	 * @return				The longitude of this Position in radians,
	 * 						positive east.  NaN if this is UNKNOWN.
	 */
	public double getLonRads() {
		return longitudeR;
	}


	/**
	 * Get the geographic latitude of this Position in degrees.
	 *
	 * @return				The geographic latitude of this Position in degrees,
	 * 						positive north.  NaN if this is UNKNOWN.
	 */
	public double getLatDegs() {
        if (Double.isNaN(latitudeR))
            return Double.NaN;
        
		return toDegrees(latitudeR);
	}


	/**
	 * Get the longitude of this Position in degrees.
	 *
	 * @return				The longitude of this Position in degrees,
	 * 						positive east.  NaN if this is UNKNOWN.
	 */
	public double getLonDegs() {
        if (Double.isNaN(longitudeR))
            return Double.NaN;
        
		return toDegrees(longitudeR);
	}

	   
    /**
     * Get the distance from the centre of the Earth to this Position.
     * 
     * <p>From AA chapter 11.
     *
     * @return              The distance to the centre of the Earth, in
     *                      units of EQUATORIAL_RADIUS.  NaN if this is UNKNOWN.
     */
    public double getCentreDistance() {
        if (Double.isNaN(latitudeR))
            return Double.NaN;
        
        double ρ = 0.9983271 +
                   0.0016764 * cos(2 * latitudeR) -
                   0.0000035 * cos(4 * latitudeR);
        return ρ;
    }


//	/**
//	 * Convert a given lat and long to a Google Maps Point.  Point represents
//	 * latitude and longitude in microdegrees (degrees * 1E6).
//	 * 
//	 * @param	latRadians	Geographic latitude in radians of the desired
//	 * 						position, positive north.
//	 * @param	lonRadians	Longitude in radians of the desired position,
//	 * 						positive east.
//	 */
//	public static GeoPoint toGeoPoint(double latRadians, double lonRadians) {
//		int latE6 = (int) round(toDegrees(latRadians) * MILLION);
//		int lonE6 = (int) round(toDegrees(lonRadians) * MILLION);
//		return new GeoPoint(latE6, lonE6);
//	}


//	/**
//	 * Convert this Position to a Google Maps Point.  Point represents
//	 * latitude and longitude in microdegrees (degrees * 1E6).
//	 * 
//	 * @param				The Google Maps representation of this Position.
//	 */
//	public GeoPoint toGeoPoint() {
//		int latE6 = (int) round(toDegrees(latitudeR) * MILLION);
//		int lonE6 = (int) round(toDegrees(longitudeR) * MILLION);
//		return new GeoPoint(latE6, lonE6);
//	}


	// ******************************************************************** //
	// Geodetic Methods.
	// ******************************************************************** //

	/**
	 * Calculate the distance and azimuth from this position to another,
	 * using the haversine formula, which is based on a spherical
	 * approximation of the Earth.  This should give an accuracy within
	 * 0.5% or so.
	 * 
	 * In some subclasses, this function may be significantly faster
	 * than calling distance(pos) and azimuth(pos).
	 *
	 * @param	pos			Position to calculate the vector to.
	 * @return				The Vector to the given position.  null if either
	 *                      position is UNKNOWN.
	 */
	public Vector vector(Position pos) {
	    if (this == UNKNOWN || pos == UNKNOWN)
	        return null;
	    
		GeoCalculator calc = GeoCalculator.getCalculator();
		
		Distance dist = calc.distance(this, pos);
		Azimuth fwdAz = calc.azimuth(this, pos);
		// Azimuth backAz = pos.azimuth(this);
		return new Vector(dist, fwdAz);
	}


	/**
	 * Calculate the distance between this position and another, using the
	 * haversine formula, which is based on a spherical approximation of
	 * the Earth.  This should give an accuracy within 0.5% or so.
	 *
	 * @param	pos			Position to calculate the distance to.
	 * @return				The distance of pos from this Position.  null
     *                      if either position is UNKNOWN.
	 */
	public Distance distance(Position pos) {
        if (this == UNKNOWN || pos == UNKNOWN)
            return null;
        
		GeoCalculator calc = GeoCalculator.getCalculator();
		return calc.distance(this, pos);
	}


	/**
	 * Calculate the distance between this position and a given latitude,
	 * based on a spherical approximation of the Earth.  This should
	 * give an accuracy within 0.5% or so.
	 *
	 * @param	lat			Latitude in radians to calculate the distance to.
	 * @return				The distance of this Position from lat.  null
     *                      if this position is UNKNOWN.
	 */
	public Distance latDistance(double lat) {
        if (this == UNKNOWN)
            return null;

		GeoCalculator calc = GeoCalculator.getCalculator();
		return calc.latDistance(this, lat);
	}


	/**
	 * Calculate the azimuth (bearing) from this position to another, using
	 * the haversine formula, which is based on a spherical approximation of
	 * the Earth.  This should give an accuracy within 0.5% or so.
	 *
	 * @param	pos			Position to calculate the azimuth to.
	 * @return				The azimuth of pos from this Position.  null
     *                      if either position is UNKNOWN.
	 */
	public Azimuth azimuth(Position pos) {
        if (this == UNKNOWN || pos == UNKNOWN)
            return null;
        
		GeoCalculator calc = GeoCalculator.getCalculator();
		return calc.azimuth(this, pos);
	}


	/**
	 * Calculate a second position given its offset from this one, using the
	 * current geodetic calculator -- see {@link GeoCalculator}.
	 * 
	 * @param	vector		The Vector to the desired position.
	 * @return				The position given by the azimuth and distance
	 * 						from this one.  Returns null if the result
	 * 						could not be computed, including if this position
	 *                      is UNKNOWN.
	 */
	public Position offset(Vector vector) {
		return offset(vector.getDistance(), vector.getAzimuth());
	}


	/**
	 * Calculate a second position given its offset from this one, using the
     * current geodetic calculator -- see {@link GeoCalculator}.
	 * 
	 * @param	distance	The Distance to the desired position.
	 * @param	azimuth		The Azimuth to the desired position.
	 * @return				The position given by the azimuth and distance
	 * 						from this one.  Returns null if the result
	 * 						could not be computed, including if this position
     *                      is UNKNOWN.
	 */
	public Position offset(Distance distance, Azimuth azimuth) {
        if (this == UNKNOWN)
            return null;

		GeoCalculator calc = GeoCalculator.getCalculator();
		return calc.offset(this, distance, azimuth);
	}
	
	
	// ******************************************************************** //
	// Formatting.
	// ******************************************************************** //

    /**
     * Format this position for user display in degrees and minutes.
     *
     * @return              The formatted angle.
     */
    public String formatDegMin() {
        return Angle.formatDegMin(toDegrees(latitudeR), 'N', 'S') + ' ' +
        	   Angle.formatDegMin(toDegrees(longitudeR), 'E', 'W');
    }


    /**
     * Format this position for user display in degrees and minutes.
     *
     * @return              The formatted angle.
     */
    public String formatDegMinSec() {
        return Angle.formatDegMinSec(toDegrees(latitudeR), 'N', 'S') + ' ' +
        	   Angle.formatDegMinSec(toDegrees(longitudeR), 'E', 'W');
    }

    
    /**
     * Format this position as a String.
     * 
     * @return          This position as a string, in degrees and minutes.
     */
    @Override
    public String toString() {
        return formatDegMin();
    }
    
	
	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //

	/**
	 * The latitude, in radians, positive north.
	 */
	private double latitudeR;

	/**
	 * The longitude, in radians, positive east.
	 */
	private double longitudeR;

}

