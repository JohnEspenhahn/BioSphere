package com.hahn.bio.util;


public class Camera {
	public final int MaxX, MaxY;

	public final int WorldWidth, WorldHeight;
	public final int ViewWidth, ViewHeight;
	
	private double zoom;
	private int offsetX, offsetY;
	private int velX, velY;
	
	public Camera(int width, int height, int maxX, int maxY) {		
		this.MaxX = maxX - width;
		this.MaxY = maxY - height;
		
		this.ViewWidth = width;
		this.ViewHeight = height;
		
		this.WorldWidth = maxX;
		this.WorldHeight = maxY;
		
		this.zoom = 1;
	}
	
	public void update() {
		offsetX += velX;		
		offsetY += velY;
		
		constrain();
	}
	
	public void moveTo(int x, int y) {
		offsetX = x - ViewWidth / 2;
		offsetY = y - ViewHeight / 2;
		
		constrain();
	}
	
	public void moveToCenter() {
		moveTo(WorldWidth / 2, WorldHeight / 2);
	}
	
	public void constrain() {
		if (offsetX < 0) offsetX = 0;
		else if (offsetX > MaxX) offsetX = MaxX;
		
		if (offsetY < 0) offsetY = 0;
		else if (offsetY > MaxY) offsetY = MaxY;
	}
	
	public void setZoom(double z) {
		this.zoom = z;
	}
	
	public void setVel(int x, int y) {
		setVelX(x);
		setVelY(y);
	}
	
	public void setVelX(int x) {
		this.velX = x;
	}
	
	public void setVelY(int y) {
		this.velY = y;
	}
	
	public int getX() {
		return offsetX;
	}
	
	public int getY() {
		return offsetY;
	}
	
	public double getZoom() {
		return zoom;
	}
	
	public boolean outOfWorld(int x, int y) {
		return x < 0 || x > WorldWidth || y < 0 || y > WorldHeight;
	}
}
