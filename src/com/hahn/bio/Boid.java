package com.hahn.bio;

import static com.hahn.bio.World.rand;
import static com.hahn.bio.Constants.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

public class Boid {
	public static final int RADIUS = 5;
	
	private final World mWorld;
	private final Brain mBrain;
	
	private final Vector2f mLoc;
	
	/** Normalized every tick at start of update */
	private final Vector2f mSpeed;
	
	private ITargetable mTarget;
	private float mEnergy;
	
	public Boid(World world, int x, int y) {
		mWorld = world;
		mBrain = Brain.create(2, 3, 2);
		
		mLoc = new Vector2f(x, y);
		
		Vector2f speed = new Vector2f(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f);
		mSpeed = speed.normalise();
		
		mTarget = null;
		mEnergy = START_BOID_ENERGY;
	}
	
	public void update() {
		// Every tick update
		mEnergy -= BOID_METABALIZE_SPEED;
		mSpeed.normalise();
		
		// Find target	
		mTarget = mWorld.findNearestPlant(mLoc);
			
		// If no target
		if (mTarget == null) {
			float[] input = mBrain.getInput();
			input[0] = 0;
			input[1] = 0;
			
		// If has a target
		} else {
			// Check reached target
			if (mLoc.distanceSquared(mTarget.getLoc()) < RADIUS*RADIUS + mTarget.getRadius()*mTarget.getRadius()) {
				if (mTarget instanceof PlantIdentifier) {
					PlantIdentifier plant = (PlantIdentifier) mTarget;
					int energy = World.plants.eat(plant);
					
					mEnergy += energy;
				}
				
				mTarget = null;
				
				return;
				
			// Update ANN input
			} else {
				Vector2f targVec = new Vector2f(mTarget.getX() - mLoc.x, mTarget.getY() - mLoc.y).normalise();
				int dir = getDirection(mSpeed, targVec);
				
				float[] input = mBrain.getInput();
				if (dir < 0) {
					// Left
					input[0] = 1;
					input[1] = 0;
				} else {
					// Right
					input[0] = 0;
					input[1] = 1;
				}
			}
		}
		
		// Run ANN
		mBrain.update();
		
		// Move based on ANN output
		float[] output = mBrain.getOutput();
		
		if (output[0] > output[1]) {
			// Right
			mSpeed.add(5);
		} else {
			// Left
			mSpeed.add(-5);
		}
		
		mLoc.add(mSpeed);
		
		// Constrain location
		mLoc.x = Util.constrain(mLoc.x, 0, WORLD_SIZE);
		mLoc.y = Util.constrain(mLoc.y, 0, WORLD_SIZE);
		
		if (mLoc.x < 0) mLoc.x = 0;
		else if (mLoc.x > WORLD_SIZE) mLoc.x = WORLD_SIZE;
		
		if (mLoc.y < 0) mLoc.y = 0;
		else if (mLoc.y > WORLD_SIZE) mLoc.y = WORLD_SIZE;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.blue);
		g.fill(new Circle(mLoc.x, mLoc.y, RADIUS));
		
		g.setColor(Color.white);
		g.drawLine(mLoc.x, mLoc.y, mLoc.x + mSpeed.x * 8, mLoc.y + mSpeed.y * 8);
	}
	
	public int getDirection(Vector2f p1, Vector2f p2) {
		float delta = (p1.x * p2.y) - (p1.y * p2.x);
		
		if (delta < 0) return 1;
		else return -1;
	}
	
	public boolean isAlive() {
		return mEnergy > 0;
	}
}
