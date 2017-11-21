package _3_Ast;

import _4_Visitors.Visitor;

public class AssignStmt extends VarAssignStmt {

	public AssignStmt(Ident ident, Exp exp) {
		super(ident, exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitAssignStmt(ident, exp);
	}

	// @Override
	// public void typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// Type expected = env.lookup(ident);
	// exp.typecheck(env).checkEqual(expected);
	// }
}
