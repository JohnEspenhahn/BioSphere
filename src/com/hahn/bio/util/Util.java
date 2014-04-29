package com.hahn.bio.util;

import org.newdawn.slick.geom.Vector2f;

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
	 * Mutates `chance` % of values in array by [-maxAmnt, maxAmnt)
	 */
	public static void mutate(float[] vals, float maxAmnt, float chance) {
		for (int i = 0; i < vals.length; i++) {
			if (World.rand.nextFloat() < chance) {
				vals[i] += (World.rand.nextFloat() * 2 - 1) * maxAmnt;
			}
		}
	}
	
	public static void main(String[] args) {
		int[][][] points = new int[][][] {
			new int[][] {
					new int[] { 10, 0 },
					new int[] { 10, 1 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { 10, 2 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { 10, -1 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { 10, -2 }
			},
			new int[][] {
					new int[] { 0, 10 },
					new int[] { -1, 10 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { -10, 0 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { -10, -1 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { -10, 1 }
			},
			new int[][] {
					new int[] { 10, 0 },
					new int[] { 10, 10 }
			},
		};
		
		for (int[][] ps: points) {
			Vector2f p1 = new Vector2f(ps[0][0], ps[0][1]);
			Vector2f p2 = new Vector2f(ps[1][0], ps[1][1]);
			
			p1.normalise();
			p2.normalise();
			
			float delta = (p1.x * p2.y) - (p1.y * p2.x);
			double theta = Math.acos(p1.dot(p2));
			
			System.out.printf("<%d, %d>\t<%d, %d> = %f theta = %f\n", ps[0][0], ps[0][1], ps[1][0], ps[1][1], delta, theta);
		}
	}
}
