package _3_Ast;

import _4_Visitors.Visitor;

import static java.util.Objects.requireNonNull;

public class Pair implements Exp{
	
	private final Exp first;
	private final Exp second;

	public Pair(Exp first, Exp second) {
		this.first = requireNonNull(first);
		this.second = requireNonNull(second);
	}

	public Exp getFirst() {
		return first;
	}

	public Exp getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + first + "," + second + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPair(first, second);
	}
	
	
	
}
