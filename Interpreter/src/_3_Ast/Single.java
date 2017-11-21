package _3_Ast;

import static java.util.Objects.requireNonNull;

public class Single<T> {
	protected final T single;

	public Single(T single) {
		this.single = requireNonNull(single);
	}

	public T getSingle() {
		return single;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + single + ")";
	}
}
