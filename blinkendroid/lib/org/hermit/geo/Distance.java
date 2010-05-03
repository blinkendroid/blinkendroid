
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

import java.text.NumberFormat;


/**
 * This class represents a geographic distance -- ie. a distance
 * from or to a given geographic position.
 *
 * @author	Ian Cameron Smith
 */
public final class Distance
{

	// ******************************************************************** //
	// Public Constants.
	// ******************************************************************** //

	/**
	 * A distance equal to zero.
	 */
	public static final Distance ZERO = new Distance(0);
	
	
	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create a Distance from a value given in metres.
	 * 
	 * @param	metres		Source distance in metres.
	 */
	public Distance(double metres) {
		distanceM = metres;
	}

	
	// ******************************************************************** //
	// Accessors and Converters.
	// ******************************************************************** //

	/**
	 * Create a Distance from a distance given in feet.
	 * 
	 * @param	feet		Source distance in feet.
	 * @return				The new Distance.
	 */
	public static Distance fromFeet(double feet) {
		return new Distance(feet * FOOT);
	}


	/**
	 * Create a Distance from a distance given in nautical miles.
	 * 
	 * @param	nmiles		Source distance in nautical miles.
	 * @return				The new Distance.
	 */
	public static Distance fromNm(double nmiles) {
		return new Distance(nmiles * NAUTICAL_MILE);
	}


	/**
	 * Get the distance in metres.
	 *
	 * @return				The distance in metres.
	 */
	public final double getMetres() {
		return distanceM;
	}


	/**
	 * Get the distance in feet.
	 *
	 * @return				The distance in feet.
	 */
	public final double getFeet() {
		return distanceM / FOOT;
	}


	/**
	 * Get the distance in nautical miles.
	 *
	 * @return				The distance in nautical miles.
	 */
	public final double getNm() {
		return distanceM / NAUTICAL_MILE;
	}


    // ******************************************************************** //
    // Arithmetic.
    // ******************************************************************** //

	/**
	 * Return the sum of this and another Distance.
	 * 
	 * @param	d			Distance to add to this one.
	 * @return				Distance representing the sum of the two
	 * 						distances.
	 */
	public Distance add(Distance d) {
		if (d == null || d == ZERO)
			return this;
		if (this == ZERO)
			return d;
		return new Distance(distanceM + d.distanceM);
	}


    // ******************************************************************** //
    // Formatting.
    // ******************************************************************** //

    /**
     * Format this position for user display in metres.
     *
     * @return              The formatted distance.
     */
    public String formatM() {
		floatFormat.setMaximumFractionDigits(1);
		return floatFormat.format(getMetres()) + " m";
    }


    /**
     * Format this position for user display in nautical miles.
     *
     * @return              The formatted distance.
     */
    public String formatNm() {
		floatFormat.setMaximumFractionDigits(1);
		return floatFormat.format(getNm()) + " nm";
    }


	/**
	 * Convert this distance into a descriptive string, using
	 * nautical measures.
	 * 
	 * @return				Description of this distance.  Examples:
	 * 						"625 feet", "4.9 naut. miles", "252 naut. miles".
	 */
    public String describeNautical() {
		double feet = getFeet();
		double nm = getNm();
		if (feet < 1000)
			return "" + (int) Math.round(feet) + " feet";
		if (nm < 10) {
			floatFormat.setMaximumFractionDigits(1);
			return floatFormat.format(nm) + " nm";
		}
		return "" + (int) Math.round(nm) + " nm";
	}
	

    /**
     * Format this position as a String.
     * 
     * @return          This position as a string, in nautical miles.
     */
    @Override
    public String toString() {
        return formatNm();
    }
    
    
	// ******************************************************************** //
	// Private Constants.
	// ******************************************************************** //

	// The length of an international standard foot, in metres.
	private static final double FOOT = 0.3048;

	// The length of an international standard nautical mile, in metres.
	private static final double NAUTICAL_MILE = 1852;
	

	// ************************************************************************ //
	// Class Data.
	// ************************************************************************ //

	// Number formatter for floating-point values.
	private static NumberFormat floatFormat = null;
	static {
		// Set up the number formatter for floating-point values.
		floatFormat = NumberFormat.getInstance();
		floatFormat.setMinimumFractionDigits(0);
		floatFormat.setMaximumFractionDigits(1);
	}
	
	
	// ******************************************************************** //
	// Private Member Data.
	// ******************************************************************** //

	/**
	 * The distance in metres.
	 */
	private double distanceM;

}

