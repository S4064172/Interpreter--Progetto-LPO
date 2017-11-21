package _4_Visitors.evaluation;

import java.util.Iterator;
import java.util.LinkedList;
import static java.util.Objects.requireNonNull;

public class LinkedListValue implements ListValue {

	private final LinkedList<Value> list = new LinkedList<>();

	private void checkIsEmpty() {
		if (list.size() == 0)
			throw new EvaluatorException("Undefined operation on the empty list");
	}

	public LinkedListValue() {
	}

	public LinkedListValue(ListValue otherList) {
		for (Value el : otherList)
			list.add(el);
	}

	public LinkedListValue(Value val, ListValue tail) {
		this(tail);
		list.addFirst(requireNonNull(val));
	}

	@Override
	public Iterator<Value> iterator() {
		return list.iterator();
	}

	@Override
	public ListValue push(Value el) {
		LinkedListValue res = new LinkedListValue(this);
		res.list.addLast(requireNonNull(el));
		return res;
	}

	@Override
	public Value top() {
		checkIsEmpty();
		return list.getLast();
	}

	@Override
	public ListValue pop() {
		checkIsEmpty();
		LinkedListValue res = new LinkedListValue(this);
		res.list.removeLast();
		return res;
	}

	@Override
	public String toString() {
		return list.toString();
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return list.equals(((LinkedListValue) obj).list);
	}
}
