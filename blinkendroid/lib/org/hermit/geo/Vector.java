
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
 * This class represents a vector over the Earth's surface -- ie. a
 * distance and azimuth.
 *
 * @author	Ian Cameron Smith
 */
public final class Vector
{

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a Vector from a given distance and azimuth.
	 * 
	 * @param	distance		Source distance.
	 * @param	azimuth			Source azimuth.
	 */
	public Vector(Distance distance, Azimuth azimuth) {
		this.distance = distance;
		this.azimuth = azimuth;
	}

	
	// ******************************************************************** //
	// Accessors and Converters.
	// ******************************************************************** //

	/**
	 * Create a Vector from distance in metres and an azimuth given in radians.
	 * 
	 * @param	metres		Source distance in metres.
	 * @param	radians		Source azimuth in radians, clockwise from north.
	 * @return				The new Vector.
	 */
	public static Vector fromMetresRadians(double metres, double radians) {
		return new Vector(new Distance(metres), new Azimuth(radians));
	}


	/**
	 * Create a Vector from distance in nautical miles and an azimuth
	 * given in radians.
	 * 
	 * @param	nmiles		Source distance in nautical miles.
	 * @param	radians		Source azimuth in radians, clockwise from north.
	 * @return				The new Vector.
	 */
	public static Vector fromNmRadians(double nmiles, double radians) {
		return new Vector(Distance.fromNm(nmiles), new Azimuth(radians));
	}

	
	/**
	 * Get the azimuth.
	 *
	 * @return				The azimuth.
	 */
	public final Azimuth getAzimuth() {
		return azimuth;
	}


	/**
	 * Get the azimuth in radians.
	 *
	 * @return				The azimuth in radians, clockwise from north.
	 * 						This will be in the range 0 <= radians < 2 * PI.
	 */
	public final double getAzimuthRadians() {
		return azimuth.getRadians();
	}


	/**
	 * Get the azimuth in degrees.
	 *
	 * @return				The azimuth in degrees, clockwise from north.
	 * 						This will be in the range 0 <= degrees < 360.0.
	 */
	public final double getAzimuthDegrees() {
		return azimuth.getDegrees();
	}


	/**
	 * Get the distance.
	 *
	 * @return				The distance.
	 */
	public final Distance getDistance() {
		return distance;
	}


	/**
	 * Get the distance in metres.
	 *
	 * @return				The distance in metres.
	 */
	public final double getDistanceMetres() {
		return distance.getMetres();
	}


	/**
	 * Get the distance in nautical miles.
	 *
	 * @return				The distance in nautical miles.
	 */
	public final double getDistanceNm() {
		return distance.getNm();
	}

	
	// ******************************************************************** //
	// Formatting.
	// ******************************************************************** //

    /**
     * Format this vector for user display in degrees and minutes.
     *
     * @return              The formatted vector.
     */
    public String formatDegMin() {
        return distance.formatM() + ' ' + azimuth.formatDegMin();
    }


    /**
     * Format this vector for user display in degrees and minutes.
     *
     * @return              The formatted vector.
     */
    public String formatDegMinSec() {
        return distance.formatM() + ' ' + azimuth.formatDegMinSec();
    }

    
    /**
     * Format this vector as a String.
     * 
     * @return          This vector as a string, in degrees and minutes.
     */
    @Override
    public String toString() {
        return formatDegMin();
    }
    

	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //

	/**
	 * The distance.
	 */
	private Distance distance;

	/**
	 * The azimuth.
	 */
	private Azimuth azimuth;

}

