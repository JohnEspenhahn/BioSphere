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

public class Boid extends ITargetable {
	public static int RADIUS = 6;
	
	private Integer mId;
	
	private World mWorld;
	private Brain mBrain;
	private Genome mGenome;
	
	private Vector2f mLoc;

	private ITargetable mTarget;
	private int mCheckDelay;
	
	/** Normalized direction */
	private Vector2f mMoveDir;
	private float mSpeed;

	private boolean mStopMoving;
	
	private Color mColor;
	
	private float mEnergy;
	private int mRepDelay;
	
	private int mAge;
	
	public Boid(Boid parent, int energy) {
		this(parent.mWorld, parent.mLoc.x + rand.nextInt(100) - 50, parent.mLoc.y + rand.nextInt(100) - 50, energy);
		
		mGenome = parent.mGenome.reproduce();
		
		createFromGenome();
	}
	
	public Boid(World world, float x, float y, int energy) {
		mWorld = world;
		mGenome = new Genome();
		
		mLoc = new Vector2f(x, y);
		constrainLocation();
		
		Vector2f dir = new Vector2f(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f);
		mMoveDir = dir.normalise();
		
		mSpeed = 1;
		
		mTarget = null;
		mEnergy = energy;
		mStopMoving = false;
		
		createFromGenome();
	}
	
	private void createFromGenome() {
		mBrain = Brain.create(mGenome);
		
		mColor = new Color(mGenome.getRed(), mGenome.getGreen(), mGenome.getBlue());
		mRepDelay = (int) mGenome.get(Gene.RepDelay);
	}
	
	public void update() {
		if (mStopMoving) {
			mEnergy -= BOID_METABALIZE_SPEED / 2;
			
			return;
		}
		
		// -------------------------------
		// Every tick update
		// -------------------------------
		double speed = mSpeed * mGenome.get(Gene.SpeedMult);
		
		mAge += 1;
		mEnergy -= BOID_METABALIZE_SPEED + speed * PERCENT_SPEED_TOWARD_METABOLISM;
		
		/*
		if (mAge > mGenome.get(Gene.MaxAge)) {
			mEnergy -= BOID_METABALIZE_SPEED * 2;
		}
		*/
		
		// If no target then find one
		if (mTarget == null || mTarget.isGone()) {
			mTarget = getTarget();
			return;
			
		} else {
			// If at target then eat
			float distSqu = mLoc.distanceSquared(mTarget.getLoc()); 
			if (distSqu < RADIUS*RADIUS + mTarget.getRadius()*mTarget.getRadius()) {
				clearInputs();
				
				float energy = 0;
				if (mTarget instanceof PlantIdentifier) {
					PlantIdentifier plant = (PlantIdentifier) mTarget;
					
					energy = World.plants.eat(plant, EAT_PLANT_SPEED);
				} else if (mTarget instanceof Boid) {
					Boid boid = (Boid) mTarget;
					
					float aggressiveness = (float) mGenome.get(Gene.Aggressiveness);
					energy = World.boids.eat(boid, EAT_BOID_SPEED, aggressiveness);
				}
				
				// Waste
				energy *= mGenome.get(Gene.MetabolismRate);
				
				// Add energy left
				mEnergy += energy;
				
				if (mTarget.isGone()) {
					mTarget = null;
				}
				
			// Otherwise not at target and need to update direction
			} else {
				Vector2f targVec = new Vector2f(mTarget.getX() - mLoc.x, mTarget.getY() - mLoc.y).normalise();
				float[] input = mBrain.getInput();
				input[1] = 1;
				
				int dir = getDirection(mMoveDir, targVec);
				if (isInViewAngleError(mMoveDir, targVec, dir)) {
					// Forward
					input[0] = 0;
				} else {
					if (dir < 0) {
						// Left
						input[0] = -1;
					} else {
						// Right
						input[0] = 1;
					}
				}
				
				// Update target once a second
				if (--mCheckDelay < 0) {
					mTarget = getTarget();
				}
			}
		}
		
		// -------------------------------
		// If has energy then reproduce
		// -------------------------------
		if (--mRepDelay < 0 && mEnergy > mGenome.get(Gene.MinRepEnergy)) {
			reproduce((int) mGenome.get(Gene.MinGiveEnergy));
		}
		
		
		// -------------------------------
		// Run ANN
		// -------------------------------
		mBrain.update();
		
		
		// -------------------------------
		// Move based on ANN output
		// -------------------------------
		float[] output = mBrain.getOutput();
		if (output[0] > 0.2) {
			// Right
			mMoveDir.add(mGenome.get(Gene.TurnSpeed));
		} else if (output[0] < -0.2) {
			// Left
			mMoveDir.add(-mGenome.get(Gene.TurnSpeed));
		} else {
			// Forward
		}
		
		// Update speed for next tick
		mSpeed = output[1] + 0.5f;
		if (mSpeed < 0) mSpeed = 0;
		
		// Move with this tick's speed
		mLoc.x += mMoveDir.x * speed;
		mLoc.y += mMoveDir.y * speed;
		
		constrainLocation();
	}
	
	private boolean isInViewAngleError(Vector2f mMoveDir2, Vector2f targVec, int dir) {
		return (dir < mGenome.get(Gene.ViewAngleError) && mMoveDir.dot(targVec) >= 0);
	}

	private void clearInputs() {
		float[] input = mBrain.getInput();
		input[0] = 0;
		input[1] = 0;
	}
	
	private ITargetable getTarget() {		
		if (isCarnivore()) {
			mCheckDelay = 40;
			return World.boids.findNearest(this);
		} else {
			mCheckDelay = 20;
			return World.plants.findNearest(this);
		}
	}
	
	public void constrainLocation() {
		mLoc.x = Util.constrain(mLoc.x, 0, WORLD_SIZE);
		mLoc.y = Util.constrain(mLoc.y, 0, WORLD_SIZE);
	}
	
	public void reproduce(int energy) {		
		Boid b = new Boid(this, energy);
		boolean added = World.boids.add(b);
		
		if (added) {
			mRepDelay = (int) mGenome.get(Gene.RepDelay);
			mEnergy -= energy;
		}
	}
	
	public void kill() {
		mEnergy = -1;
	}
	
	public void draw(Graphics g) {
		g.setColor(mColor);
		g.fill(new Circle(mLoc.x, mLoc.y, getRadius()));
		
		if (!mStopMoving) {
			if (isCarnivore()) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.white);
			}
	
			g.drawLine(mLoc.x, mLoc.y, mLoc.x + mMoveDir.x * 8, mLoc.y + mMoveDir.y * 8);
			
			if (DEBUG) {
				if (isCarnivore()) {
					g.setColor(Color.yellow);
					g.draw(new Circle(mLoc.x, mLoc.y, (float) mGenome.get(Gene.ViewRange)));
				}
				
				if (mTarget != null) {
					g.setColor(Color.red);
					g.drawLine(mLoc.x, mLoc.y, mTarget.getX(), mTarget.getY());
				}
			}
		}
	}
	
	public void drawGenome(Graphics g, Boid compare) {
		mGenome.draw(g, (compare == null ? null : compare.mGenome));
	}
	
	public int getDirection(Vector2f p1, Vector2f p2) {
		float delta = (p1.x * p2.y) - (p1.y * p2.x);
		
		if (delta < 0) return 1;
		else return -1;
	}
	
	public void setId(int id) {
		if (mId == null) {
			mId = id;
		} else {
			throw new RuntimeException("Tried to set boid id twice!");
		}
	}
	
	public boolean isCarnivore() {
		return mGenome.get(Gene.MetabolismRate) > 0.9 && mGenome.get(Gene.Carnivore) > 0.5;
	}
	
	public void stopMoving() {
		mStopMoving = true;
	}
	
	public void removeEnergy(float amnt) {
		mEnergy -= amnt;
	}
	
	public float getEnergy() {
		return mEnergy;
	}
	
	public int getId() {
		return mId;
	}
	
	public boolean isAlive() {
		return mEnergy > 0;
	}
	
	@Override
	public int getX() {
		return (int) mLoc.x;
	}
	
	@Override
	public int getY() {
		return (int) mLoc.y;
	}

	@Override
	public Vector2f getLoc() {
		return mLoc;
	}

	@Override
	public int getRadius() {
		return RADIUS;
	}

	@Override
	public boolean isGone() {
		return !isAlive();
	}
}
