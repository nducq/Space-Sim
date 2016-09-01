package main;

import processing.core.PApplet;

public class CircumCircle {
	private double x;
	private double y;
	private double radius;
	
	public CircumCircle(double x, double y, double radius){
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public void draw(PApplet device, Map map){
		device.stroke(255, 0, 0);
		device.fill(0, 0, 0, 0);
		
		device.ellipseMode(device.CENTER);
		device.ellipse((int)(x) + map.getMapX(), (int)(y) + map.getMapY(), (int)(radius * 2), (int)(radius * 2));
	}
}
