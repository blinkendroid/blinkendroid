
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
 * An immutable edge in a geometric graph.
 * 
 * <p>This immutable class embodies an edge in a graph where the vertices are
 * assumed to be points in the plane.  Either or both of the vertices
 * may be at infinity, in which case this class can provide the position
 * and direction of the line representing the edge.
 */
public class Edge
    implements Comparable<Edge>
{

    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

    /**
     * Create an Edge from individual vertices.
     * 
     * @param   a           One vertex, in no particular order.  Must be
     *                      a real Point.
     * @param   b           The other vertex.  Must be a real Point.
     */
    public Edge(Point a, Point b) {
        if (a.isNaN() || b.isNaN())
            throw new IllegalArgumentException("Undefined vertices" +
                                               " not allowed in an Edge.");
        if (a.isInfinite() || b.isInfinite())
            throw new IllegalArgumentException("Infinite co-ordinates" +
                                               " not allowed in an Edge.");
        
        VVertexA = a;
        VVertexB = b;
        leftDatum = null;
        rightDatum = null;
    }
    

    /**
     * Create an Edge from two data points and individual vertices.
     * 
     * @param   ld          One data point for the edge.  This is a point
     *                      which the edge passes through, and defines
     *                      the position and direction of an infinite edge.
     * @param   rd          Other data point for the edge.
     * @param   a           One vertex, in no particular order.  Must be
     *                      Point.INFINITE or a real Point.
     * @param   b           The other vertex.  May be
     *                      Point.INFINITE or a real Point.
     */
    public Edge(Point a, Point b, Point ld, Point rd) {
        if (a.isNaN() || b.isNaN())
            throw new IllegalArgumentException("Undefined vertices" +
                                               " not allowed in an Edge.");
        if (ld.isNaN() || rd.isNaN())
            throw new IllegalArgumentException("Undefined data points" +
                                               " not allowed in an Edge.");
        if (ld.isInfinite() || rd.isInfinite())
            throw new IllegalArgumentException("Infinite data points" +
                                               " not allowed in an Edge.");
        
        leftDatum = ld;
        rightDatum = rd;
        VVertexA = a;
        VVertexB = b;
    }
    

    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Get one vertex of this edge (denoted "A").
     * 
     * @return              The "A" vertex of this edge.  May be
     *                      Point.INFINITE or a real Point.
     */
    public Point getVertexA() {
        return VVertexA;
    }
    

    /**
     * Get one vertex of this edge (denoted "B").
     * 
     * @return              The "B" vertex of this edge.  May be
     *                      Point.INFINITE or a real Point.
     */
    public Point getVertexB() {
        return VVertexB;
    }
    

    /**
     * Get one datum point of this edge (denoted "A").
     * 
     * @return              The "A" datum of this edge.  Will be null
     *                      if there is no datum, as may be the case for
     *                      non-infinite edges.
     */
    public Point getDatumA() {
        return leftDatum;
    }
    

    /**
     * Get one datum point of this edge (denoted "B").
     * 
     * @return              The "B" datum of this edge.  Will be null
     *                      if there is no datum, as may be the case for
     *                      non-infinite edges.
     */
    public Point getDatumB() {
        return rightDatum;
    }
    

    /**
     * Determine whether this edge is infinite.
     * 
     * @return              True if both vertices are infinite; false else.
     */
    public boolean isInfinite() {
        return VVertexA == Point.INFINITE && VVertexB == Point.INFINITE;
    }


    /**
     * Determine whether this edge is partly infinite.
     * 
     * @return              True if either vertex is infinite; false else.
     */
    public boolean isPartlyInfinite() {
        return VVertexA == Point.INFINITE || VVertexB == Point.INFINITE;
    }


    /**
     * Get a reference point which fixes the position of this edge.
     * 
     * @return              A reference point which can be used to fix the
     *                      position of this edge.  If the edge is fully
     *                      infinite, this will be some point which the edge
     *                      passes through; if the edge is partly infinite,
     *                      it will be the non-infinite vertex; otherwise
     *                      it will be one of the vertices.
     */
    public Point referencePoint() {
        if (isInfinite())
            return Point.mid(leftDatum, rightDatum);
        if (VVertexA != Point.INFINITE)
            return VVertexA;
        return VVertexB;
    }


    /**
     * Get the direction vector for this edge.  If the edge is partly
     * or fully infinite, this can be used to draw the edge in conjunction
     * with referencePoint().
     * 
     * @return              The direction vector for this edge.
     */
    public Vector directionVector() {
        // If this is a non-infinite edge, calculate the direction based
        // on the two vertices.  Scale to a unit vector.
        if (!isPartlyInfinite()) {
            final Vector diff = Point.vector(VVertexA, VVertexB);
            return Vector.scale(diff, 1.0 / diff.length());
        }

        // Base it on the passed-through points.
        final double lx = leftDatum.getX();
        final double ly = leftDatum.getY();
        final double rx = rightDatum.getX();
        final double ry = rightDatum.getY();

        // If the edge is vertical, we have to handle it specially to avoid
        // divide by zero below.  Return a unit vector either up or
        // down as appropriate.
        if (lx == rx) {
            if (ly < ry)
                return new Vector(-1, 0);
            else
                return new Vector(1, 0);
        }
        
        // Calculate the direction from the two direction points.  Scale
        // to a unit vector.
        final double x = -(ry - ly) / (rx - lx);
        final double y = 1;
        final double len = Math.sqrt(x * x + y * y);
        final double scale = 1.0 / len * (rx < lx ? -1 : 1);
        return new Vector(x * scale, y * scale);
    }


    /**
     * Get the length of this edge.
     * 
     * @return              The length of this edge; this will be
     *                      Double.POSITIVE_INFINITY if the edge is infinite
     *                      or partly infinite.
     */
    public double length() {
        if (isPartlyInfinite())
            return Double.POSITIVE_INFINITY;
        return VVertexA.dist(VVertexB);
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
     * <p>This comparison has little objective value; it is used to enforce
     * a natural ordering on edges, so that arrays of edges can be compared
     * easily for equality.  This is in turn used for testing.  Because
     * compareTo() only compares vertices, so does this method.
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
        if (obj == null || !(obj instanceof Edge))
            return false;

        final Edge o = (Edge) obj;
        
        return VVertexA.equals(o.VVertexA) && VVertexB.equals(o.VVertexB);
    }


    /**
     * Compare an edge against another edge.  The comparison is based on
     * comparing the two vertices, as per {@link Point#compareTo(Point)}.
     * 
     * <p>Note that the precision of the test is limited by the precision
     * set in {@link MathTools#setPrecision(double)}.  That is, only as
     * many fractional digits are compared as configured there; hence,
     * two very close points will be considered equal.
     * 
     * <p>This comparison has little objective value; it is used to enforce
     * a natural ordering on edges, so that arrays of edges can be compared
     * easily for equality.  This is in turn used for testing.
     * 
     * @param   ev          The other edge to compare to.
     * @return              A negative, zero or positive integer, as this
     *                      edge is less than, equal to, or greater than ev.
     */
    public int compareTo(Edge ev) {
        Point me1, me2;
        if (VVertexA.compareTo(VVertexB) < 0) {
            me1 = VVertexA;
            me2 = VVertexB;
        } else {
            me1 = VVertexB;
            me2 = VVertexA;
        }

        Point o1, o2;
        if (ev.VVertexA.compareTo(ev.VVertexB) < 0) {
            o1 = ev.VVertexA;
            o2 = ev.VVertexB;
        } else {
            o1 = ev.VVertexB;
            o2 = ev.VVertexA;
        }

        int stat = me1.compareTo(o1);
        if (stat == 0)
            stat = me2.compareTo(o2);
        return stat;
    }


    /**
     * Returns a hash code value for the object. This method is 
     * supported for the benefit of hashtables. 
     *
     * <p>The hash code returned here is based on the hash codes of
     * the vertices.  See {@link Point#hashCode()}.  This means that
     * the least significant bits of the co-ordinates are not compared,
     * in line with the precision set in
     * {@link MathTools#setPrecision(double)}.  Hence, this method should
     * be consistent with equals() and compareTo().
     *
     * @return              A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return VVertexA.hashCode() ^ VVertexB.hashCode();
    }


    // ******************************************************************** //
    // Utilities.
    // ******************************************************************** //

    /**
     * Convert this instance to a String suitable for display.
     * 
     * @return             String representation of this instance.
     */
    @Override
    public String toString() {
        return "{" + VVertexA + "," + VVertexB +
                    ":L=" + leftDatum + ",R=" + rightDatum + "}";
    }


    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //
    
    private final Point leftDatum;
    private final Point rightDatum;

    private final Point VVertexA;

    private final Point VVertexB;

}

