package org.cbase.blinkendroid.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a cluster of Pixels and
 * implements a simple algorithm for finding the 
 * densest/biggest cluster
 * 
 * @author dima
 */
public class SimpleCluster extends ArrayList<Pixel> {
	
	private int size = -1;
	private Rectangle bounds;
	
	/**
	 * Getter for this clusters rectangular bounds
	 * @return rectangular bounds of this cluster
	 */
	public Rectangle getBounds() {
		if(bounds == null) {
			bounds = new Rectangle(this);
		}
		
		return bounds;
	}
	
	/**
	 * Caching getter for this clusters plane size
	 * @return plane size
	 */
	public long getPlaneSize() {
		if(size == -1) {
			size = getBounds().getWidth()*getBounds().getHeight();
		}
		
		return size;
	}
	
	/**
	 * Getter for the clusters density
	 * @return
	 */
	public double getDensity() {
		if(getPlaneSize() == 0) {
			return 0;
		}
		
		return size()/getPlaneSize();
	}

	/**
	 * Tries to isolate the densest/biggest cluster using a quite stupid
	 * algorithm which happens to work quite well with the color keying approach
	 * but would fail miserably with edge detection
	 * @param points
	 * @return cluster
	 */
	public static SimpleCluster findBiggestCluster(ArrayList<Pixel> points) {
		double[] distances = new double[points.size()];
		Pixel centroid = calculateCentroid(points);	
		double meanDistance = calculateDistances(centroid, points, distances);
		List<Pixel> members = getNeighbouringPoints(points, distances, meanDistance);
		
		SimpleCluster sc = new SimpleCluster();
		sc.addAll(members);
		return sc;
	}
		 
	/**
	 * Calculates mean coordinate and creates new centroid
	 * @param centroid
	 * @param points
	 * @return
	 */
	private static Pixel calculateCentroid(ArrayList<Pixel> points) {
		
		if(points.size() < 1) {
			return null;
		}
		
		int sumX = 0;
		int sumY = 0;
		
		int pointsSize = points.size();
		
		for(int i = 0; i < pointsSize; i++) {
			
			Pixel p = points.get(i);
			
			int pX = p.getX();
			int pY = p.getY();
			sumX += pX;
			sumY += pY;
		}
		
		int cX = sumX/points.size();
		int cY = sumY/points.size();

		// our new centroid
		return new Pixel(cX, cY);
	}
	
	/**
	 * Finds all points within a given distance to the centroid
	 * @param pixels the pixels to select from
	 * @param distances array of distances whose index corresponds to the pixels position in the pixels list
	 * @param d max distance from centroid
	 * @return list of matching points
	 */
	private static List<Pixel> getNeighbouringPoints(List<Pixel> pixels, double[] distances, double d) {
		ArrayList<Pixel> clean = new ArrayList<Pixel>();
		
		for(int i = 0; i < distances.length; i++) {
			if(distances[i] <= d) {
				clean.add(pixels.get(i));
			}
		}
		
		return clean;
	}

	/**
	 * Calculates mean distance and distance to centroid for every given point
	 * @param centroid
	 * @param points
	 * @param distances this array is filled with every points distance 
	 * 			to the centroid using its position in the points list
	 * @return mean distance
	 */
	private static double calculateDistances(Pixel centroid, ArrayList<Pixel> points, double[] distances) {
		long distanceSum = 0;		
		int pointsSize = points.size();
		
		for(int i = 0; i < pointsSize; i++) {
			double dist = centroid.getDistance(points.get(i));
			distanceSum += dist;
			distances[i] = dist;
		}
		
		return distanceSum/points.size();
	}
	
	/**
	 * Finds the densest cluster
	 * @param clusters list of clusters
	 * @return densest cluster
	 */
	public static SimpleCluster findDensestCluster(ArrayList<SimpleCluster> clusters) {
		return Collections.max(clusters, new Comparator<SimpleCluster>() {
	
		@Override
		public int compare(SimpleCluster o1, SimpleCluster o2) {
			if(o1.getDensity() > o2.getDensity()) {
				return 1;
			}
			else if(o1.getDensity() < o2.getDensity()) {
				return -1;
			}
			
			return 0;
								
		}});
	}
}
