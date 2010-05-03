
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
 * An immutable point in the plane.  This immutable class represents
 * a point as an X and Y co-ordinate.
 */
public class Point
    implements Comparable<Point>
{

    // ******************************************************************** //
    // Public Constants.
    // ******************************************************************** //

    /**
     * A constant representing a point at infinity.  This is the only Point
     * whose co-ordinates can be infinite.
     */
    public static final Point INFINITE =
            new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true);

    /**
     * A constant representing an unknown point (the 2-D equivalent of NaN).
     * This is the only Point whose co-ordinates can be NaN.
     */
    public static final Point UNKNOWN = new Point(Double.NaN, Double.NaN, true);

    /**
     * Index value which specifies the X co-ordinate within a point.
     * @see #getComponent(int)
     */
    public static final int X_INDEX = 0;

    /**
     * Index value which specifies the Y co-ordinate within a point.
     * @see #getComponent(int)
     */
    public static final int Y_INDEX = 1;
   

    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

    /**
     * Create a Point from individual co-ordinates.
     * 
     * @param   x           The X co-ordinate.
     * @param   y           The Y co-ordinate.
     */
    public Point(double x, double y) {
       this(x, y, false);
    }


    /**
     * Create a Point from individual co-ordinates.
     * 
     * @param   x           The X co-ordinate.
     * @param   y           The Y co-ordinate.
     * @param   allowOdd    Iff true, allow infinities and NaNs.
     */
    private Point(double x, double y, boolean allowOdd) {
        if (!allowOdd) {
            if (Double.isInfinite(x) || Double.isInfinite(y))
                throw new IllegalArgumentException("Infinite co-ordinates" +
                                                   " not allowed in a Point.");
            if (Double.isNaN(x) || Double.isNaN(y))
                throw new IllegalArgumentException("NaN co-ordinates" +
                                                   " not allowed in a Point.");
        }
        
        this.x = x;
        this.y = y;
    }


    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Determine whether this point is infinite.
     * 
     * @return          True if this point is at infinity.
     */
    public boolean isInfinite() {
        return Double.isInfinite(x) || Double.isInfinite(y);
    }


    /**
     * Determine whether this point is NaN.
     * 
     * @return          True if this point is an undefined point.
     */
    public boolean isNaN() {
        return Double.isNaN(x) || Double.isNaN(y);
    }


    /**
     * Get the X co-ordinate of this point.
     * 
     * @return          The X co-ordinate of this point.
     */
    public double getX() {
        return x;
    }


    /**
     * Get the Y co-ordinate of this point.
     * 
     * @return          The Y co-ordinate of this point.
     */
    public double getY() {
        return y;
    }


    /**
     * Get the specified co-ordinate of this point.  This method is useful
     * where Points are passed to methods which can work on either
     * co-ordinate.
     * 
     * @param   i       Index of the desired co-ordinate; either
     *                  {@link #X_INDEX} or {@link #Y_INDEX}.
     * @return          The specified co-ordinate of this point.
     * @throws  IllegalArgumentException  Bad index.
     */
    public double getComponent(int i) throws IllegalArgumentException {
        if (i == X_INDEX)
            return x;
        else if (i == Y_INDEX)
            return y;
        throw new IllegalArgumentException("Invalid index " + i + " in getComponent");
    }


    /**
     * Get the X co-ordinate of this point as a float.  This can be
     * convenient for graphics methods, for example.
     * 
     * @return          The X co-ordinate of this point as a float.
     */
    public float getXf() {
        return (float) x;
    }


    /**
     * Get the Y co-ordinate of this point as a float.  This can be
     * convenient for graphics methods, for example.
     * 
     * @return          The Y co-ordinate of this point as a float.
     */
    public float getYf() {
        return (float) y;
    }


    // ******************************************************************** //
    // Calculations.
    // ******************************************************************** //

    /**
     * Calculate the distance between this point and another.
     * 
     * @param   o       The other point.
     * @return          The scalar distance between this point
     *                  and o -- always positive.
     */
    public double dist(Point o) {
        final double dx = x - o.x;
        final double dy = y - o.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


    // ******************************************************************** //
    // Static Methods.
    // ******************************************************************** //

    /**
     * Calculate the Vector between two points.
     * 
     * @param   a       The starting point.
     * @param   b       The ending point.
     * @return          A Vector which would translate a to b.
     */
    public static Vector vector(Point a, Point b) {
        return new Vector(b.x - a.x, b.y - a.y);
    }


    /**
     * Calculate the midpoint between two points.
     * 
     * @param   a       One point.
     * @param   b       The other point.
     * @return          The point midway between a and b.
     */
    public static Point mid(Point a, Point b) {
        return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    
    // ******************************************************************** //
    // Sorting Support.
    // ******************************************************************** //

    /**
     * Indicates whether some other object is "equal to" this one.
     * This method implements an equivalence relation
     * on non-null object references.
     * 
     * <p>This method simply compares the co-ordinates of the two points,
     * with a limited precision.
     * 
     * <p>Note that the precision of the test is limited by the precision
     * set in {@link MathTools#setPrecision(double)}.  That is, only as
     * many fractional digits are compared as configured there; hence,
     * two very close points will be considered equal.
     *
     * @param   obj         The reference object with which to compare.
     * @return              true if this object is the same as the obj
     *                      argument, to within the given precision;
     *                      false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point))
            return false;

        final Point o = (Point) obj;
        return MathTools.eq(x, o.x) && MathTools.eq(y, o.y);
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 
     * <p>This method compares the co-ordinates of the two points,
     * with a limited precision.  The Y co-ordinate is given precedence;
     * that is, the smaller Y will be considered less than the larger Y.
     * If the Y values are equal to within the configured precision, then the X
     * values are compared.
     * 
     * <p>Note that the precision of the test is limited by the precision
     * set in {@link MathTools#setPrecision(double)}.  That is, only as
     * many fractional digits are compared as configured there; hence,
     * two very close points will be considered equal.
     *
     * @param   o           The object to be compared to this one.
     * @return              A negative integer, zero, or a positive
     *                      integer as this object is less than, equal
     *                      to, or greater than the specified object.
     * @throws  ClassCastException  The specified object's type prevents it
     *                      from being compared to this object.
     */
    public int compareTo(Point o) {
        // Note: in the following, we check for less than *AND* greater than,
        // AFTER eliminating equality.  The other option is that the values
        // are infinite; hence these tests are required.
        if (!MathTools.eq(y, o.y)) {
            if (y < o.y)
                return -1;
            else if (y > o.y)
                return 1;
        } else if (!MathTools.eq(x, o.x)) {
            if (x < o.x)
                return -1;
            else if (x > o.x)
                return 1;
        }
        return 0;
    }


    /**
     * Returns a hash code value for the object. This method is 
     * supported for the benefit of hashtables. 
     *
     * <p>The hash code returned here is based on the co-ordinates of this
     * Point, and is designed to be different for different points.
     * The least significant bits of the co-ordinates are not compared,
     * in line with the precision set in
     * {@link MathTools#setPrecision(double)}.  Hence, this method should
     * be consistent with equals() and compareTo().
     *
     * @return              A hash code value for this object.
     */
    @Override
    public int hashCode() {
        long xb = Double.doubleToLongBits(MathTools.round(x));
        long yb = Double.doubleToLongBits(MathTools.round(y));
        
        // Fold the co-ordinates into 32 bits.  At the same time don't
        // map equivalent bits of x and y into the same bits of the hash.
        return (int) xb ^ (int) (xb >> 32) ^
                (int) (yb >> 53) ^ (int) (yb >> 21) ^ (int) (yb << 11);
    }


    // ******************************************************************** //
    // Utilities.
    // ******************************************************************** //

    /**
     * Convert this instance to a String suitable for display.
     * 
     * @return String representation of this instance.
     */
    @Override
    public String toString() {
        return "<" + x + "," + y + ">";
    }


    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    // The coordinates of this point.
    private final double x;
    private final double y;

}

