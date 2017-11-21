package _3_Ast;

public abstract class PrimLiteral<T> implements Exp {

	protected final T value;

	public PrimLiteral(T n) {
		this.value = n;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + value + ")";
	}
}
