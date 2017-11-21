package _3_Ast;

import _4_Visitors.Visitor;

public class VarStmt extends VarAssignStmt {

	public VarStmt(Ident ident, Exp exp) {
		super(ident, exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitVarStmt(ident, exp);
	}

	// @Override
	// public void typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// env.newFresh(ident, exp.typecheck(env));
	// }
}
