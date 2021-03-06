package com.hahn.bio.util;

public final class Config {	
	public static int WORLD_SIZE = 2048;
	
	public static int START_BOIDS = 128,
					  MAX_BOIDS = 256,
					  MAX_PLANTS = 512;
	
	public static int START_BOID_ENERGY = 250,
				      START_PLANT_ENERGY = 500;
	
	public static float PLANT_GROW_SPEED = 0.3f,
						BOID_METABALIZE_SPEED = 0.1f,
						EAT_PLANT_SPEED = 2,
						EAT_BOID_SPEED = 4,
						PERCENT_SPEED_TOWARD_METABOLISM = 0.2f;
	
	public static int MAX_TURN_SPEED = 10;
	
	public static float GENOME_MUTATE_CHANGE = 0.001f;
	
	public static boolean DEBUG = false;
}
