package com.hahn.bio.boid;

import static com.hahn.bio.util.Config.*;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import com.hahn.bio.MainGame;
import com.hahn.bio.World;

public class Genome {
	private byte[] mGenomeBytes;
	private Map<Gene, Double> mGenome;
	
	private int mRed, mGreen, mBlue;
	
	private final int mBrainStart = 32;
	private int mBrainEnd;
	
	public Genome() {
		mGenomeBytes = new byte[64];
		
		World.rand.nextBytes(mGenomeBytes);
		
		calculateGenes();
	}
	
	public Genome(Genome g) {
		mGenomeBytes = g.mGenomeBytes.clone();
		
		mutate();
		
		calculateGenes();
	}
	
	private void mutate() {
		for (int i = 0; i < mGenomeBytes.length; i++) {
			for (int j = 0; j < 8; j++) {
				if (World.rand.nextFloat() < GENOME_MUTATE_CHANGE) {
					mGenomeBytes[i] ^= (1 << j);
				}
			}
		}
	}
	
	private void calculateGenes() {
		mGenome = new HashMap<Gene, Double>();
		
		for (Gene g: Gene.values()) {
			mGenome.put(g, g.calculate(this));
		}
		
		int redTier   = (int) (mGenomeBytes.length * (1.0/3.0));
		int greenTier = (int) (mGenomeBytes.length * (2.0/3.0));
		int blueTier  = (int) (mGenomeBytes.length * (3.0/3.0));
		for (int i = 0; i < mGenomeBytes.length; i++) {
			int val = mGenomeBytes[i];
			for (int b = 0; b < 8; b++) {
				if ((val & (1 << b)) != 0) {
					if (i < redTier) {
						mRed += 1;
					} else if (i < greenTier) {
						mGreen += 1;
					} else if (i < blueTier) {
						mBlue += 1;
					}
				}
			}
		}
		
		double bitsPerColor = mGenomeBytes.length * 8 / 3.0;
		mRed   = (int) ((double) mRed   / bitsPerColor * 255);
		mGreen = (int) ((double) mGreen / bitsPerColor * 255);
		mBlue  = (int) ((double) mBlue  / bitsPerColor * 255);
	}
	
	public double get(Gene g) {
		return mGenome.get(g);
	}
	
	public Genome reproduce() {
		return new Genome(this);
	}
	
	public void draw(Graphics g, Genome compare) {
		g.setColor(new Color(100, 100, 100, 100));
		g.fillRect(0, 0, MainGame.WIDTH, MainGame.HEIGHT);
		
		final int yStep = 16;
		final int xStep = 8;
		final int rowLength = 8;
		
		int same = 0, total = 0;
		
		int y = 55;
		
		for (int i = 0; i < mGenomeBytes.length; i += rowLength, y += yStep) {
			int x = 55;
			for (int j = 0; j < rowLength && i + j < mGenomeBytes.length; j++) {
				int b = mGenomeBytes[i + j];
				
				for (int k = 0; k < 8; k++, x += xStep) {
					int maskedByte = (b & (1 << k));
					
					if (i + j >= mBrainStart && i + j < mBrainEnd) {
						g.setColor(Color.blue);
					} else {
						g.setColor(Color.white);
					}
					
					if (compare != null) {
						int compareByte = compare.mGenomeBytes[i + j];
						int compareMaskedByte = (compareByte & (1 << k));
						if (maskedByte != compareMaskedByte) {
							g.setColor(Color.red);
						} else {
							same += 1;
						}
						
						total += 1;
					}
					
					if (maskedByte != 0) {
						g.drawString("1", x, y);
					} else {
						g.drawString("0", x, y);
					}
				}
			}
		}
		
		if (total > 0) {
			g.setColor(Color.white);
			g.drawString(String.format("Match: %f%%", (float) same / total * 100), 225, 325);
		}
	}
	
	public int getActiveIn(int idx) {
		int active = 0;
		for (int i = 0; i < 8; i++) {
			if ((mGenomeBytes[idx] & (1 << i)) != 0) {
				active += 1;
			}
		}
		
		return active;
	}
	
	public byte getGeneAt(int idx) {
		return mGenomeBytes[idx];
	}
	
	public int getBrainStart() {
		return mBrainStart;
	}
	
	public void setBrainEnd(int i) {
		mBrainEnd = i;
	}
	
	public int getRed() {
		return mRed;
	}
	
	public int getGreen() {
		return mGreen;
	}
	
	public int getBlue() {
		return mBlue;
	}
}
