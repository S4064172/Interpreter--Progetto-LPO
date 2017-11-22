package _4_Visitors.typechecking;

public class PairType implements Type{
	
	private final Type elTypeFirst;
	private final Type elTypeSecond;

	
	public static final String PAIR = "PAIR";

	public PairType(Type elTypeFirst, Type elTypeSecond) {
		this.elTypeFirst = elTypeFirst;
		this.elTypeSecond = elTypeSecond;
	}
	
	public PairType(PairType pair){
		this.elTypeFirst = pair.getElTypeFirst();
		this.elTypeSecond = pair.getElTypeSecond();
	}
	
	public Type getElTypeFirst() {
		return elTypeFirst;
	}


	public Type getElTypeSecond() {
		return elTypeSecond;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PairType))
			return false;
		PairType lt = (PairType) obj;
		return (elTypeFirst.equals(lt.elTypeFirst) && elTypeSecond.equals(lt.elTypeSecond));
	}

	@Override
	public int hashCode() {
		return 42 * (elTypeFirst.hashCode() + elTypeSecond.hashCode());
	}
	

	@Override
	public String toString()
	{
		return "("+elTypeFirst+","+elTypeSecond+")";
	}
	

}
