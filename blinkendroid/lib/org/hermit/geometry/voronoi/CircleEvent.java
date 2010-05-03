
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
class CircleEvent extends Event {

    CircleEvent(DataNode n, DataNode l, DataNode r, Point c) {
        NodeN = n;
        NodeL = l;
        NodeR = r;
        Center = c;
        Valid = true;
    }
    
    
    @Override
    public double getX() {
        return Center.getX();
    }


    @Override
    public double getY() {
        double dist = NodeN.DataPoint.dist(Center);
        return MathTools.round(Center.getY() + dist);
    }


    @Override
    Node process(Node Root, double ys,
                  HashSet<Point> vertList,
                  HashSet<VoronoiEdge> edgeList,
                  ArrayList<DataNode> CircleCheckList)
    {
        final DataNode b = NodeN;
        final DataNode a = Node.LeftDataNode(b);
        final DataNode c = Node.RightDataNode(b);
        if (a == null || b.getParent() == null || c == null
                || !a.DataPoint.equals(NodeL.DataPoint)
                || !c.DataPoint.equals(NodeR.DataPoint)) {
            // Abbruch da sich der Graph ver�ndert hat
            return Root;
        }
        
        final EdgeNode eu = (EdgeNode) b.getParent();
        CircleCheckList.add(a);
        CircleCheckList.add(c);

        // 1. Create the new Vertex
        final Point VNew = new Point(Center.getX(), Center.getY());
        // VNew[0] =
        // Fortune.ParabolicCut(a.DataPoint[0],a.DataPoint[1],c.DataPoint[0],c.DataPoint[1],ys);
        // VNew[1] = (ys + a.DataPoint[1])/2 -
        // 1/(2*(ys-a.DataPoint[1]))*(VNew[0]-a.DataPoint[0])*(VNew[0]-a.DataPoint[0]);
        vertList.add(VNew);

        // 2. Find out if a or c are in a distand part of the tree (the other
        // is then b's sibling) and assign the new vertex
        EdgeNode eo;
        final Node eleft = eu.getLeft();
        final Node eright = eu.getRight();
        if (eleft == b) {
            // c is sibling
            eo = Node.EdgeToRightDataNode(a);

            // replace eu by eu's Right
            eu.getParent().Replace(eu, eright);
        } else {
            // a is sibling
            eo = Node.EdgeToRightDataNode(b);

            // replace eu by eu's Left
            eu.getParent().Replace(eu, eleft);
        }
        eu.Edge.AddVertex(VNew);
        // ///////////////////// uncertain
        // if(eo==eu)
        // return Root;
        // /////////////////////

        // complete & cleanup eo
        eo.Edge.AddVertex(VNew);
        // while(eo.Edge.VVertexB == Fortune.VVUnkown)
        // {
        // eo.Flipped = !eo.Flipped;
        // eo.Edge.AddVertex(Fortune.VVInfinite);
        // }
        // if(eo.Flipped)
        // {
        // Vector T = eo.Edge.LeftData;
        // eo.Edge.LeftData = eo.Edge.RightData;
        // eo.Edge.RightData = T;
        // }


        // 2. Replace eo by new Edge
        final VoronoiEdge VE = new VoronoiEdge();
        VE.LeftData = a.DataPoint;
        VE.RightData = c.DataPoint;
        VE.AddVertex(VNew);
        edgeList.add(VE);

        final EdgeNode VEN = new EdgeNode(VE, false, eo.getLeft(), eo
                .getRight());
        final Node parent = eo.getParent();
        if (parent == null)
            return VEN;
        parent.Replace(eo, VEN);
        return Root;
    }


    DataNode NodeN, NodeL, NodeR;

    Point Center;

    public boolean Valid = true;

    
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
        return "<" + getX() + "," + getY() + "::" + (Valid ? "" : "!!!") + NodeN + "," + Center + ">";
    }
    
}

