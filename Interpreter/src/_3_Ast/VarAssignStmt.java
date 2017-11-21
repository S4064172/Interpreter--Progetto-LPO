package _3_Ast;

import static java.util.Objects.requireNonNull;

public abstract class VarAssignStmt implements Stmt {
	protected final Ident ident;
	protected final Exp exp;

	protected VarAssignStmt(Ident ident, Exp exp) {
		this.ident = requireNonNull(ident);
		this.exp = requireNonNull(exp);
	}

	public Ident getIdent() {
		return ident;
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + ident + "," + exp + ")";
	}
}
