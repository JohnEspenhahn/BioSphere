package com.hahn.bio;

import static com.hahn.bio.Constants.*;
import static com.hahn.bio.World.rand;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Plants extends Kernel {
	public static final int FIND_NEAREST = 0;
	
	private static boolean created = false;
	
	private final int[] mRequest;
	private final int[] mResponse;
	
	@Constant
	private final int[] mSize = new int[1];
	
	private final int[] mXs, mYs;
	private final float[] mEnergy;
	private final int[] mRadius;
	
	private Plants(int max) {
		mRequest = new int[3];
		mResponse = new int[4];
		
		mSize[0] = max;
		
		mXs = new int[max];
		mYs = new int[max];
		mEnergy = new float[max];
		
		mRadius = new int[max];
	}
	
	private void init() {
		setExplicit(true);
		
		for (int id = 0; id < mSize[0]; id++) {
			mXs[id] = rand.nextInt(WORLD_SIZE);
			mYs[id] = rand.nextInt(WORLD_SIZE);
			mEnergy[id] = START_PLANT_ENERGY;
		}
		
		put(mSize);
		put(mXs);
		put(mYs);
		put(mEnergy);
	}
	
	public static Plants create() {
		if (created) {
			throw new RuntimeException("Can only create one `Plants` object!");
		}
		
		created = true;
		Plants plants = new Plants(MAX_PANTS / 2);
		plants.init();
		
		return plants;
	}
	
	public PlantIdentifier findNearest(Vector2f point) {
		mRequest[0] = FIND_NEAREST;
		mRequest[1] = (int) point.x;
		mRequest[2] = (int) point.y;
		
		mResponse[2] = Integer.MAX_VALUE;
		
		put(mRequest);
		put(mResponse);
		
		Range range = Range.create(mSize[0]);
		execute(range);
		
		get(mResponse);
		
		if (mResponse[2] != Integer.MAX_VALUE) {
			return new PlantIdentifier(mResponse[3], mResponse[0], mResponse[1]);
		} else {
			return null;
		}
	}
	
	public int eat(PlantIdentifier plant) {
		get(mEnergy);
		
		int energy = (int) mEnergy[plant.id];
		mEnergy[plant.id] = 0;
		mRadius[plant.id] = 0;
		
		put(mEnergy);
		
		return energy;
	}
	
	public int getRadius(PlantIdentifier plant) {
		return mRadius[plant.id];
	}
	
	public void draw(Graphics g) {
		get(mXs);
		get(mYs);
		get(mEnergy);
		
		g.setColor(Color.green);
		for (int id = 0; id < mSize[0]; id++) {
			if (mEnergy[id] > 0) {
				mEnergy[id] += 0.1f;
				mRadius[id] = (int) Math.sqrt(mEnergy[id] / 4);
				
				g.fill(new Circle(mXs[id], mYs[id], mRadius[id]));
			}
		}
		
		put(mEnergy);
	}

	@Deprecated
	@Override
	public void run() {
		int request = mRequest[0];
		
		if (request == FIND_NEAREST) {
			int rX = mRequest[1];
			int rY = mRequest[2];
			
			int id = getGlobalId();
			int thisX = mXs[id];
			int thisY = mYs[id];
			float thisEnergy = mEnergy[id];
			
			if (thisEnergy <= 0) {
				return;
			}
			
			int dist = distanceSqu(rX, rY, thisX, thisY);
			if (dist < mResponse[2]) {
				mResponse[0] = thisX;
				mResponse[1] = thisY;
				mResponse[2] = dist;
				mResponse[3] = id;
			}
		}
	}
	
	private int distanceSqu(int x1, int y1, int x2, int y2) {
		return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	}
}
