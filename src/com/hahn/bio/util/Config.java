package com.hahn.bio.util;

public final class Config {	
	public static int WORLD_SIZE = 1028;
	
	public static int START_BOIDS = 64,
					  MAX_PLANTS = 256;
	
	public static int START_BOID_ENERGY = 250,
				      START_PLANT_ENERGY = 500;
	
	public static float PLANT_GROW_SPEED = 0.5f,
						BOID_METABALIZE_SPEED = 0.1f,
						PERCENT_SPEED_TOWARD_METABOLISM = 0.5f;
	
	public static float DEFAULT_MUTATE_CHANCE = 0.01f;
	
	public static boolean DEBUG = false;
}
