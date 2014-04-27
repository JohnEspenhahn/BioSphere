package com.hahn.bio.plant;

import org.newdawn.slick.geom.Vector2f;

public interface ITargetable {

	public Vector2f getLoc();
	public int getRadius();
	public boolean isGone();
	
	public int getX();
	public int getY();
}
