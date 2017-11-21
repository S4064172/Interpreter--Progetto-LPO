package _3_Ast;

import _4_Visitors.Visitor;

public class Add extends BinaryOp {
	public Add(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitAdd(left, right);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// left.typecheck(env).checkEqual(INT);
	// return right.typecheck(env).checkEqual(INT);
	// }
}
