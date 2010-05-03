
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

import java.util.Random;


/**
 * An immutable rectangular region in the plane.  This immutable
 * class represents a rectangle as two sets of X and Y co-ordinates.
 */
public class Region {

    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //

    /**
     * Create a Point from individual co-ordinates.
     * 
     * @param   x1           One X co-ordinate.
     * @param   y1           One Y co-ordinate.
     * @param   x2           The other X co-ordinate.
     * @param   y2           The other Y co-ordinate.
     */
    public Region(double x1, double y1, double x2, double y2) {
        // Make sure we store the coordinates in order.
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
    }


    // ******************************************************************** //
    // Accessors.
    // ******************************************************************** //

    /**
     * Get the lower X co-ordinate of this region.
     * 
     * @return          The lower X co-ordinate of this region.
     */
    public double getX1() {
        return x1;
    }


    /**
     * Get the lower Y co-ordinate of this region.
     * 
     * @return          The lower Y co-ordinate of this region.
     */
    public double getY1() {
        return y1;
    }


    /**
     * Get the upper X co-ordinate of this region.
     * 
     * @return          The upper X co-ordinate of this region.
     */
    public double getX2() {
        return x2;
    }


    /**
     * Get the upper Y co-ordinate of this region.
     * 
     * @return          The upper Y co-ordinate of this region.
     */
    public double getY2() {
        return y2;
    }


    /**
     * Get the width of this region.
     * 
     * @return          The width of this region.
     */
    public double getWidth() {
        return x2 - x1;
    }


    /**
     * Get the height of this region.
     * 
     * @return          The height of this region.
     */
    public double getHeight() {
        return y2 - y1;
    }


    // ******************************************************************** //
    // Data Manipulation.
    // ******************************************************************** //

    /**
     * Get a random point within this region.
     * 
     * @return          An evenly-distributed random point within this region.
     */
    public Point randomPoint() {
        double x = rnd.nextDouble() * (x2 - x1) + x1;
        double y = rnd.nextDouble() * (y2 - y1) + y1;
        return new Point(x, y);
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
        return "<" + x1 + "," + y1 + " -> " + x2 + "," + y2 + ">";
    }


    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //

    // RNG used for random points.
    private static Random rnd = new Random();
    
    
    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    // The lower-valued coordinates of this region.
    private final double x1;
    private final double y1;

    // The higher-valued coordinates of this region.
    private final double x2;
    private final double y2;

}

