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

import org.hermit.geometry.MathTools;
import org.hermit.geometry.Point;


/**
 * @author clint
 * 
 */
class EdgeNode extends Node {

    EdgeNode(VoronoiEdge E, boolean Flipped, Node left, Node right) {
        super(left, right);
        Edge = E;
        this.Flipped = Flipped;
    }


    double Cut(double ys, double x) {
        final double l0 = Edge.LeftData.getX();
        final double l1 = Edge.LeftData.getY();
        final double r0 = Edge.RightData.getX();
        final double r1 = Edge.RightData.getY();

        double delta;
        if (!Flipped)
            delta = ParabolicCut(l0, l1, r0, r1, ys);
        else
            delta = ParabolicCut(r0, r1, l0, l1, ys);
        return MathTools.round(x - delta);
    }

    
    private static double ParabolicCut(double x1, double y1, double x2, double y2,
            double ys) {
        // y1=-y1;
        // y2=-y2;
        // ys=-ys;
        //          
        if (MathTools.eq(x1, x2) && MathTools.eq(y1, y2))
            // if(y1>y2)
            // return double.PositiveInfinity;
            // if(y1<y2)
            // return double.NegativeInfinity;
            // return x;
            throw new IllegalArgumentException(
                    "Identical datapoints are not allowed!");

        if (MathTools.eq(y1, ys) && MathTools.eq(y2, ys))
            return (x1 + x2) / 2;
        if (MathTools.eq(y1, ys))
            return x1;
        if (MathTools.eq(y2, ys))
            return x2;
        final double a1 = 1 / (2 * (y1 - ys));
        final double a2 = 1 / (2 * (y2 - ys));
        if (MathTools.eq(a1, a2))
            return (x1 + x2) / 2;
        double xs1 = 0.5
                / (2 * a1 - 2 * a2)
                * (4 * a1 * x1 - 4 * a2 * x2 + 2 * Math.sqrt(-8 * a1 * x1 * a2
                        * x2 - 2 * a1 * y1 + 2 * a1 * y2 + 4 * a1 * a2 * x2
                        * x2 + 2 * a2 * y1 + 4 * a2 * a1 * x1 * x1 - 2 * a2
                        * y2));
        double xs2 = 0.5
                / (2 * a1 - 2 * a2)
                * (4 * a1 * x1 - 4 * a2 * x2 - 2 * Math.sqrt(-8 * a1 * x1 * a2
                        * x2 - 2 * a1 * y1 + 2 * a1 * y2 + 4 * a1 * a2 * x2
                        * x2 + 2 * a2 * y1 + 4 * a2 * a1 * x1 * x1 - 2 * a2
                        * y2));
        xs1 = MathTools.round(xs1);
        xs2 = MathTools.round(xs2);
        if (xs1 > xs2) {
            final double h = xs1;
            xs1 = xs2;
            xs2 = h;
        }
        if (y1 >= y2)
            return xs2;
        return xs1;
    }


    void cleanupEdge() {
        while (Edge.VVertexB == Point.UNKNOWN)
            Edge.AddVertex(Point.INFINITE);
        // VE.Flipped = !VE.Flipped;

        if (Flipped) {
            final Point T = Edge.LeftData;
            Edge.LeftData = Edge.RightData;
            Edge.RightData = T;
        }

        Edge.Done = true;
    }


    VoronoiEdge Edge;

    private boolean Flipped;


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
        return super.toString() + (Flipped ? "!" : "") + Edge;
    }

}

