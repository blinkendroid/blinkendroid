
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


import java.util.Random;

import org.hermit.geometry.Point;
import org.hermit.geometry.Region;


/**
 * A data generator which generates clustered random points.
 */
public class NuclearGenerator
    implements Generator
{
    
    // ******************************************************************** //
    // Constructors.
    // ******************************************************************** //
    
    /**
     * Create a clustering data generator.
     * 
     * @param  nclusters    The desired number of clusters in each generated
     *                      data set.
     */
    public NuclearGenerator(int nclusters) {
        numClusters = nclusters;
    }
    
    
    // ******************************************************************** //
    // Data Generator.
    // ******************************************************************** //

    /**
     * Create a set of data points within the given region.
     * 
     * @param  region      The region of the plane in which the points
     *                     must lie.
     * @param  npoints     The desired number of points.
     * @return             The generated data points.
     */
    public Point[] createPoints(Region region, int npoints) {
        // First, create the randomly-placed nuclear plants within the region.
        // Don't put two close together.
        refPoints = new Point[numClusters];
        makeCentres:
        for (int i = 0; i < numClusters; ) {
            Point p = region.randomPoint();
            for (int c = 0; c < i; ++c)
                if (p.dist(refPoints[c]) < 80)
                    continue makeCentres;
            refPoints[i++] = p;
        }

        // Now, create random points,
        Point[] points = new Point[npoints];
        for (int i = 0; i < npoints; ) {
            // Create a random point in the plane.
            Point p = region.randomPoint();
            
            // The chance of this point existing depends on the distance to
            // one or more nuclear plants.  Being close to multiple plants
            // increases our chances.
            boolean exists = false;
            for (int c = 0; c < numClusters; ++c) {
                double dist = p.dist(refPoints[c]);
                double r = Math.abs(rnd.nextGaussian()) * 2.0;
                exists |= dist < 20 * r;
            }
            
            if (exists)
                points[i++] = p;
        }

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
        return refPoints;
    }
    
    
    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //

    // RNG used for random points.
    private static Random rnd = new Random();
    

    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //

    // The number of clusters in the generated data.
    private int numClusters;

    // The reference points for the most recent data set.
    private Point[] refPoints = null;

}

