package _4_Visitors.evaluation;

public interface ListValue extends Value, Iterable<Value> {
	ListValue push(Value el);

	Value top();

	ListValue pop();

	@Override
	default ListValue asList() {
		return this;
	}
}
