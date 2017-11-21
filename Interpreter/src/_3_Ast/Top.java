package _3_Ast;

import _4_Visitors.Visitor;

public class Top extends UnaryOp {

	public Top(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitTop(exp);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// return exp.typecheck(env).checkList();
	// }

}
