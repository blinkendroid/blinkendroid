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
abstract class Event implements Comparable<Event> {

    /**
     * Note: subclasses override this!
     * 
     * @return
     */
    abstract double getX();


    /**
     * Note: subclasses override this!
     * 
     * @return
     */
    abstract double getY();


    abstract Node process(Node Root, double ys,
                           HashSet<Point> vertList,
                           HashSet<VoronoiEdge> edgeList,
                           ArrayList<DataNode> CircleCheckList);

    public int compareTo(Event ev) {
        if (!MathTools.eq(getY(), ev.getY())) {
            if (getY() < ev.getY())
                return -1;
            else if (getY() > ev.getY())
                return 1;
        } else {
            if (getX() < ev.getX())
                return -1;
            else if (getX() > ev.getX())
                return 1;
        }
        return 0;
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
    
}

