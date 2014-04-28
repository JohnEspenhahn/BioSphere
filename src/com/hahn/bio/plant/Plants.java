package com.hahn.bio.plant;

import static com.hahn.bio.World.rand;
import static com.hahn.bio.util.Config.MAX_PLANTS;
import static com.hahn.bio.util.Config.PLANT_GROW_SPEED;
import static com.hahn.bio.util.Config.START_PLANT_ENERGY;
import static com.hahn.bio.util.Config.WORLD_SIZE;

import java.util.Stack;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.hahn.bio.MainGame;
import com.hahn.bio.boid.Boid;

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
	
	private final Stack<Integer> mFreeSlots;
	
	private Plants(int max) {
		mRequest = new int[3];
		mResponse = new int[4];
		
		mSize[0] = max;
		mFreeSlots = new Stack<Integer>();
		
		mXs = new int[max];
		mYs = new int[max];
		mEnergy = new float[max];
		
		mRadius = new int[max];
	}
	
	private void init() {
		setExplicit(true);
		dispose();
		
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
		Plants plants = new Plants(MAX_PLANTS / 2);
		plants.init();
		
		return plants;
	}
	
	public PlantIdentifier findNearest(Boid b) {
		mRequest[0] = FIND_NEAREST;
		mRequest[1] = b.getX();
		mRequest[2] = b.getY();
		
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
	
	public float eat(PlantIdentifier plant, float amnt) {
		float e = mEnergy[plant.id];
		if (e > amnt) {
			mEnergy[plant.id] -= amnt;
		} else {
			amnt = mEnergy[plant.id];
			kill(plant);
		}
		
		return amnt;
	}
	
	public void kill(PlantIdentifier plant) {
		if (mEnergy[plant.id] > 0) {
			mEnergy[plant.id] = 0;
			mRadius[plant.id] = 0;
			
			mFreeSlots.push(plant.id);
		}
	}
	
	public int getRadius(PlantIdentifier plant) {
		return mRadius[plant.id];
	}
	
	public float getEnergy(PlantIdentifier plant) {
		return mEnergy[plant.id];
	}
	
	private boolean hasOpenSlot() {
		return !mFreeSlots.isEmpty();
	}
	
	public void addNear(int nearX, int nearY, int giveEnergy) {
		if (hasOpenSlot()) {			
			int x;
			do {
				x = nearX + rand.nextInt(MainGame.WIDTH) - MainGame.WIDTH / 2;
			} while (x < 0 || x > WORLD_SIZE);
			
			int y;
			do {
				y = nearY + rand.nextInt(MainGame.WIDTH) - MainGame.WIDTH / 2; 
			} while (y < 0 || y > WORLD_SIZE);
			
			add(x, y, giveEnergy);
		}
	}
	
	public void add(int x, int y, int giveEnergy) {
		if (hasOpenSlot()) {
			int newId = mFreeSlots.pop();
			
			mEnergy[newId] = giveEnergy;
			mXs[newId] = x;
			mYs[newId] = y;
		}
	}
	
	public void updateAndDraw(Graphics g) {
		get(mXs);
		get(mYs);
		get(mEnergy);
		
		g.setColor(Color.green);
		for (int id = 0; id < mSize[0]; id++) {
			if (mEnergy[id] > 0) {
				mEnergy[id] += PLANT_GROW_SPEED;
				
				int giveEnergy = START_PLANT_ENERGY / 4;
				if (hasOpenSlot() && mEnergy[id] > giveEnergy * 2 && rand.nextDouble() < 0.03) {
					mEnergy[id] -= giveEnergy;
					addNear(mXs[id], mYs[id], giveEnergy);
				}
				
				mRadius[id] = (int) Math.sqrt(mEnergy[id] / 4);
				if (mRadius[id] < Boid.RADIUS) mRadius[id] = Boid.RADIUS;
				
				g.fill(new Circle(mXs[id], mYs[id], mRadius[id]));
			}
		}
		
		put(mEnergy);
		put(mYs);
		put(mXs);
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
