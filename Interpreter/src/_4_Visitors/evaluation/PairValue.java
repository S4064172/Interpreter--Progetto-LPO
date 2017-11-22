package _4_Visitors.evaluation;

public interface PairValue extends Value {
	
	
	Value first();
	
	Value second();
	
	@Override
	default PairValue asPair(){
		return this;
	}

}
