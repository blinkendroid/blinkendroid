
/**
 * geometry: basic geometric classes.
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


package org.hermit.geometry;


/**
 * Mathematical utilities for geometric calculations.
 * 
 * <p>This package provides mathematical comparisons which ignore the
 * lowest bits of the provided values.  This is useful when comparing
 * values which should be equal, except for floating-point representability
 * and rounding.
 */
public class MathTools {

    // ******************************************************************** //
    // Configuration.
    // ******************************************************************** //

    /**
     * Set the precision for calculations.  The precision is given
     * as a scaling factor; values are scaled by this value and rounded to 1.
     * 
     * @param   val         The precision as a scaling factor.
     */
    public static final void setPrecision(double val) {
        precision = val;
    }


    // ******************************************************************** //
    // Comparisons.
    // ******************************************************************** //

    /**
     * Return the given value rounded according to the current precision.
     * 
     * @param   val         The value to round.
     * @return              The rounded value.
     */
    public static final double round(double val) {
        return Math.rint(val * precision) / precision;
    }


    /**
     * Determine whether two values are equal to within the current precision.
     * 
     * @param   a           One value to compare.
     * @param   b           The other value to compare.
     * @return              True if the values do not differ within the
     *                      current precision.
     */
    public static final boolean eq(double a, double b) {
        return Math.abs(a - b) * precision < 1.0;
    }


    /**
     * Determine whether a value is less than another to within the current precision.
     * 
     * @param   a           One value to compare.
     * @param   b           The other value to compare.
     * @return              True if a < b by at least the current precision.
     */
    public static final boolean lt(double a, double b) {
        return (b - a) * precision > 1.0;
    }

    
    // ******************************************************************** //
    // Private Class Data.
    // ******************************************************************** //

    // The required precision for comparisons, expressed as a
    // scaling factor; values are scaled by this value and rounded to 1.
    private static double precision = 10000000000.0;

}

