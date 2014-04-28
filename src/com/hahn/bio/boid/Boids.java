package com.hahn.bio.boid;

import static com.hahn.bio.World.rand;
import static com.hahn.bio.util.Config.*;

import java.util.Arrays;
import java.util.Stack;

import org.newdawn.slick.Graphics;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.hahn.bio.World;

public class Boids extends Kernel {
	private static boolean created = false;
	
	private int[] mRequest;
	private int[] mResponse;
	
	private int[] mXs;
	private int[] mYs;
	private boolean[] mIsCarnivore;
	
	private Boid[] mBoids;
	
	private final Stack<Integer> mFreeSlots;
	
	private Boids(int max) {		
		mRequest = new int[3];
		mResponse = new int[2];
		
		mXs = new int[max];
		mYs = new int[max];
		mIsCarnivore = new boolean[max];
		
		mBoids = new Boid[max];
		
		mFreeSlots = new Stack<Integer>();
		for (int i = 0; i < max; i++) {
			mFreeSlots.push(i);
		}
	}
	
	private void init() {
		setExplicit(true);
		
		Arrays.fill(mXs, -1);
		Arrays.fill(mYs, -1);
	}
	
	public static Boids create(World world) {
		if (created) {
			throw new RuntimeException("Can only create one `Boids` object!");
		}
		
		created = true;
		Boids boids = new Boids(MAX_BOIDS);
		boids.init();
		
		for (int i = 0; i < START_BOIDS; i++) {
			int x = rand.nextInt(WORLD_SIZE);
			int y = rand.nextInt(WORLD_SIZE);
			
			boids.add(new Boid(world, x, y, START_BOID_ENERGY));
		}
		
		boids.put(boids.mXs);
		boids.put(boids.mYs);
		
		return boids;
	}
	
	public void draw(Graphics g) {
		for (Boid b: mBoids) {
			if (b != null) {
				b.draw(g);
			}
		}
	}
	
	public void update() {
		for (int id = 0; id < mBoids.length; id++) {
			Boid b = mBoids[id];
			if (b != null) {
				b.update();
				
				// Remove if dead
				if (!b.isAlive()) {
					remove(b);
				} else {
					mXs[b.getId()] = (int) b.getX();
					mYs[b.getId()] = (int) b.getY();
				}
			}
		}
		
		put(mXs);
		put(mYs);
		put(mIsCarnivore);
	}
	
	public boolean add(Boid b) {
		if (mFreeSlots.size() > 0) {
			int id = mFreeSlots.pop();
			
			b.setId(id);
			
			mXs[id] = (int) b.getX();
			mYs[id] = (int) b.getY();
			mIsCarnivore[id] = b.isCarnivore();
			
			mBoids[id] = b;
			
			return true;
		} else {
			return false;
		}
	}
	
	public float eat(Boid boid, float amnt, float stunChance) {		
		if (boid.getEnergy() > amnt) {
			boid.removeEnergy(amnt);
			
			if (World.rand.nextDouble() < stunChance) {
				boid.stopMoving();
			}
		} else {
			amnt = boid.getEnergy();
			remove(boid);
		}
		
		return amnt;
	}
	
	public void remove(Boid b) {
		b.kill();
		mBoids[b.getId()] = null;
		
		mXs[b.getId()] = -1;
		mYs[b.getId()] = -1;
		
		mFreeSlots.push(b.getId());
	}
	
	public Boid findNearest(Boid b) {
		mRequest[0] = b.getId();
		mRequest[1] = (int) b.getX();
		mRequest[2] = (int) b.getY();
		
		mResponse[0] = Integer.MAX_VALUE;
		mResponse[1] = -1;
		
		put(mRequest);
		put(mResponse);
		
		Range range = Range.create(mBoids.length);
		execute(range);
		
		get(mResponse);
		
		int id = mResponse[1]; 
		if (id != -1) {
			return mBoids[id];
		} else {
			return null;
		}
	}
	
	@Deprecated
	@Override
	public void run() {
		int rId = mRequest[0];
		int rX = mRequest[1];
		int rY = mRequest[2];
		
		int id = getGlobalId();
		int thisX = mXs[id];
		int thisY = mYs[id];
		boolean thisIsCarnivore = mIsCarnivore[id];
		
		if (id != rId && !thisIsCarnivore && thisX >= 0 && thisY >= 0) {
			int distSqu = distanceSqu(rX, rY, thisX, thisY);
			
			if (distSqu < mResponse[0]) {
				mResponse[0] = distSqu;
				mResponse[1] = id;
			}
		}
	}
	
	private int distanceSqu(int x1, int y1, int x2, int y2) {
		return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
	}
	
	public int size() {
		return mBoids.length - mFreeSlots.size();
	}
}
