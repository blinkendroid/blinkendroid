
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
 * This class represents a point or area of interest.  Its subclasses define
 * specific interest zones, as either points, lines or areas.
 *
 * @author	Ian Cameron Smith
 */
public abstract class PointOfInterest
{

	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

	// Half pi = 90 degrees.
    private static final double HALFPI = Math.PI / 2.0;

    // The Earth's axial tilt in radians.  TODO: use correct current value.
    private static final double EARTH_TILT = Math.toRadians(23.43944444);
    
    // Interesting places.
    private static final double CHALLENGER_LAT = Math.toRadians(11d + 22d/60d);
    private static final double CHALLENGER_LON = Math.toRadians(142d + 36d/60d);
    private static final double INACC_LAT = -Math.toRadians(48d + 52.6d/60d);
    private static final double INACC_LON = -Math.toRadians(123d + 23.6d/60d);
    private static final double RLYEH_LAT = -Math.toRadians(47d + 9d/60d);
    private static final double RLYEH_LON = -Math.toRadians(126d + 43d/60d);

    // Interesting latitudes.  Don't expose these as public constants
    // because they are not constant; we need to calculate the correct
    // axial tilt.
    private static final double NPOLE = HALFPI;
    private static final double ARCTIC = HALFPI - EARTH_TILT;
    private static final double CANCER = EARTH_TILT;
    private static final double EQUATOR = 0.0;
    private static final double CAPRICORN = -EARTH_TILT;
    private static final double ANTARC = -HALFPI + EARTH_TILT;
    private static final double SPOLE = -HALFPI;

    /**
     * List of general global points of interest.  The closest point here
     * will be taken as our interesting point, if in there is one in
     * the range specified by DISTANCE_NEAR.
     */
    public static final PointOfInterest[] GLOBAL_POIS = {
    	// Specific locations are most interesting.
    	new POS(EQUATOR, 0.0, "The Origin"),
    	new POS(EQUATOR, Math.PI, "The Anti-Origin"),
    	new POS(CHALLENGER_LAT, CHALLENGER_LON, "The Challenger Deep"),
    	new POS(INACC_LAT, INACC_LON, "Pole of Inaccessibility"),
    	new POS(RLYEH_LAT, RLYEH_LON, "R'lyeh"),

    	// Specific latitudes are next most interesting.
    	new LAT(NPOLE, "The North Pole"),
    	new LAT(ARCTIC, "The Arctic Circle"),
    	new LAT(CANCER, "The Tropic of Cancer"),
    	new LAT(EQUATOR, "The Equator"),
    	new LAT(CAPRICORN, "The Tropic of Capricorn"),
    	new LAT(ANTARC, "The Antarctic Circle"),
    	new LAT(SPOLE, "The South Pole"),

    	// Major meridians are also interesting, but only outside the Arctic
    	// (otherwise, if we're near the pole, it always tells us how far
    	// we are from the Greenwich meridian).
    	new LON(0.0, ANTARC, ARCTIC, "The Greenwich Meridian"),
    	new LON(Math.PI, ANTARC, ARCTIC, "The Anti-Meridian"),
    };
    

    /**
     * List of regions of the world.
     * 
     * Area is important in this list -- the first band we're in is the
     * one returned.  This is significant for overlapping bands (like
     * the Antarctic and the Screaming Sixties).
     */
    public static final PointOfInterest[] GLOBAL_AREAS = {
    	// The Arctic and Antarctic are dead exciting, and take precedence.
    	new BAND(ARCTIC, NPOLE, "The Arctic"),
    	new BAND(SPOLE, ANTARC, "The Antarctic"),

    	// The southern ocean is pretty daring.
    	new BAND(Math.toRadians(-50), Math.toRadians(-40), "The Roaring Forties"),
    	new BAND(Math.toRadians(-60), Math.toRadians(-50), "The Furious Fifties"),
    	new BAND(Math.toRadians(-70), Math.toRadians(-60), "The Screaming Sixties"),

    	// Moderate latitude bands are fairly boring.
    	new BAND(EQUATOR, CANCER, "The Northern Tropics"),
    	new BAND(CAPRICORN, EQUATOR, "The Southern Tropics"),
    	
    	// Temperate climes are just too boring.  I'd rather know my distance
    	// from the tropics.
    	// new BAND(CANCER, ARCTIC, "Temperate Climes"),
    	// new BAND(ANTARC, CAPRICORN, "Temperate Climes"),
    };


	// ******************************************************************** //
	// Public Classes.
	// ******************************************************************** //
    
    /**
     * Class POS represents an interesting position.
     */
    public static final class POS extends PointOfInterest {
    	
    	/**
    	 * Create a position of interest.
    	 * 
    	 * @param	lat			Latitude, in radians.
    	 * @param	lon			Longitude, in radians.
    	 * @param	n			Name of this latitude.
    	 */
    	POS(double lat, double lon, String n) {
    		super(n);
    		position = new Position(lat, lon);
    	}
    	
    	/**
    	 * Get the distance of a given point from this point of interest.
    	 * 
    	 * @param	pos			Point to measure from.
    	 * @return				The distance.
    	 * @see org.hermit.geo.PointOfInterest#distance(org.hermit.geo.Position)
    	 */
    	@Override
		public final Distance distance(Position pos) {
    		return pos.distance(position);
    	}

    	// The position.
    	private final Position position;
 
    }
 
    
    /**
     * Class LAT represents an interesting line of latitude.
     */
    public static final class LAT extends PointOfInterest {
    	
    	/**
    	 * Create a latitude of interest.
    	 * 
    	 * @param	lat			Latitude, in radians.
    	 * @param	n			Name of this latitude.
    	 */
    	LAT(double lat, String n) {
    		super(n);
    		latitude = lat;
    	}
    	
    	/**
    	 * Get the distance of a given point from this point of interest.
    	 * 
    	 * @param	pos			Point to measure from.
    	 * @return				The distance.
    	 * @see org.hermit.geo.PointOfInterest#distance(org.hermit.geo.Position)
    	 */
    	@Override
		public final Distance distance(Position pos) {
    		return pos.latDistance(latitude);
    	}
    	
    	/**
    	 * Describe the status of the given position relative to this
    	 * point of interest.
    	 * 
    	 * @param	pos			The position to describe.
    	 * @return				A string describing where pos is in relation
    	 * 						to this POI.
    	 */
    	@Override
		public final String status(Position pos) {
    		Distance d = distance(pos);
    		double nm = d.getNm();
    		if (nm <= DISTANCE_THRESH)
    			return getName();
    		else if (pos.getLatRads() > latitude)
    			return d.describeNautical() + " north of " + getName();
    		else
    			return d.describeNautical() + " south of " + getName();
    	}

    	// The latitude, in radians, positive north.
    	private final double latitude;
 
    }
 
    
    /**
     * Class LON represents an interesting meridian, or a segment of
     * a meridian.
     */
    public static final class LON extends PointOfInterest {
    	
    	/**
    	 * Create a meridian of interest.
    	 * 
    	 * @param	lon			Longitude, in radians.
    	 * @param	n			Name of this meridian.
    	 */
    	LON(double lon, String n) {
    		super(n);
    		longitude = lon;
    		southLim = -HALFPI;
    		northLim = HALFPI;
    	}
    	
    	/**
    	 * Create a meridian segment of interest.
    	 * 
    	 * @param	lon			Longitude, in radians.
    	 * @param	slim		Southern limit of the segment, in radians.
    	 * @param	nlim		Northern limit of the segment, in radians.
    	 * @param	n			Name of this meridian.
    	 */
    	LON(double lon, double slim, double nlim, String n) {
    		super(n);
    		longitude = lon;
    		southLim = Math.min(slim, nlim);
    		northLim = Math.max(slim, nlim);
    	}
	
    	/**
    	 * Get the distance of a given point from this point of interest.
    	 * 
    	 * @param	pos			Point to measure from.
    	 * @return				The distance.
    	 * @see org.hermit.geo.PointOfInterest#distance(org.hermit.geo.Position)
    	 */
    	@Override
		public final Distance distance(Position pos) {
    		// See if we're in the latitude band of interest.
    		double lat = pos.getLatRads();
    		if (lat < southLim || lat > northLim)
    			return new Distance(DISTANCE_FAR);
    		
    		// Create a position that represents the meridian of interest,
    		// filling in the latitude from the caller's position.
    		// Note: this is incorrect, but simple, and not too bad over
    		// short distances except near the poles.
    		Position i = new Position(lat, longitude);
    		return pos.distance(i);
    	}
    	
    	/**
    	 * Describe the status of the given position relative to this
    	 * point of interest.
    	 * 
    	 * @param	pos			The position to describe.
    	 * @return				A string describing where pos is in relation
    	 * 						to this POI.
    	 */
    	@Override
		public final String status(Position pos) {
    		Distance d = distance(pos);
    		double nm = d.getNm();
    		if (nm <= DISTANCE_THRESH)
    			return getName();
    		else if (pos.getLonRads() > longitude)
    			return d.describeNautical() + " east of " + getName();
    		else
    			return d.describeNautical() + " west of " + getName();
    	}

    	// The longitude, positive east, and the south and
    	// north limits of the segment of interest, all in radians.
    	private final double longitude;
    	private final double southLim;
    	private final double northLim;
 
    }
 
 
    /**
     * Class BAND represents an interesting band of latitudes.
     */
    public static final class BAND extends PointOfInterest {
    	
    	/**
    	 * Create a meridian of interest.
    	 * 
    	 * @param	south		South limit (latitude) of the band, in radians.
    	 * @param	north		North limit (latitude) of the band, in radians.
    	 * @param	n			Name of this band.
    	 */
    	BAND(double south, double north, String n) {
    		super(n);
    		northLimit = Math.max(north, south);	// Just in case.
    		southLimit = Math.min(north, south);	// Just in case.
    	}
    	
    	/**
    	 * Get the distance of a given point from this point of interest.
    	 * 
    	 * @param	pos			Point to measure from.
    	 * @return				The distance.
    	 * @see org.hermit.geo.PointOfInterest#distance(org.hermit.geo.Position)
    	 */
    	@Override
		public final Distance distance(Position pos) {
    		double lat = pos.getLatRads();
    		
    		if (lat < southLimit)
    			return pos.latDistance(southLimit);
    		else if (lat > northLimit)
    			return pos.latDistance(northLimit);
    		else
    			return new Distance(0.0);
    	}

    	// The north and south limits, in radians, positive north.
    	private final double northLimit;
    	private final double southLimit;
 
    }
 

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a Position from a latitude and longitude.
	 * 
	 * @param	n			Name of this point / area..
	 */
	public PointOfInterest(String n) {
		name = n;
	}


	// ******************************************************************** //
	// Accessors.
	// ******************************************************************** //
   	
	/**
	 * Get the name of this point of interest.
	 * 
	 * @return				The name of this point.
	 */
	public final String getName() {
		return name;
	}
	
	
	/**
	 * Calculate the distance from the given position to this point
	 * of interest.
	 * 
	 * @param	pos			The position to calculate from.
	 * @return				The distance from pos to here.  Note: may be
	 *						inaccurate over larger distances, e.g. for
	 *						distance to a meridian.
	 */
	public abstract Distance distance(Position pos);

	
	/**
	 * Describe the status of the given position relative to this
	 * point of interest.
	 * 
	 * @param	pos			The position to describe.
	 * @return				A string describing where pos is in relation
	 * 						to this POI.
	 */
	public String status(Position pos) {
		Distance d = distance(pos);
		double nm = d.getNm();
		if (nm <= DISTANCE_THRESH)
			return name;
		else
			return d.describeNautical() + " from " + name;
	}


	// ******************************************************************** //
	// Static Methods.
	// ******************************************************************** //
   	
	/**
	 * Describe the status of the given position relative to global
	 * points of interest.
	 * 
	 * @param	pos			The position to describe.
	 * @return				A string describing where pos is in relation
	 * 						to the nearest known global POI.
	 */
	public static final String describePosition(Position pos) {
		// Get the status message for the region we're in, if any.
		String area = describeRegion(pos);
		
		// Find the closest point of interest.
		PointOfInterest best = null;
		double bestDistance = 0;
		PointOfInterest anyPoint = null;
		double anyDistance = 0;
		for (PointOfInterest poi : GLOBAL_POIS) {
			Distance d = poi.distance(pos);
			double nm = d.getNm();
			if (anyPoint == null || nm < anyDistance) {
				anyPoint = poi;
				anyDistance = nm;
			}
			if (nm <= DISTANCE_NEAR && (best == null || nm < bestDistance)) {
				best = poi;
				bestDistance = nm;
			}
		}

		// Get the status message for the closest point.  If we're right there,
		// kill the area description.
		String point = best != null ? best.status(pos) : null;
		if (best != null && bestDistance <= DISTANCE_THRESH)
			area = null;
		
		// Concatenate the area and point descriptions, if we have both.
		// If we have one, just use that.  If we have neither -- i.e.
		// nothing interesting in range -- then use the nearest point
		// we found.  This gives us the range to the tropics when in the
		// high temperate zones.
		if (area == null) {
			if (point == null) {
				if (anyPoint == null)
					return "The World";		// Really shouldn't ever get here.
				else
					return anyPoint.status(pos);
			} else
				return point;
		} else {
			if (point == null)
				return area;
			else
				return area + ", " + point;
		}
	}
	
   	
	/**
	 * Describe the status of the given position in terms of any region
	 * of interest it lies within.
	 * 
	 * @param	pos			The position to describe.
	 * @return				A string describing the region, if any, that
	 * 						pos is within.  Null if we aren't in an
	 * 						interesting region.
	 */
	public static final String describeRegion(Position pos) {
		// Find the general region we're in.
		for (PointOfInterest poi : GLOBAL_AREAS) {
			Distance d = poi.distance(pos);
			if (d.getNm() <= 0)
				return poi.status(pos);
		}

		return null;
	}

   	
	/**
	 * Describe the status of the given position relative to global
	 * points of interest.
	 * 
	 * @param	pos			The position to describe.
	 * @return				A string describing where pos is in relation
	 * 						to the nearest known global POI.  Null if we
	 * 						aren't close to anywhere interesting.
	 */
	public static final String describePoint(Position pos) {
		PointOfInterest best = null;
		double bestDistance = 0;

		// Find the closest point or area of interest.
		for (PointOfInterest poi : GLOBAL_POIS) {
			Distance d = poi.distance(pos);
			double nm = d.getNm();
			if (best == null || (nm <= DISTANCE_NEAR && nm < bestDistance)) {
				best = poi;
				bestDistance = nm;
			}
		}

		// Return our status relative to the closest point of interest.
		if (best != null)
			return best.status(pos);
		
		return null;
	}
	
	
    // ******************************************************************** //
    // Private Constants.
    // ******************************************************************** //

	// Distance in metres representing a long way away.
    private static final double DISTANCE_FAR =
    							GeoCalculator.MEAN_RADIUS * Math.PI;

	// Distance in nautical miles from a "special place" which is considered
    // to be within interesting range.
    private static final double DISTANCE_NEAR = 200;
	
    // Distance in nautical miles from a "special place" which is considered
    // to be there.
    private static final double DISTANCE_THRESH = 0.01;
	
	
	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //
	
	// Name of this point of interest.
	private final String name;

}

