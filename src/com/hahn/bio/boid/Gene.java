package com.hahn.bio.boid;

import static com.hahn.bio.util.Config.*;

@SuppressWarnings("unused")
public enum Gene {
	MaxAge(add(add(add(mult(pos(gene(0)), 2), pos(gene(1))), abs(gene(3))), gene(5))),
	Red(pos(gene(7))),
	Green(pos(gene(9))),
	Blue(pos(gene(11))),
	RepDelay(add(add(pos(gene(12)), pos(gene(13))), 100)),
	MinRepEnergy(add(add(pos(gene(15)), pos(gene(16))), 100)),
	MinGiveEnergy(add(pos(gene(15)), 50)),
	SpeedMult(mult(frac(gene(17)), 3)),
	MetabolismRate(frac(gene(20))),
	Aggressiveness(max(frac(gene(21)), 0.2f)),
	Carnivore(frac(gene(22))),
	TurnSpeed(mult(frac(gene(23)), MAX_TURN_SPEED)),
	ViewRange(pos(gene(25))),
	ViewAngleError(mult(frac(gene(27)), MAX_TURN_SPEED));
	
	public final OP Algorithm;
	private Gene(OP algorithm) {
		this.Algorithm = algorithm;
	}
	
	public double calculate(Genome g) {
		return Algorithm.getValue(g);
	}
	
	private static IEvaluable gene(int idx) {
		return new GenePointer(idx);
	}
	
	private static OP add(Object a, Object b) {
		return new Add(a, b);
	}
	
	private static OP pos(Object a) {
		return add(a, -Byte.MIN_VALUE);
	}
	
	private static OP sub(Object a, Object b) {
		return new Sub(a, b);
	}
	
	private static OP abs(Object a) {
		return new Abs(a);
	}
	
	private static OP mult(Object a, Object b) {
		return new Mult(a, b);
	}
	
	private static OP div(Object a, Object b) {
		return new Div(a, b);
	}
	
	private static OP frac(Object a) {
		return div(abs(a), Byte.MAX_VALUE);
	}
	
	private static OP max(Object a, Object b) {
		return new Max(a, b);
	}
	
	private static class Max extends OP {
		public Max(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return Math.max(a.getValue(g), b.getValue(g));
		}
	}
	
	private static class Div extends OP {
		public Div(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) / b.getValue(g);
		}
	}
	
	private static class Mult extends OP {
		public Mult(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) * b.getValue(g);
		}
	}
	
	private static class Abs extends OP {
		public Abs(Object a) { super(a, null); }

		@Override
		public double getValue(Genome g) {
			return Math.abs(a.getValue(g));
		}
	}
	
	private static class Sub extends OP {
		public Sub(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) - b.getValue(g);
		}
	}
	
	private static class Add extends OP {
		public Add(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) + b.getValue(g);
		}
	}
	
	private static abstract class OP extends Constant {
		IEvaluable a, b;
		
		public OP(Object a, Object b) {
			super(0);
			
			if (a != null) {
				if (a instanceof IEvaluable) {
					this.a = (IEvaluable) a;
				} else {
					this.a = new Constant((Number) a);
				}
			}
			
			if (b != null) {
				if (b instanceof IEvaluable) {
					this.b = (IEvaluable) b;
				} else {
					this.b = new Constant((Number) b);
				}
			}
		}
		
		@Override
		public abstract double getValue(Genome g);
	}
	
	private static interface IEvaluable {
		public double getValue(Genome g);
	}
	
	private static class GenePointer implements IEvaluable {
		int idx;
		
		public GenePointer(int idx) { 
			this.idx = idx;
		}
		
		@Override
		public double getValue(Genome g) {
			return g.getGeneAt(idx);
		}
	}
	
	private static class Constant implements IEvaluable {
		Number d;
		
		public Constant(Number i) {
			this.d = i;
		}
		
		public double getValue(Genome g) {
			return d.doubleValue();
		}
	}
}
