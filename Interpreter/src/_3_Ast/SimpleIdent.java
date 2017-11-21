package _3_Ast;

import static java.util.Objects.requireNonNull;

import _4_Visitors.Visitor;

public class SimpleIdent implements Ident {
	private final String name;

	public SimpleIdent(String name) {
		this.name = requireNonNull(name);
	}

	public String getName() {
		return name;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Ident))
			return false;
		return name.equals(((Ident) obj).getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + name + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIdent(name);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// return env.lookup(this);
	// }
}
