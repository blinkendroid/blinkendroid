
/**
 * spline: routines for spline interpolation.
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


package org.hermit.geometry.spline;


/**
 * Representation of a cubic polynomial.
 */
public final class Cubic {
    
    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

    /**
     * Create a cubic polynomial of form a + b*x + c*x^2 + d*x^3.
     * 
     * @param   a       A coefficient.
     * @param   b       B coefficient.
     * @param   c       C coefficient.
     * @param   d       D coefficient.
     */
    public Cubic(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    
    // ******************************************************************** //
    // Evaluation.
    // ******************************************************************** //

    /**
     * Evaluate the polynomial for a given value.
     * 
     * @param   x       X value to evaluate for.
     * @return          The value of the polynomial for the given X.
     */
    public double eval(double x) {
        return ((d * x + c) * x + b) * x + a;
    }

    
    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //
   
    // The coefficients.
    private final double a, b, c, d;

}

