package org.cbase.blinkendroid.geom;

public class Pixel implements Cloneable {

	int x;
	int y;
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	protected Object clone() {
		return new Pixel(getX(), getY());
	}
	
	public double getDistance(Pixel p2) {
		int xDist = Math.abs(this.getX() - p2.getX());
		int yDist = Math.abs(this.getY() - p2.getY());
		
		return Math.sqrt(((xDist*xDist)+(yDist*yDist)));
	}
	
	@Override
	public String toString() {
		return new String(x + ";" +  y);
	}
	
}
