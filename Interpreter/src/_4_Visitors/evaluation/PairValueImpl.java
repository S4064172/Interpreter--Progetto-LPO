package _4_Visitors.evaluation;


public class PairValueImpl implements PairValue{
	
	private final Value elTypeFirst;
	private final Value elTypeSecond;

	public PairValueImpl(Value elTypeFirst, Value elTypeSecond) {
		this.elTypeFirst = elTypeFirst;
		this.elTypeSecond = elTypeSecond;
	}
	
	public PairValueImpl(PairValueImpl pair) {
		this.elTypeFirst = pair.first();
		this.elTypeSecond = pair.second();
	}

		
	@Override
	public String toString() {
		return "("+elTypeFirst.toString()+","+elTypeSecond.toString()+")";
	}

	@Override
	public int hashCode() {
		return 42 * (elTypeFirst.hashCode() + elTypeSecond.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		return elTypeFirst.equals(((PairValueImpl) obj).elTypeFirst) && elTypeSecond.equals(((PairValueImpl)obj).elTypeSecond);
	}

	@Override
	public Value first() {
		return elTypeFirst;
	}

	@Override
	public Value second() {
		return elTypeSecond;
	}

}
