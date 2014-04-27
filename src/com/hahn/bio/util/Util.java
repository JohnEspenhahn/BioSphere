package com.hahn.bio.util;

import static com.hahn.bio.util.Config.*;

import com.hahn.bio.World;

public class Util {	
	public static String[] trim(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
		}
		
		return arr;
	}
	
	public static float constrain(float x, int min, int max) {
		if (x < min) return min;
		else if (x > max) return max;
		else return x;
	}
	
	/**
	 * Fills array with random values [-max, max)
	 */
	public static void fillRandom(float[] vals, float max) {
		for (int i = 0; i < vals.length; i++) {
			vals[i] = (World.rand.nextFloat() * 2 - 1) * max;
		}
	}

	/**
	 * Mutates DEFAULT_MUTATE_CHANCE % of values in array by [-maxAmnt, maxAmnt)
	 */
	public static void mutate(float[] vals, float maxAmnt) {
		Util.mutate(vals, maxAmnt, DEFAULT_MUTATE_CHANCE);
	}
	
	/**
	 * Mutates `chance` % of values in array by [-maxAmnt, maxAmnt)
	 */
	public static void mutate(float[] vals, float maxAmnt, float chance) {
		for (int i = 0; i < vals.length; i++) {
			if (World.rand.nextFloat() < chance) {
				vals[i] += (World.rand.nextFloat() * 2 - 1) * maxAmnt;
			}
		}
	}
}
