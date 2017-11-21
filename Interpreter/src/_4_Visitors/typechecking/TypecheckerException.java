package _4_Visitors.typechecking;

public class TypecheckerException extends RuntimeException {

	public TypecheckerException() {
	}

	public TypecheckerException(String message) {
		super(message);
	}

	public TypecheckerException(String found, String expected) {
		this("Found " + found + ", expected " + expected);
	}
}
