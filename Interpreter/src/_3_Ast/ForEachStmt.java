package _3_Ast;

import static java.util.Objects.requireNonNull;

import _4_Visitors.Visitor;

public class ForEachStmt implements Stmt {
	private final Ident ident;
	private final Exp exp;
	private final StmtSeq block;

	public ForEachStmt(Ident id, Exp exp, StmtSeq block) {
		this.ident = requireNonNull(id);
		this.exp = requireNonNull(exp);
		this.block = requireNonNull(block);
	}

	public Ident getId() {
		return ident;
	}

	public Exp getExp() {
		return exp;
	}

	public StmtSeq getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + ident + "," + exp + "," + block + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitForEachStmt(ident, exp, block);
	}

	// @Override
	// public void typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// Type ty = exp.typecheck(env).checkList();
	// env.enterScope();
	// env.newFresh(ident, ty);
	// block.typecheck(env);
	// env.exitScope();
	// }

}
