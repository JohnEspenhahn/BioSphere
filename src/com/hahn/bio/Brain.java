package com.hahn.bio;

import java.util.Arrays;

public class Brain {
	private final float[] mSigmoidLookup = new float[201];
	
	// Brain properties
	private final float[] mInput;
	private final float[] mInputWeights;
	
	private final float[] mHidden;
	private final float[] mHiddenWeights;
	
	private final float[] mOutput;
	
	private Brain(int in, int hidden, int out) {
		this.setupSigmoid();
		
		// Brain properties
		this.mInput = new float[in];
		this.mInputWeights = new float[in * hidden];
		Util.fillRandom(mInputWeights, 1);
		
		this.mHidden = new float[hidden];
		this.mHiddenWeights = new float[hidden * out];
		Util.fillRandom(mHiddenWeights, 1);
		
		this.mOutput = new float[out];
	}
	
	private Brain(Brain b) {
		this.setupSigmoid();
		
		// Brain properties
		this.mInput = b.mInput.clone();
		this.mInputWeights = b.mInputWeights.clone();
		Util.mutate(mInputWeights, 0.1f);
		
		this.mHidden = b.mHidden.clone();
		this.mHiddenWeights = b.mHiddenWeights.clone();
		Util.mutate(mHiddenWeights, 0.1f);
		
		this.mOutput = b.mOutput.clone();
	}
	
	private void init() {
		
	}
	
	public static Brain create(int in, int hidden, int out) {
		Brain brain = new Brain(in, hidden, out);
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
	
	public Brain reproduce() {
		Brain b = new Brain(this);
		b.init();
		
		return b;
	}
}
