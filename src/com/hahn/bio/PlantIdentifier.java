package com.hahn.bio;

import org.newdawn.slick.geom.Vector2f;

public class PlantIdentifier implements ITargetable {
	public final int id;
	public final Vector2f loc;
	
	public PlantIdentifier(int id, int x, int y) {		
		this.id = id;
		this.loc = new Vector2f(x, y);
	}
	
	public Vector2f getLoc() {
		return loc;
	}
	
	public int getRadius() {
		return World.plants.getRadius(this);
	}
	
	public boolean isGone() {
		return World.plants.getEnergy(this) <= 0;
	}
	
	public int getX() {
		return (int) loc.x;
	}
	
	public int getY() {
		return (int) loc.y;
	}
}
