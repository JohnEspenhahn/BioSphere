package com.hahn.bio.boid;

import static com.hahn.bio.util.Config.*;

import java.util.HashMap;
import java.util.Map;

import com.hahn.bio.World;

public class Genome {
	private byte[] bGenome;
	private Map<Gene, Double> genome;
	
	private int red, green, blue;
	
	public Genome() {
		bGenome = new byte[30];
		
		World.rand.nextBytes(bGenome);
		
		calculateGenes();
	}
	
	public Genome(Genome g) {
		bGenome = g.bGenome.clone();
		
		mutate();
		
		calculateGenes();
	}
	
	private void mutate() {
		for (int i = 0; i < bGenome.length; i++) {
			for (int j = 0; j < 8; j++) {
				if (World.rand.nextFloat() < GENOME_MUTATE_CHANGE) {
					bGenome[i] ^= (1 << j);
				}
			}
		}
	}
	
	private void calculateGenes() {
		genome = new HashMap<Gene, Double>();
		
		for (Gene g: Gene.values()) {
			genome.put(g, g.calculate(this));
		}
		
		int redTier   = (int) (bGenome.length * (1.0/3.0));
		int greenTier = (int) (bGenome.length * (2.0/3.0));
		int blueTier  = (int) (bGenome.length * (3.0/3.0));
		for (int i = 0; i < bGenome.length; i++) {
			int val = bGenome[i];
			for (int b = 0; b < 8; b++) {
				if ((val & (1 << b)) != 0) {
					if (i < redTier) {
						red += 1;
					} else if (i < greenTier) {
						green += 1;
					} else if (i < blueTier) {
						blue += 1;
					}
				}
			}
		}
		
		double bitsPerColor = bGenome.length * 8 / 3.0;
		red   = (int) ((double) red   / bitsPerColor * 255);
		green = (int) ((double) green / bitsPerColor * 255);
		blue  = (int) ((double) blue  / bitsPerColor * 255);
	}
	
	public double get(Gene g) {
		return genome.get(g);
	}
	
	public Genome reproduce() {
		return new Genome(this);
	}
	
	public byte getGeneAt(int idx) {
		return bGenome[idx];
	}
	
	public int getRed() {
		return red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public int getBlue() {
		return blue;
	}
}
