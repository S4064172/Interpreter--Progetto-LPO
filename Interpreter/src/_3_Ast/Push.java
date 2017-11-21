package _3_Ast;

import _4_Visitors.Visitor;

public class Push implements Exp {
	private final Exp left;
	private final Exp right;

	public Push(Exp left, Exp right) {
		this.left = left;
		this.right = right;
	}

	public Exp getLeft() {
		return left;
	}

	public Exp getRight() {
		return right;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + left + "," + right + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPush(left, right);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// Type elType = left.typecheck(env);
	// return right.typecheck(env).checkEqual(new ListType(elType));
	// }
}
