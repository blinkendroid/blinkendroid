
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
 * Global constants for geodetic calculations.
 *
 * @author	Ian Cameron Smith
 */
public interface GeoConstants
{

    // ******************************************************************** //
    // Public Constants.
    // ******************************************************************** //
	
	/**
	 * An approximation of the mean radius of the Earth in metres.
	 * For spherical-Earth formulae, this is good enough.
	 */
	public static final double MEAN_RADIUS = 6371000.0;
	
	/**
	 * The equatorial radius of the Earth in metres.
	 */
	public static final double EQUATORIAL_RADIUS = 6378137.0;
	
	/**
	 * The polar radius of the Earth in metres.
	 */
	public static final double POLAR_RADIUS = 6356755.0;


	/**
	 * Selectable ellipsoids, for geodetic calculations.
	 */
	public enum Ellipsoid {
		/** Pseudo-ellipsoid for an assumed spherical Earth. */
		SPHERE("Sphere",				MEAN_RADIUS,	0.0),
		
		/** WGS 84 ellipsoid. */
		WGS84("GRS80 / WGS84 (NAD83)",	6378137,		1.0 / 298.25722210088),
		
		/** Clarke 1866 (NAD27) ellipsoid. */
		NAD27("Clarke 1866 (NAD27)",	6378206.4,		1.0 / 294.9786982138),
		
		/** Airy 1858 ellipsoid. */
		AIRY1858("Airy 1858",			6377563.396,	1.0 / 299.3249646),
		
		/** Airy Modified ellipsoid. */
		AIRYM("Airy Modified",			6377340.189,	1.0 / 299.3249646),
		
		/** NWL-9D (WGS 66) ellipsoid. */
		WGS66("NWL-9D (WGS 66)",		6378145,		1.0 / 298.25),
		
		/** WGS 72 ellipsoid. */
		WGS72("WGS 72",					6378135,		1.0 / 298.26);

		Ellipsoid(String n, double a, double f) {
			name = n;
			axis = a;
			flat = f;
		}

		/** User-visible name of this ellipsoid. */
		public final String name;

		/** Equatorial semimajor axis of this ellipsoid (in metres). */
		public final double axis;

		/** Flattening of this ellipsoid. */
		public final double flat;

	}

}

