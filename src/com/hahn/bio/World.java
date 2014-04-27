package com.hahn.bio;

import static com.hahn.bio.Constants.START_BOIDS;
import static com.hahn.bio.Constants.WORLD_SIZE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class World {
	public static final Random rand = new Random();

	public static Plants plants;
	
	private final List<Boid> mBoids;
	
	public World() {
		plants = Plants.create();
		
		mBoids = new ArrayList<Boid>();
		
		for (int i = 0; i < START_BOIDS; i++) {
			int x = rand.nextInt(WORLD_SIZE);
			int y = rand.nextInt(WORLD_SIZE);
			
			mBoids.add(new Boid(this, x, y));
		}
	}
	
	public void draw(Graphics g) {
		plants.draw(g);
		
		for (Boid b: mBoids) {
			b.draw(g);
		}
	}
	
	public void update() {
		Iterator<Boid> it = mBoids.iterator();
		while (it.hasNext()) {
			Boid b = it.next();
			b.update();
			
			// Remove if dead
			if (!b.isAlive()) {
				it.remove();
			}
		}
	}
	
	public List<Boid> getBoids() {
		return mBoids;
	}
	
	public PlantIdentifier findNearestPlant(Vector2f point) {
		return plants.findNearest(point);
	}
}
