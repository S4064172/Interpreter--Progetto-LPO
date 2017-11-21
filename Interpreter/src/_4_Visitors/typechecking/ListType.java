package _4_Visitors.typechecking;

public class ListType implements Type {

	private final Type elType;

	public static final String LIST = "LIST";

	public ListType(Type elType) {
		this.elType = elType;
	}

	public Type getElType() {
		return elType;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ListType))
			return false;
		ListType lt = (ListType) obj;
		return elType.equals(lt.elType);
	}

	@Override
	public int hashCode() {
		return 31 * elType.hashCode();
	}

	@Override
	public String toString() {
		return elType + " " + LIST;
	}
}
