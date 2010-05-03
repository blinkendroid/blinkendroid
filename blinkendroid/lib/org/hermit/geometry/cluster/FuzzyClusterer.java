
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


package org.hermit.geometry.cluster;


import java.util.Random;

import org.hermit.geometry.MathTools;
import org.hermit.geometry.Point;
import org.hermit.geometry.Region;


/**
 * An implementation of Lloyd's k-clusterMeans clustering algorithm.
 */
public class FuzzyClusterer
    extends Clusterer
{
	
    /**
     * Prepare a clustering pass on the indicated data.
     * 
     * @param  points      The array of dataPoints to be clustered.
     * @param  ids         Array of cluster numbers which this call will
     *                     fill in, defining which cluster each point
     *                     belongs to.  The caller must leave the data here
     *                     intact between iterations.
     * @param  means       Array of x,y values in which to place centroids
     *                     of the clusters.
     * @param  region      The region of the plane in which the points lie.
     */
    @Override
    public void prepare(Point[] points, int[] ids, double[][] means, Region region) {
        super.prepare(points, ids, means, region);
        
	    // Save the data arrays.
	    dataPoints = points;
        pointClusters = ids;
        clusterMeans = means;
		numPoints = points.length;
		numClusters = means.length;

		// Set up the strengths array.
		clusterStrengths = new double[numPoints][numClusters];

		// Set the initial cluster centroids to be random values
		// within the data region.
        double x = region.getX1();
        double y = region.getY1();
        double w = region.getWidth();
        double h = region.getHeight();
		for (int i = 0; i < numClusters; ++i) {
			means[i][0] = random.nextDouble() * w + x;
			means[i][1] = random.nextDouble() * h + y;
		}
        
        // Make an initial assignment of points to clusters, so on the first
		// iteration we have a basis for computing centroids.
        assignPoints(ids, means);
	}


    /**
     * Runs a single iteration of the clustering algorithm on the stored data.
     * The results are stored in the arrays that were passed into
     * {@link #prepare(Point[], int[], double[][], Region)}.
     * 
     * <p>After each iteration, the cluster IDs and cluster means should
     * be consistent with each other.
     * 
     * @return             true if the algorithm has converged.
     */
	@Override
    public boolean iterate() {
        System.out.println("Fuzzy: iterate");
        
        // Compute the new centroids of the clusters, based on the existing
	    // point assignments.
        boolean converged = computeCentroids(pointClusters, clusterMeans);
        
	    // Assign data points to clusters based on the new centroids.
        if (!converged)
            assignPoints(pointClusters, clusterMeans);
        
        // Our convergence criterion is no change in the means.
        return converged;
	}

	
	/**
	 * Compute the centroids of all the clusters.
     * 
     * @param  ids          Array of cluster numbers which this call will
     *                      fill in, defining which cluster each point
     *                      belongs to.  The caller must leave the data here
     *                      intact between iterations.
     * @param  means        Array of x,y values in which we will place the
     *                      centroids of the clusters.
     * @return              true iff none of the means moved by a significant
     *                      amount.
	 */
	private boolean computeCentroids(int[] ids, double[][] means) {
        boolean dirty = false;
        for (int c = 0; c < numClusters; ++c) {
            // Compute the weighted sum of the data points.
            double tx = 0.0, ty = 0.0, tot = 0.0;
            for (int p = 0; p < numPoints; ++p) {
                Point point = dataPoints[p];
                double str = Math.pow(clusterStrengths[p][c], M);
                tx += point.getX() * str;
                ty += point.getY() * str;
                tot += str;
            }
            
            // Calculate the mean, and see if it's different from
            // the previous one.
            final double nx = tx / tot;
            final double ny = ty / tot;
            if (!MathTools.eq(means[c][0], nx) || !MathTools.eq(means[c][1], ny)) {
                means[c][0] = nx;
                means[c][1] = ny;
                dirty = true;
            }
        }
        
        return !dirty;
	}
	
	
	/**
	 * Assign each point in the data array to the cluster whose centroid
	 * it is closest to.
     * 
     * @param  ids          Array of cluster numbers which this call will
     *                      fill in, defining which cluster each point
     *                      belongs to.  The caller must leave the data here
     *                      intact between iterations.
     * @param  means        Array of x,y values in which we will place the
     *                      centroids of the clusters.
     * @return              true iff none of the points changed to a different
     *                      cluster.
	 */
	private boolean assignPoints(int[] ids, double[][] means) {
        // Accumulate the sum of the distances squared between each
        // point and its closest mean.
        sumDistSquared = 0.0;
        
        // Assign each point to a cluster, according to which cluster
        // centroid it is closest to.  Set dirty to true if any point
        // changes to a different cluster.
        boolean dirty = false;
        for (int p = 0; p < numPoints; ++p) {
            Point point = dataPoints[p];
            int closest = -1;
            double minDistance = Double.MAX_VALUE;
            double maxStrength = 0;
            
            for (int c = 0; c < means.length; ++c) {
                double distsq = computeDistanceSquared(point, means[c]);
                double dist = Math.sqrt(distsq);
                double sum = 0.0;
                for (int j = 0; j < means.length; ++j) {
                    double djsq = computeDistanceSquared(point, means[j]);
                    double dj = Math.sqrt(djsq);
                    sum += Math.pow(dist / dj, 2 / (M - 1));
                }
                clusterStrengths[p][c] = 1 / sum;
                if (1 / sum > maxStrength) {
                    maxStrength = 1 / sum;
                    closest = c;
                    minDistance = distsq;
                }
            }
            sumDistSquared += minDistance;

            if (closest != ids[p]) {
                ids[p] = closest;
                dirty = true;
            }
        }

        return !dirty;
	}
	

    /**
     * Computes the absolute squared Cartesian distance between two points.
     */
    private static final double computeDistanceSquared(Point a, double[] b) {
        final double dx = a.getX() - b[0];
        final double dy = a.getY() - b[1];
        return dx * dx + dy * dy;
    }

    
    /**
     * Calculate a quality metric for the current clustering solution.
     * This number is available after each call to {@link #iterate()}.
     * 
     * @return             Quality metric for this solution; small is better.
     */
    @Override
    public double metric() {
        return sumDistSquared;
    }


    // ******************************************************************** //
    // Class Data.
    // ******************************************************************** //
	
	// RNG used to set initial mean values.
    private static final Random random = new Random();
    
    // M (power factor) used for calculating weights.  Must be > 1.0.
    private static final double M = 2.0;

    
    // ******************************************************************** //
    // Private Data.
    // ******************************************************************** //
   
    // The number of points in the data set on a given pass.
    private int numPoints;
    
    // The desired number of clusters.
    private int numClusters;
    
    // During a pass -- multiple iterations -- this points to the
    // array of data points.
    private Point[] dataPoints;
    
    // Cluster IDs to which the points have been assigned by the most
    // recent iteration.  This will be null if prepare() hasn't been called,
    // and is filled in by each call to iterate().
    private int[] pointClusters = null;

    // Calculated centroid positions of the clusters.  This will be null
    // if prepare() hasn't been called, and is filled in by each call
    // to iterate().
    private double[][] clusterMeans = null;
    
    // The sum of the squares of the distances from each point to the mean
    // of its assigned cluster in the current solution.
    private double sumDistSquared = 0.0;

    // For each point p, clusterStrengths[p][c] is its degree of
    // belonging to each cluster c.
    private double[][] clusterStrengths;

}

