package com.hahn.bio;

public class Util {
	public static float constrain(float x, int min, int max) {
		if (x < min) return min;
		else if (x > max) return max;
		else return x;
	}
}
