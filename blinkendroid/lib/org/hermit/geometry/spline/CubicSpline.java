
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


import org.hermit.geometry.Point;


/**
 * Implementation of a natural cubic spline.
 */
public class CubicSpline
{
    
    // ******************************************************************** //
    // Constructor.
    // ******************************************************************** //

    /**
     * Create a cubic spline curve with a specified set of control points.
     * 
     * @param   points  The control points for this curve.
     */
    public CubicSpline(Point[] points) {
        final int len = points.length;
        if (len < 2)
            throw new IllegalArgumentException("A ControlCurve needs" + 
                                               " at least 2 control points");
        controlPoints = points;
        
        // Flatten out the control points array.
        controlsX = new double[len];
        controlsY = new double[len];
        for (int i = 0; i < len; ++i) {
            controlsX[i] = controlPoints[i].getX();
            controlsY[i] = controlPoints[i].getY();
       }
        
        // Calculate the gamma values just once.
        final int n = controlPoints.length - 1;
        double[] gamma = new double[n + 1];
        gamma[0] = 1.0 / 2.0;
        for (int i = 1; i < n; ++i)
            gamma[i] = 1 / (4 - gamma[i - 1]);
        gamma[n] = 1 / (2 - gamma[n - 1]);

        // Calculate the cubic segments.
        cubicX = calcNaturalCubic(n, controlsX, gamma);
        cubicY = calcNaturalCubic(n, controlsY, gamma);
    }

    
    // ******************************************************************** //
    // Spline Calculation.
    // ******************************************************************** //

    /**
     * Calculate the natural cubic spline that interpolates x[0-n].
     *     
     * @return          The spline as a set of cubic segments, each over the
     *                  range x = [0-1[.
     */
    private Cubic[] calcNaturalCubic(int n, double[] x, double[] gamma) {
        double[] delta = new double[n + 1];
        delta[0] = 3 * (x[1] - x[0]) * gamma[0];
        for (int i = 1; i < n; ++i)
            delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
        delta[n] = (3 * (x[n] - x[n - 1])-delta[n - 1]) * gamma[n];

        double[] D = new double[n + 1];
        D[n] = delta[n];
        for (int i = n - 1; i >= 0; --i) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        // Calculate the cubic segments.
        Cubic[] C = new Cubic[n];
        for (int i = 0; i < n; i++) {
            final double a = x[i];
            final double b = D[i];
            final double c = 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1];
            final double d = 2 * (x[i] - x[i + 1]) + D[i] + D[i + 1];
            C[i] = new Cubic(a, b, c, d);
        }
        return C;
    }


    /**
     * Interpolate the spline.
     * 
     * @param   steps   The number of steps to interpolate in each segment.
     * @return          The interpolated values.
     */
    public Point[] interpolate(int steps) {
        Point[] p = new Point[cubicX.length * steps + 1];
        int np = 0;
        
        /* very crude technique - just break each segment up into steps lines */
        p[np++] = new Point(cubicX[0].eval(0), cubicY[0].eval(0));
        for (int i = 0; i < cubicX.length; i++) {
            for (int j = 1; j <= steps; j++) {
                double x = (double) j / (double) steps;
                p[np++] = new Point(cubicX[i].eval(x), cubicY[i].eval(x));
            }
        }
        return p;
    }


    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //
    
    // The control points for this curve.
    private final Point[] controlPoints;
    
    // The X and Y co-ordinates of the control points.
    private final double[] controlsX;
    private final double[] controlsY;

    // Cubic spline segments.  Each segment is a polynomial over the
    // range x = [0-1[.
    private Cubic[] cubicX;
    private Cubic[] cubicY;
 
}

