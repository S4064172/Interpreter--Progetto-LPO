package _2_Tokenizer;

import java.io.IOException;

import _1_StreamScanner.ScannerException;
import _2_TokenType.TokenType;

public interface Tokenizer extends AutoCloseable {

	TokenType next() throws IOException, ScannerException;

	String tokenString();

	int intValue();

	boolean boolValue();

	TokenType tokenType();

	boolean hasNext();

	public void close() throws IOException;

}