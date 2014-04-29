package com.hahn.bio.boid;

import java.util.Arrays;

public class Brain {
	private final float[] mSigmoidLookup = new float[201];
	
	// Brain properties
	private final float[] mInput;
	private final float[] mInputWeights;
	
	private final float[] mHidden;
	private final float[] mHiddenWeights;
	
	private final float[] mOutput;
	
	private Brain(Genome genome) {
		this.setupSigmoid();
		
		// Brain properties
		final int in = 2, hidden = 3, out = 2;
		int brainGene = genome.getBrainStart();
		
		this.mInput = new float[in];
		
		// Load hidden weights from genome
		this.mInputWeights = new float[in * hidden];
		for (int i = 0; i < mInputWeights.length; i++) {
			int v = genome.getGeneAt(brainGene++);
			
			mInputWeights[i] = (float) v / Byte.MAX_VALUE;
		}
		
		this.mHidden = new float[hidden];
		
		// Load output weights from genome
		this.mHiddenWeights = new float[hidden * out];
		for (int i = 0; i < mHiddenWeights.length; i++) {
			int v = genome.getGeneAt(brainGene++);
			
			mHiddenWeights[i] = (float) v / Byte.MAX_VALUE;
		}
		
		this.mOutput = new float[out];
	}
	
	private void init() {
		
	}
	
	public static Brain create(Genome genome) {
		Brain brain = new Brain(genome);
		brain.init();
		
		return brain;
	}
	
	public void update() {		
		Arrays.fill(mHidden, 0);
		Arrays.fill(mOutput, 0);
		
		// Input => Hidden
		for (int i = 0; i < mInput.length; i++) {
			for (int j = 0; j < mHidden.length; j++) {
				run(0, i, j);
			}
		}
		
		// Hidden => Output
		for (int i = 0; i < mHidden.length; i++) {
			for (int j = 0; j < mOutput.length; j++) {
				run(1, i, j);
			}
		}
		
	}
	
	public float[] getInput() {
		return mInput;
	}
	
	public float[] getHidden() {		
		return mHidden;
	}
	
	public float[] getOutput() {		
		return mOutput;
	}
	
	public void run(int pass, int node, int connection) {		
		if (pass == 0) {
			mHidden[connection] += mInput[node] * mInputWeights[node * mHidden.length + connection];
		} else if (pass == 1) {
			float sigmoid = lookupSigmoid(mHidden[node]);
			mOutput[connection] += sigmoid * mHiddenWeights[node * mOutput.length + connection];
		}
	}

	public float lookupSigmoid(float level) {
		if (level > 1) level = 1;
		else if (level < -1) level = -1;
		
		return mSigmoidLookup[(int) ((level + 1) * 100)];
	}
	
	private void setupSigmoid() {
		for (int i = 0; i < mSigmoidLookup.length; i++) {
			float x = (i / 100.0f) - 1;
			mSigmoidLookup[i] = (float) Math.tanh(x); // 2.0 / (1.0 + exp(-2.0 * x))
		}
	}
}
