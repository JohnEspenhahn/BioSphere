package com.hahn.bio.boid;

import static com.hahn.bio.util.Config.*;

import java.util.HashMap;
import java.util.Map;

import com.hahn.bio.World;

public class Genome {
	private byte[] bGenome;
	private Map<Gene, Double> genome;
	
	public Genome() {
		bGenome = new byte[128];
		
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
				if (World.rand.nextFloat() < DEFAULT_MUTATE_CHANCE) {
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
}
