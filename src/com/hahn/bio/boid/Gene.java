package com.hahn.bio.boid;

public enum Gene {
	MaxAge(add(add(add(mult(pos(gene(0)), 2), pos(gene(1))), abs(gene(3))), gene(5))),
	Red(pos(gene(7))),
	Green(pos(gene(9))),
	Blue(pos(gene(11))),
	MinRepEnergy(add(add(pos(gene(15)), pos(gene(16))), 100)),
	MinGiveEnergy(add(pos(gene(15)), 50)),
	SpeedMult(mult(frac(gene(17)), 3));
	
	public final OP Algorithm;
	private Gene(OP algorithm) {
		this.Algorithm = algorithm;
	}
	
	public double calculate(Genome g) {
		return Algorithm.getValue(g);
	}
	
	static IEvaluable gene(int idx) {
		return new GenePointer(idx);
	}
	
	static OP add(Object a, Object b) {
		return new Add(a, b);
	}
	
	static OP pos(Object a) {
		return add(a, -Byte.MIN_VALUE);
	}
	
	static OP sub(Object a, Object b) {
		return new Sub(a, b);
	}
	
	static OP abs(Object a) {
		return new Abs(a);
	}
	
	static OP mult(Object a, Object b) {
		return new Mult(a, b);
	}
	
	static OP div(Object a, Object b) {
		return new Div(a, b);
	}
	
	static OP frac(Object a) {
		return div(abs(a), (int) Byte.MAX_VALUE);
	}
	
	static class Div extends OP {
		public Div(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) / b.getValue(g);
		}
	}
	
	static class Mult extends OP {
		public Mult(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) * b.getValue(g);
		}
	}
	
	static class Abs extends OP {
		public Abs(Object a) { super(a, null); }

		@Override
		public double getValue(Genome g) {
			return Math.abs(a.getValue(g));
		}
	}
	
	static class Sub extends OP {
		public Sub(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) - b.getValue(g);
		}
	}
	
	static class Add extends OP {
		public Add(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) + b.getValue(g);
		}
	}
	
	static abstract class OP extends Constant {
		IEvaluable a, b;
		
		public OP(Object a, Object b) {
			super(0);
			
			if (a != null) {
				if (a instanceof IEvaluable) {
					this.a = (IEvaluable) a;
				} else {
					this.a = new Constant((double) a);
				}
			}
			
			if (b != null) {
				if (b instanceof IEvaluable) {
					this.b = (IEvaluable) b;
				} else {
					this.b = new Constant((double) b);
				}
			}
		}
		
		@Override
		public abstract double getValue(Genome g);
	}
	
	static interface IEvaluable {
		public double getValue(Genome g);
	}
	
	static class GenePointer implements IEvaluable {
		int idx;
		
		public GenePointer(int idx) { 
			this.idx = idx;
		}
		
		@Override
		public double getValue(Genome g) {
			return g.getGeneAt(idx);
		}
	}
	
	static class Constant implements IEvaluable {
		double d;
		
		public Constant(double i) {
			this.d = i;
		}
		
		public double getValue(Genome g) {
			return d;
		}
	}
}
