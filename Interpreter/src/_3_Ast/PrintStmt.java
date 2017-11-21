package _3_Ast;

import static java.util.Objects.requireNonNull;

import _4_Visitors.Visitor;

public class PrintStmt implements Stmt {
	private final Exp exp;

	public PrintStmt(Exp exp) {
		this.exp = requireNonNull(exp);
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + exp + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPrintStmt(exp);
	}

	// @Override
	// public void typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// exp.typecheck(env);
	// }
}
