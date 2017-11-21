package _3_Ast;

import _4_Visitors.Visitor;

public class Not extends UnaryOp {

	public Not(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitNot(exp);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// return exp.typecheck(env).checkEqual(BOOL);
	// }
}
