package _3_Ast;

import _4_Visitors.Visitor;

public class Mul extends BinaryOp {
	public Mul(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitMul(left, right);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// left.typecheck(env).checkEqual(INT);
	// return right.typecheck(env).checkEqual(INT);
	// }
}
