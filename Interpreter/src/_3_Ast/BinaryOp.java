package _3_Ast;

import static java.util.Objects.requireNonNull;

public abstract class BinaryOp implements Exp {
	protected final Exp left;
	protected final Exp right;

	protected BinaryOp(Exp left, Exp right) {
		this.left = requireNonNull(left);
		this.right = requireNonNull(right);
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

}
