
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.hermit.geometry.MathTools;
import org.hermit.geometry.Point;
import org.hermit.geometry.Edge;
import org.hermit.geometry.Graph;


/**
 * Fortune's algorithm for generating a Voronoi diagram.
 * 
 * <p>This class takes a set of points in the plane and generates
 * the corresponding Voronoi diagram using Fortune's algorithm.
 */
public abstract class Fortune {

    // ******************************************************************** //
    // Public Methods.
    // ******************************************************************** //

    /**
     * Compute the Voronoi diagram for the given set of points.
     * 
     * @param   points      The data points.
     * @return              A Graph representing the generated diagram.
     */
    public static Graph ComputeVoronoiGraph(Iterable<Point> points) {
        // Create a priority queue of data events for each point,
        // and pass that to the main algorithm.  Ignore dupes in the
        // input points.
        final PriorityQueue<Event> queue = new PriorityQueue<Event>();
        for (final Point v : points) {
            DataEvent ev = new DataEvent(v);
            if (!queue.contains(ev))
                queue.add(ev);
        }
        
        return ComputeVoronoiGraph(queue);
    }
    
    
    /**
     * Compute the Voronoi diagram for the given set of points.
     * 
     * @param   points      The data points.
     * @return              A Graph representing the generated diagram.
     */
    public static Graph ComputeVoronoiGraph(Point[] points) {
        // Create a priority queue of data events for each point,
        // and pass that to the main algorithm.  Ignore dupes in the
        // input points.
        final PriorityQueue<Event> queue = new PriorityQueue<Event>();
        for (final Point v : points) {
            DataEvent ev = new DataEvent(v);
            if (!queue.contains(ev))
                queue.add(ev);
        }
        
        return ComputeVoronoiGraph(queue);
    }
    

    /**
     * Filter the given graph, removing any edges whose data points are
     * closer than min.
     * 
     * @param   graph       The graph to filter.
     * @param   min         The minimum data point separation for any
     *                      edge we wish to keep.
     * @return              A new Graph, represenging the filtered input
     *                      graph.
     */
    public static Graph FilterVG(Graph graph, double min) {
        // Go through all the edges, and copy the ones which aren't too
        // small to a new edge list.
        final HashSet<Edge> edgeList = new HashSet<Edge>();
        Iterator<Edge> edges = graph.getEdges();
        while (edges.hasNext()) {
            Edge e = edges.next();
            Point da = e.getDatumA();
            Point db = e.getDatumB();
            if (da.dist(db) >= min)
                edgeList.add(e);
        }

        // Make a new graph of the new edges.
        return new Graph(edgeList);
    }
    

    // ******************************************************************** //
    // Private Methods.
    // ******************************************************************** //

    /**
     * This routine implements the Fortune algorithm.
     * 
     * @param   queue       A priority queue of data events for each
     *                      input point.
     * @return              A Graph representing the generated diagram.
     */
    private static Graph ComputeVoronoiGraph(PriorityQueue<Event> queue) {
        final HashMap<DataNode, CircleEvent> CurrentCircles = new HashMap<DataNode, CircleEvent>();
        final HashSet<Point> vertexList = new HashSet<Point>();
        final HashSet<VoronoiEdge> edgeList = new HashSet<VoronoiEdge>();

        Node RootNode = null;
        while (queue.size() > 0) {
            final Event VE = queue.poll();
            final ArrayList<DataNode> CircleCheckList = new ArrayList<DataNode>();
            if (VE instanceof CircleEvent) {
                CircleEvent cev = (CircleEvent) VE;
                CurrentCircles.remove(cev.NodeN);
                if (!cev.Valid)
                    continue;
            }
            RootNode = VE.process(RootNode, VE.getY(), vertexList, edgeList, CircleCheckList);
            
            for (final DataNode VD : CircleCheckList) {
                if (CurrentCircles.containsKey(VD)) {
                    final CircleEvent cev = CurrentCircles.remove(VD);
                    cev.Valid = false;
                }
                
                final CircleEvent VCE = VD.CircleCheckDataNode(VE.getY());
                if (VCE != null) {
                    queue.add(VCE);
                    CurrentCircles.put(VD, VCE);
                }
            }
            
            if (VE instanceof DataEvent) {
                final Point DP = ((DataEvent) VE).getDatum();
                for (final CircleEvent VCE : CurrentCircles.values()) {
                    double dist = DP.dist(VCE.Center);
                    double offs = VCE.getY() - VCE.Center.getY();
                    if (MathTools.lt(dist, offs))
                        VCE.Valid = false;
                }
            }
        }
        
        RootNode.CleanUpTree();
        for (final VoronoiEdge VE : edgeList) {
            if (VE.Done)
                continue;
            if (VE.VVertexB == Point.UNKNOWN) {
                VE.AddVertex(Point.INFINITE);
                if (MathTools.eq(VE.LeftData.getY(), VE.RightData.getY())
                        && VE.LeftData.getX() < VE.RightData.getX()) {
                    final Point T = VE.LeftData;
                    VE.LeftData = VE.RightData;
                    VE.RightData = T;
                }
            }
        }
        
        final ArrayList<VoronoiEdge> MinuteEdges = new ArrayList<VoronoiEdge>();
        for (final VoronoiEdge VE : edgeList) {
            if (!VE.isPartlyInfinite() && VE.VVertexA.equals(VE.VVertexB)) {
                MinuteEdges.add(VE);
                // prevent rounding errors from expanding to holes
                for (final VoronoiEdge VE2 : edgeList) {
                    if (VE2.VVertexA.equals(VE.VVertexA))
                        VE2.VVertexA = VE.VVertexA;
                    if (VE2.VVertexB.equals(VE.VVertexA))
                        VE2.VVertexB = VE.VVertexA;
                }
            }
        }
        for (final VoronoiEdge VE : MinuteEdges)
            edgeList.remove(VE);
        
        // Now build the Graph to pass back to the caller.
        final HashSet<Edge> finalEdges = new HashSet<Edge>();
        for (final VoronoiEdge VE : edgeList)
            finalEdges.add(VE.toEdge());
        return new Graph(finalEdges);
    }

}

