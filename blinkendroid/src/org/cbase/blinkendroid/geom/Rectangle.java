package org.cbase.blinkendroid.geom;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Rectangle {

	private int x;
	private int y;
	private int width;
	private int height;
	
	public Rectangle(List<Pixel> points) {
		
		if(points.size() < 2) {
			return;
		}
		
		Collections.sort(points, new Comparator<Pixel>() {

			@Override
			public int compare(Pixel o1, Pixel o2) {
				if(o1.getX() < o2.getX()) {
					return -1;
				}
				else if(o1.getX() == o2.getX()) {
					return 0;
				}
				
				return 1;
			}
			
		});

		Pixel minX = (Pixel)points.get(0).clone();
		Pixel maxX = (Pixel)points.get(points.size()-1).clone();

		Collections.sort(points, new Comparator<Pixel>() {

			@Override
			public int compare(Pixel o1, Pixel o2) {
				if(o1.getY() < o2.getY()) {
					return -1;
				}
				else if(o1.getY() == o2.getY()) {
					return 0;
				}
				
				return 1;
			}
			
		});

		Pixel minY = (Pixel)points.get(0).clone();
		Pixel maxY = (Pixel)points.get(points.size()-1).clone();
		
		int x = (int)minX.getX();
		int y = (int)minY.getY();
		int width = (int)(Math.abs(maxX.getX() - minX.getX()));
		int height = (int)(Math.abs(maxY.getY() - minY.getY()));
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		return y;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	
	public boolean isWihtin(Rectangle rect) {
		return false;
	}

	public boolean isWihtin(List<Pixel> points) {
		return false;
	}
}
