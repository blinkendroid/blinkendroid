
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
 * An immutable vector in the plane.  This immutable class represents
 * a vector as an X and Y offset.
 */
public class Vector implements Comparable<Vector> {

    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

    /**
     * Create a Vector from individual offsets.
     * 
     * @param   x           The X offset.
     * @param   y           The Y offset.
     */
    public Vector(double x, double y) {
       this.x = x;
       this.y = y;
    }


    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Get the X component of this vector.
     * 
     * @return          The X component of this vector.
     */
    public double getX() {
        return x;
    }


    /**
     * Get the Y component of this vector.
     * 
     * @return          The Y component of this vector.
     */
    public double getY() {
        return y;
    }


    /**
     * Get the length of this vector.
     * 
     * @return          The length of this vector.
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }


    // ******************************************************************** //
    // Static Methods.
    // ******************************************************************** //

    /**
     * Calculate the vector sum of two vectors.
     * 
     * @param   a       The first vector to add.
     * @param   b       The second vector.
     * @return          The sum.
     */
    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }


    /**
     * Calculate the vector difference of two vectors.
     * 
     * @param   a       The first vector to add.
     * @param   b       The second vector.
     * @return          The difference, a - b.
     */
    public static Vector sub(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }


    /**
     * Scale a vector by a given value.
     * 
     * @param   vec     The vector to scale.
     * @param   scale   The value to scale by.
     * @return          The product of vec and scale.
     */
    public static Vector scale(Vector vec, double scale) {
        return new Vector(vec.x * scale, vec.y * scale);
    }


    // ******************************************************************** //
    // Sorting Support.
    // ******************************************************************** //

    /**
     * Indicates whether some other object is "equal to" this one.
     * This method implements an equivalence relation
     * on non-null object references.
     * 
     * <p>This method simply compares the components of the two vectors,
     * with a limited precision.
     * 
     * <p>Note that the precision of the test is limited by the precision
     * set in {@link MathTools#setPrecision(double)}.  That is, only as
     * many fractional digits are compared as configured there; hence,
     * two very close vectors will be considered equal.
     *
     * @param   obj         The reference object with which to compare.
     * @return              true if this object is the same as the obj
     *                      argument, to within the given precision;
     *                      false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Vector))
            return false;

        final Vector o = (Vector) obj;
        return MathTools.eq(x, o.x) && MathTools.eq(y, o.y);
    }


    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 
     * <p>This method compares the components of the two vectors,
     * with a limited precision.  The Y component is given precedence;
     * that is, the smaller Y will be considered less than the larger Y.
     * If the Y values are equal to within the configured precision, then the X
     * values are compared.
     * 
     * <p>Note that the precision of the test is limited by the precision
     * set in {@link MathTools#setPrecision(double)}.  That is, only as
     * many fractional digits are compared as configured there; hence,
     * two very close vectors will be considered equal.
     *
     * @param   o           The object to be compared to this one.
     * @return              A negative integer, zero, or a positive
     *                      integer as this object is less than, equal
     *                      to, or greater than the specified object.
     * @throws  ClassCastException  The specified object's type prevents it
     *                      from being compared to this object.
     */
    public int compareTo(Vector o) {
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
     * <p>The hash code returned here is based on the components of this
     * Vector, and is designed to be different for different vectors.
     * The least significant bits of the components are not compared,
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

    // The components of this Vector.
    private final double x;
    private final double y;

}

