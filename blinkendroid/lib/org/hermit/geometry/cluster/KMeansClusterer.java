
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


import org.hermit.geometry.MathTools;
import org.hermit.geometry.Point;
import org.hermit.geometry.Region;


/**
 * An implementation of Lloyd's k-clusterMeans clustering algorithm.
 */
public class KMeansClusterer
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
        dataRegion = region;
	    dataPoints = points;
        pointClusters = ids;
        clusterMeans = means;
		numPoints = points.length;
		numClusters = means.length;

        // Make the working data arrays.
        sumXs = new int[numClusters];
        sumYs = new int[numClusters];
        clusterSizes = new int[numClusters];

		// Set the initial cluster centroids to be random values
		// within the data region.
		for (int i = 0; i < numClusters; ++i) {
		    Point p = dataRegion.randomPoint();
			means[i][0] = p.getX();
			means[i][1] = p.getY();
		}
        
        // Make an initial assignment of points to clusters, so on the first
		// iteration we have a basis for computing centroids.
        assignPoints(pointClusters, clusterMeans);
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
        System.out.println("K-Means: iterate");
        
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
	    // Clear the working data.
        for (int i = 0; i < numClusters; ++i)
            sumXs[i] = sumYs[i] = clusterSizes[i] = 0;
        
        // Calculate the sums of the points in each cluster.
        for (int i = 0; i < numPoints; ++i) {
            Point point = dataPoints[i];
            int c = ids[i];
            sumXs[c] += point.getX();
            sumYs[c] += point.getY();
            clusterSizes[c] += 1;
        }
        
        // Now calculate the means for each cluster, and see if any has
        // changed since the previous round.
        boolean dirty = false;
        for (int c = 0; c < numClusters; ++c) {
            int s = clusterSizes[c];
            
            // If the cluster is empty, assign a random mean.  Else calculate
            // the average of the points in the cluster.
            double nx, ny;
            if (s == 0) {
                Point p = dataRegion.randomPoint();
                nx = p.getX();
                ny = p.getY();
            } else {
                nx = sumXs[c] / s;
                ny = sumYs[c] / s;
            }
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
        for (int i = 0; i < numPoints; ++i) {
            // Find the closest mean to the current data point.  Also
            // accumulate the sum of the distances squared.
            Point point = dataPoints[i];
            int closest = -1;
            double minDistance = Double.MAX_VALUE;
            for (int c = 0; c < means.length; ++c) {
                double distance = computeDistanceSquared(point, means[c]);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = c;
                }
            }
            sumDistSquared += minDistance;
            
            if (closest != ids[i]) {
                ids[i] = closest;
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
    // Private Data.
    // ******************************************************************** //
   
    // The region of the plane in which we're working.
    private Region dataRegion;
    
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
    private double[][] clusterMeans  = null;
    
    // The sum of the squares of the distances from each point to the mean
    // of its assigned cluster in the current solution.
    private double sumDistSquared = 0.0;

    // Working data -- sums and counts, used to average the clusters.
    private int[] sumXs;
    private int[] sumYs;
    private int[] clusterSizes;

}

