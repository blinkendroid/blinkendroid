
/**
 * cluster: routines for cluster analysis.
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


package org.hermit.geometry.generator;


import org.hermit.geometry.Point;
import org.hermit.geometry.Region;


/**
 * A data generator which generates evenly-distributed random points.
 */
public class RandomGenerator
    implements Generator
{
    
    // ******************************************************************** //
    // Data Generator.
    // ******************************************************************** //

    /**
     * Create a set of data points within the given region.
     * 
     * @param  region      The region of the plane in which the points
     *                     must lie.
     * @param  num         The desired number of points.
     * @return             The generated data points.
     */
    public Point[] createPoints(Region region, int num) {
        Point[] points = new Point[num];
        for (int i = 0; i < num; ++i)
            points[i] = region.randomPoint();

        return points;
    }

    
    /**
     * Get reference points, if any, associated with the most recently
     * generated data set.
     * 
     * @return              The reference points, if any, used to generate
     *                      the most recent data set.  null if none.
     */
    public Point[] getReferencePoints() {
        return null;
    }

}

