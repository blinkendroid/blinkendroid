
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

import java.util.ArrayList;
import java.util.HashSet;

import org.hermit.geometry.MathTools;
import org.hermit.geometry.Point;


/**
 * @author clint
 * 
 */
class DataEvent extends Event {

    public DataEvent(Point DP) {
        DataPoint = DP;
    }


    @Override
    public double getX() {
        return DataPoint.getX();
    }


    @Override
    public double getY() {
        return DataPoint.getY();
    }


    public Point getDatum() {
        return DataPoint;
    }


    /**
     * Will return the new root (unchanged except in start-up)
     */
    @Override
    Node process(Node Root, double ys,
                  HashSet<Point> vertList,
                  HashSet<VoronoiEdge> edgeList,
                  ArrayList<DataNode> CircleCheckList)
    {
        if (Root == null) {
            Root = new DataNode(DataPoint);
            CircleCheckList.add((DataNode) Root);
            return Root;
        }

        // 1. Find the node to be replaced
        final Node C = Node.FindDataNode(Root, ys, DataPoint.getX());

        // 2. Create the subtree (ONE Edge, but two VEdgeNodes)
        final VoronoiEdge VE = new VoronoiEdge();
        VE.LeftData = ((DataNode) C).DataPoint;
        VE.RightData = DataPoint;
        VE.VVertexA = Point.UNKNOWN;
        VE.VVertexB = Point.UNKNOWN;
        edgeList.add(VE);

        Node SubRoot;
        if (MathTools.eq(VE.LeftData.getY(), VE.RightData.getY())) {
            DataNode l, r;
            if (VE.LeftData.getX() < VE.RightData.getX()) {
                l = new DataNode(VE.LeftData);
                r = new DataNode(VE.RightData);
                SubRoot = new EdgeNode(VE, false, l, r);
            } else {
                l = new DataNode(VE.RightData);
                r = new DataNode(VE.LeftData);
                SubRoot = new EdgeNode(VE, true, l, r);
            }
            CircleCheckList.add(l);
            CircleCheckList.add(r);
        } else {
            DataNode l = new DataNode(VE.LeftData);
            DataNode rl = new DataNode(VE.RightData);
            DataNode rr = new DataNode(VE.LeftData);
            EdgeNode r = new EdgeNode(VE, true, rl, rr);
            SubRoot = new EdgeNode(VE, false, l, r);
            CircleCheckList.add(l);
            CircleCheckList.add(rl);
            CircleCheckList.add(rr);
        }

        // 3. Apply subtree
        Node parent = C.getParent();
        if (parent == null)
            return SubRoot;
        parent.Replace(C, SubRoot);
        return Root;
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
        if (obj == null || !(obj instanceof DataEvent))
            return false;

        final DataEvent o = (DataEvent) obj;
        return DataPoint.equals(o.DataPoint);
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
        return "<" + getX() + "," + getY() + ">";
    }

    private Point DataPoint;


}

