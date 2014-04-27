package com.hahn.bio.boid;

public enum Gene {
	MaxAge(add(add(add(mult(pos(gene(0)), 2), pos(gene(1))), abs(gene(3))), gene(5))),
	Red(pos(gene(7))),
	Green(pos(gene(9))),
	Blue(pos(gene(11)));
	
	public final OP Algorithm;
	private Gene(OP algorithm) {
		this.Algorithm = algorithm;
	}
	
	public double calculate(Genome g) {
		return Algorithm.getValue(g);
	}
	
	public static Evaluable gene(int idx) {
		return new GenePointer(idx);
	}
	
	public static OP add(Object a, Object b) {
		return new Add(a, b);
	}
	
	public static OP pos(Object a) {
		return new Add(a, -Byte.MIN_VALUE);
	}
	
	public static OP sub(Object a, Object b) {
		return new Sub(a, b);
	}
	
	public static OP abs(Object a) {
		return new Abs(a);
	}
	
	public static OP mult(Object a, Object b) {
		return new Mult(a, b);
	}
	
	public static OP div(Object a, Object b) {
		return new Div(a, b);
	}
	
	public static class Div extends OP {
		public Div(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) / b.getValue(g);
		}
	}
	
	public static class Mult extends OP {
		public Mult(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) * b.getValue(g);
		}
	}
	
	public static class Abs extends OP {
		public Abs(Object a) { super(a, null); }

		@Override
		public double getValue(Genome g) {
			return Math.abs(a.getValue(g));
		}
	}
	
	public static class Sub extends OP {
		public Sub(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) - b.getValue(g);
		}
	}
	
	public static class Add extends OP {
		public Add(Object a, Object b) { super(a, b); }

		@Override
		public double getValue(Genome g) {
			return a.getValue(g) + b.getValue(g);
		}
	}
	
	public static abstract class OP extends Evaluable {
		Evaluable a, b;
		
		public OP(Object a, Object b) {
			super(0);
			
			if (a != null) {
				if (a instanceof Evaluable) {
					this.a = (Evaluable) a;
				} else {
					this.a = new Evaluable((int) a);
				}
			}
			
			if (b != null) {
				if (b instanceof Evaluable) {
					this.b = (Evaluable) b;
				} else {
					this.b = new Evaluable((int) b);
				}
			}
		}
		
		@Override
		public abstract double getValue(Genome g);
	}
	
	public static class GenePointer extends Evaluable {
		public GenePointer(int idx) { super(idx); }
		
		@Override
		public double getValue(Genome g) {
			return g.getGeneAt((int) d);
		}
	}
	
	public static class Evaluable {
		double d;
		
		public Evaluable(double i) {
			this.d = i;
		}
		
		public double getValue(Genome g) {
			return d;
		}
	}
}
