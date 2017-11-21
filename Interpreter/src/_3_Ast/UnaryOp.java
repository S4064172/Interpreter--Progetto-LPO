package _3_Ast;

import static java.util.Objects.requireNonNull;

public abstract class UnaryOp implements Exp {
	protected final Exp exp;

	protected UnaryOp(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + exp + ")";
	}
}
