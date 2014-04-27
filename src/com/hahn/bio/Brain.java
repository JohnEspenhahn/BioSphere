package com.hahn.bio;

import java.util.Arrays;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class Brain extends Kernel {
	@Constant
	private final float[] mSigmoidLookup = new float[201];
	
	/** [0] input, [1] hidden, [2] output */
	@Constant
	private final int[] mSizes = new int[3];
	
	/** 0 - Input => Hidden, 1 - Hidden => Output */
	private final int[] mPassId = new int[1];
	
	// Brain properties
	private final float[] mInput;
	private final float[] mInputWeights;
	
	private final float[] mHidden;
	private final float[] mHiddenWeights;
	
	private final float[] mOutput;
	
	private Brain(int in, int hidden, int out) {
		this.setupSigmoid();
		
		this.mSizes[0] = in;
		this.mSizes[1] = hidden;
		this.mSizes[2] = out;
		
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
		
		this.mSizes[0] = b.mSizes[0];
		this.mSizes[1] = b.mSizes[1];
		this.mSizes[2] = b.mSizes[2];
		
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
		setExplicit(true);
		
		put(mSizes);
		put(mSigmoidLookup);
	}
	
	public static Brain create(int in, int hidden, int out) {
		Brain brain = new Brain(in, hidden, out);
		brain.init();
		
		return brain;
	}
	
	public int inputSize() {
		return mSizes[0];
	}
	
	public void update() {
		Range range;
		
		Arrays.fill(mHidden, 0);
		Arrays.fill(mOutput, 0);
		
		put(mInput);
		put(mHidden);
		put(mOutput);
		
		// Input => Hidden		
		mPassId[0] = 0;
		range = Range.create2D(mInput.length, mHidden.length);
		put(mPassId);
		execute(range);
		
		// Hidden => Output
		mPassId[0] = 1;
		range = Range.create2D(mHidden.length, mOutput.length);
		put(mPassId);
		execute(range);
	}
	
	public float[] getInput() {
		return mInput;
	}
	
	public float[] getHidden() {
		get(mHidden);
		
		return mHidden;
	}
	
	public float[] getOutput() {
		get(mOutput);
		
		return mOutput;
	}
	
	@Deprecated
	@Override
	public void run() {
		int pass = mPassId[0];
		int node = getGlobalId(0);
		int connection = getGlobalId(1);
		
		if (pass == 0) {
			mHidden[connection] += mInput[node] * mInputWeights[node * mSizes[1] + connection];
		} else if (pass == 1) {
			float sigmoid = lookupSigmoid(mHidden[node]);
			mOutput[connection] += sigmoid * mHiddenWeights[node * mSizes[2] + connection];
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
