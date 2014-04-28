package com.hahn.bio.plant;

import org.newdawn.slick.geom.Vector2f;

public abstract class ITargetable {

	public abstract Vector2f getLoc();
	public abstract int getRadius();
	public abstract boolean isGone();
	
	public abstract int getX();
	public abstract int getY();
	
	public int distanceSquared(ITargetable other) {
		int x1 = getX();
		int y1 = getY();
		
		int x2 = other.getX();
		int y2 = other.getY();
		
		return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	}
}
