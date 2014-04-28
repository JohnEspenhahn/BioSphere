package com.hahn.bio;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.hahn.bio.boid.Boids;
import com.hahn.bio.plant.Plants;

public class World {
	public static final Random rand = new Random();

	public static Plants plants;
	public static Boids boids;
	
	public World() {
		plants = Plants.create();
		
		boids = Boids.create(this);
	}
	
	public void draw(Graphics g) {
		plants.updateAndDraw(g);
		
		boids.draw(g);
		
		g.setColor(Color.white);
	}
	
	public void update() {		
		boids.update();
	}
}
