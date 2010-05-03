
/**
 * bentools: Voronoi diagram generator.  This is Benjamin Dittes'
 * C# implementation of Fortune's algorithm, translated to Java
 * by Ian Cameron Smith.
 * 
 * <p>The only license info I can see: "If you ever need a voronoi
 * clustering in C#, feel free to use my solution here."  See
 * http://bdittes.googlepages.com/
 *
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */


package org.hermit.geometry.voronoi;

import org.hermit.geometry.Edge;
import org.hermit.geometry.Point;


/**
 * @author clint
 * 
 */
class VoronoiEdge
{

    // ******************************************************************** //
    // Building.
    // ******************************************************************** //

    void AddVertex(Point V) {
        if (VVertexA == Point.UNKNOWN)
            VVertexA = V;
        else if (VVertexB == Point.UNKNOWN)
            VVertexB = V;
        else
            throw new RuntimeException("Tried to add third vertex!");
    }


    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Determine whether this edge is infinite.
     * 
     * @return              True if both vertices are infinite; false else.
     */
    boolean isInfinite() {
        return VVertexA == Point.INFINITE && VVertexB == Point.INFINITE;
    }


    /**
     * Determine whether this edge is partly infinite.
     * 
     * @return              True if either vertex is infinite; false else.
     */
    boolean isPartlyInfinite() {
        return VVertexA == Point.INFINITE || VVertexB == Point.INFINITE;
    }


    // ******************************************************************** //
    // Conversion.
    // ******************************************************************** //

    /**
     * Convert this VoronoiEdge to an immutable Edge.
     * 
     * @return              An Edge equivalent to this object.
     */
    Edge toEdge() {
        return new Edge(VVertexA, VVertexB, LeftData, RightData);
    }


    // ******************************************************************** //
    // Package-Visible Data.
    // ******************************************************************** //

    boolean Done = false;

    Point RightData = Point.UNKNOWN, LeftData = Point.UNKNOWN;

    Point VVertexA = Point.UNKNOWN, VVertexB = Point.UNKNOWN;


}

