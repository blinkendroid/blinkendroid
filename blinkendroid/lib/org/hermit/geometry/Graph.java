
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


import java.util.HashSet;
import java.util.Iterator;


/**
 * An immutable graph, with possibly infinite edges.
 * 
 * <p>This immutable class represents a graph as a collection of Edge
 * objects.  These edges can be infinite.
 */
public class Graph {
    
    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

    /**
     * Construct a graph with a given set of edges.
     * 
     * @param   e           The set of edges which define the graph.
     *                      May include infinite edges.
     */
    public Graph(HashSet<Edge> e) {
        graphVertices = null;
        graphEdges = e;
    }
    

    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Get the number of edges in the graph.
     * 
     * @return              The number of edges in the graph.  Note that
     *                      this number includes an infinite point if
     *                      there are any infinite edges in the graph.
     */
    public int getNumEdges() {
        return graphEdges.size();
    }
    

    /**
     * Get the edges in the graph.
     * 
     * @return              An iterator over the edges in the graph.  Note that
     *                      this iterator includes an infinite point if
     *                      there are any infinite edges in the graph.
     */
    public Iterator<Edge> getEdges() {
        return graphEdges.iterator();
    }


    /**
     * Get an array of the edges in the graph.
     * 
     * @return              A newly-allocated array containing the
     *                      edges in the graph.  Note that this is somewhat
     *                      inefficient, as this array is allocated each time.
     *                      Note that this array includes an infinite point if
     *                      there are any infinite edges in the graph.
     */
    public Edge[] getEdgeArray() {
        Edge[] vEdges = new Edge[graphEdges.size()];
        graphEdges.toArray(vEdges);
        return vEdges;
    }


    /**
     * Get the number of vertices in the graph.
     * 
     * @return              The number of vertices in the graph.
     */
    public int getNumVertices() {
        if (graphVertices == null)
            listVertices();
        return graphVertices.size();
    }
    

    /**
     * Get the vertices in the graph.
     * 
     * @return              An iterator over the vertices in the graph.
     */
    public Iterator<Point> getVertices() {
        if (graphVertices == null)
            listVertices();
        return graphVertices.iterator();
    }
    

    /**
     * Get an array of the vertices in the graph.
     * 
     * @return              A newly-allocated array containing the
     *                      vertices in the graph.  Note that this is somewhat
     *                      inefficient, as this array is allocated each time.
     */
    public Point[] getVertexArray() {
        if (graphVertices == null)
            listVertices();
        Point[] vVerts = new Point[graphVertices.size()];
        graphVertices.toArray(vVerts);
        return vVerts;
    }


    /**
     * Create the list of the vertices in the graph.  This is called
     * to set up the vertex list based on the edge list, if and when
     * it is required.
     */
    private void listVertices() {
        // Now list all the vertices which are used in our new edge list.
        graphVertices = new HashSet<Point>();
        for (final Edge VE : graphEdges) {
            graphVertices.add(VE.getVertexA());
            graphVertices.add(VE.getVertexB());
        }
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
        return getClass().getSimpleName();
    }
    

    /**
     * Dump the entire set of edges in this graph to the console.
     *
     * @param   prefix      Prefix for each output line.
     */
    public void dump(String prefix) {
        System.out.println(prefix + "Graph [");
        for (Edge e : graphEdges)
            System.out.println(prefix + "    " + e);
        System.out.println(prefix + "]");
    }

    
    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    // The edges which make up this graph.
    private HashSet<Edge> graphEdges;

    // The vertices in this graph.  This list is normally null, and is
    // generated from the edges when required.
    private HashSet<Point> graphVertices = null;

}

