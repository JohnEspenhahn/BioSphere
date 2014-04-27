package com.hahn.bio.boid;

import static com.hahn.bio.World.rand;
import static com.hahn.bio.util.Config.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import com.hahn.bio.World;
import com.hahn.bio.plant.ITargetable;
import com.hahn.bio.plant.PlantIdentifier;
import com.hahn.bio.util.Util;

public class Boid {
	public static final int RADIUS = 5;
	
	private World mWorld;
	private Brain mBrain;
	private Genome mGenome;
	
	private Vector2f mLoc;

	private ITargetable mTarget;
	private int mCheckDelay;
	
	/** Normalized direction */
	private Vector2f mMoveDir;
	private float mSpeed;

	private Color mColor;
	private float mEnergy;
	private int mAge;
	
	public Boid(Boid parent, int energy) {
		this(parent.mWorld, parent.mLoc.x + rand.nextInt(100) - 50, parent.mLoc.y + rand.nextInt(100) - 50, energy);
		
		mBrain = parent.mBrain.reproduce();
		mGenome = parent.mGenome.reproduce();
		
		recalculate();
	}
	
	public Boid(World world, float x, float y, int energy) {
		mWorld = world;
		mBrain = Brain.create(3, 4, 3);
		mGenome = new Genome();
		
		mLoc = new Vector2f(x, y);
		constrainLocation();
		
		Vector2f dir = new Vector2f(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f);
		mMoveDir = dir.normalise();
		
		mSpeed = 1;
		
		mTarget = null;
		mEnergy = energy;
		
		recalculate();
	}
	
	private void recalculate() {
		mColor = new Color((int) mGenome.get(Gene.Red), (int) mGenome.get(Gene.Green), (int) mGenome.get(Gene.Blue));
	}
	
	public void update() {
		// -------------------------------
		// Every tick update
		// -------------------------------
		mAge += 1;
		mEnergy -= BOID_METABALIZE_SPEED + mSpeed * PERCENT_SPEED_TOWARD_METABOLISM;
		
		if (mAge > mGenome.get(Gene.MaxAge)) {
			mEnergy -= BOID_METABALIZE_SPEED * 2;
		}
		
		// If no target then find one
		if (mTarget == null || mTarget.isGone()) {
			mTarget = World.plants.findNearest(mLoc);
			return;
			
		} else {
			// If at target then eat
			float distSqu = mLoc.distanceSquared(mTarget.getLoc()); 
			if (distSqu < RADIUS*RADIUS + mTarget.getRadius()*mTarget.getRadius()) {
				clearInputs();
				
				if (mTarget instanceof PlantIdentifier) {
					PlantIdentifier plant = (PlantIdentifier) mTarget;
					
					// Get removed energy
					float energy = World.plants.eat(plant, 5);
					
					// Waste
					energy *= 0.55f;
					
					// Add energy left
					mEnergy += energy;
				}
				
			// Otherwise not at target and need to update direction
			} else {
				Vector2f targVec = new Vector2f(mTarget.getX() - mLoc.x, mTarget.getY() - mLoc.y).normalise();
				int dir = getDirection(mMoveDir, targVec);
				
				float[] input = mBrain.getInput();
				input[2] = 0;
				if (dir < 0) {
					// Left
					input[0] = 1;
					input[1] = 0;
				} else {
					// Right
					input[0] = 0;
					input[1] = 1;
				}
				
				// Update target once a second
				if (--mCheckDelay < 0) {
					mCheckDelay = 20;
					mTarget = World.plants.findNearest(mLoc);
				}
			}
		}
		
		// -------------------------------
		// If has energy then reproduce
		// -------------------------------
		if (mEnergy > START_BOID_ENERGY * 2) {
			reproduce(START_BOID_ENERGY);
		}
		
		
		// -------------------------------
		// Run ANN
		// -------------------------------
		mBrain.update();
		
		
		// -------------------------------
		// Move based on ANN output
		// -------------------------------
		float[] output = mBrain.getOutput();		
		if (output[0] > output[1]) {
			// Right
			mMoveDir.add(5);
		} else {
			// Left
			mMoveDir.add(-5);
		}
		
		mSpeed = output[2] + 0.5f;
		if (mSpeed < 0) mSpeed = 0;
		
		mLoc.x += mMoveDir.x * mSpeed;
		mLoc.y += mMoveDir.y * mSpeed;
		
		constrainLocation();
	}
	
	private void clearInputs() {
		float[] input = mBrain.getInput();
		input[0] = 0;
		input[1] = 0;
		input[2] = 1;
	}
	
	public void constrainLocation() {
		mLoc.x = Util.constrain(mLoc.x, 0, WORLD_SIZE);
		mLoc.y = Util.constrain(mLoc.y, 0, WORLD_SIZE);
	}
	
	public void reproduce(int energy) {
		mEnergy -= energy;
		
		Boid b = new Boid(this, energy);
		mWorld.addBoid(b);
	}
	
	public void kill() {
		
	}
	
	public void draw(Graphics g) {
		g.setColor(mColor);
		g.fill(new Circle(mLoc.x, mLoc.y, RADIUS));
		
		g.setColor(Color.white);
		g.drawLine(mLoc.x, mLoc.y, mLoc.x + mMoveDir.x * 8, mLoc.y + mMoveDir.y * 8);
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
