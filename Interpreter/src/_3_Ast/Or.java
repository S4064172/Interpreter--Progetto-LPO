package _3_Ast;

import _4_Visitors.Visitor;

public class Or extends BinaryOp {
	public Or(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitOr(left, right);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// left.typecheck(env).checkEqual(BOOL);
	// return right.typecheck(env).checkEqual(BOOL);
	// }
}
