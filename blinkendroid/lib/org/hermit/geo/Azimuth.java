
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


import static java.lang.Math.toRadians;

import org.hermit.utils.Angle;


/**
 * This class represents a geographic azimuth -- ie. a compass heading
 * from or to a given geographic position.
 *
 * @author	Ian Cameron Smith
 */
public final class Azimuth
	extends Angle
{

	// ******************************************************************** //
	// Public Constructors.
	// ******************************************************************** //

	/**
	 * Create an Azimuth from an azimuth given in radians.
	 * 
	 * @param	radians		Source azimuth in radians, clockwise from north.
	 */
	public Azimuth(double radians) {
		// Normalize to the range 0 <= radians < 2 * PI.
		super(modTwoPi(radians));
	}

	
	// ******************************************************************** //
	// Accessors and Converters.
	// ******************************************************************** //

	/**
	 * Create a Azimuth from an azimuth given in degrees.
	 * 
	 * @param	degrees		Source azimuth in degrees, clockwise from north.
	 * @return				The new Azimuth.
	 */
	public static Azimuth fromDegrees(double degrees) {
		return new Azimuth(toRadians(degrees));
	}


	// ******************************************************************** //
	// Azimuth Arithmetic.
	// ******************************************************************** //

	/**
	 * Calculate the azimuth which is the given angular offset from this one.
	 * 
	 * @param	radians		Offset to add to this Azimuth, in radians;
	 * 						positive is clockwise from north, may be
	 * 						negative.
	 * @return				Azimuth which is equal to this Azimuth plus
	 * 						the given offset.  Overflow is taken care of.
	 */
	@Override
	public Azimuth add(double radians) {
		// The constructor takes care of normalization.
		return new Azimuth(getRadians() + radians);
	}

}

