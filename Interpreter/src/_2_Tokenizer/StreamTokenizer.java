package _2_Tokenizer;

import static _2_TokenType.TokenType.*;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import _1_StreamScanner.Scanner;
import _1_StreamScanner.ScannerException;
import _1_StreamScanner.StreamScanner;
import _2_TokenType.TokenType;

public class StreamTokenizer implements Tokenizer {
	private static final String regEx;
	private static final Map<String, TokenType> keywords = new HashMap<>();
	private static final Map<String, TokenType> symbols = new HashMap<>();

	private boolean hasNext = true; // any stream contains at least the EOF token
	private TokenType tokenType;
	private String tokenString;
	private boolean boolValue;
	private int intValue;
	private final Scanner scanner;

	static {
		// remark: groups must correspond to the ordinal of the corresponding
		// token type
/*********/
		// recognizes the identity
		final String identRegEx = "([a-zA-Z][a-zA-Z0-9_]*)"; // group 1		
		// recognizes the octal literal
		final String octRegEx = "(?:0(?:0|[1-7][0-7]*))"; //not a group		
		// recognizes the int literal + octal literal
		final String numRegEx = "("+octRegEx+"|0|[1-9][0-9]*)"; // group 2	
		// recognizes all the symbols of us language
		final String symbolRegEx = "\\|\\||&&|==|\\+|\\*|=|\\(|\\)|;|,|\\{|\\}|<|-|!|\\[|\\]|@|/"; 
/********/
		// recognizes the blank spaces
		final String skipRegEx = "(\\s+|//.*)"; // group 3
		
		// this is us regex
		regEx = identRegEx + "|" + numRegEx + "|" + skipRegEx + "|" + symbolRegEx;
	}

	static {
		keywords.put("in", IN);
		keywords.put("false", BOOL);
		keywords.put("for", FOR);
		keywords.put("print", PRINT);
		keywords.put("pop", POP);
		keywords.put("push", PUSH);
		keywords.put("top", TOP);
		keywords.put("true", BOOL);
		keywords.put("var", VAR);
/*********/
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("pair", PAIR);
		keywords.put("fst", FST);
		keywords.put("snd", SND);
		keywords.put("while", WHILE);
		keywords.put("do", DO);
		keywords.put("length", LENGTH);
		keywords.put("switch", SWITCH);
		keywords.put("case", CASE);
		keywords.put("break", BREAK);
/********/
	}

	static {
		symbols.put("+", PLUS);
		symbols.put("*", TIMES);
		symbols.put("=", ASSIGN);
		symbols.put("(", OPEN_PAR);
		symbols.put(")", CLOSED_PAR);
		symbols.put(";", STMT_SEP);
		symbols.put(",", EXP_SEP);
		symbols.put("{", START_BLOCK);
		symbols.put("}", END_BLOCK);
		symbols.put("||", OR);
		symbols.put("&&", AND);
		symbols.put("==", EQ);
		symbols.put("<", LTH);
		symbols.put("-", MINUS);
		symbols.put("!", NOT);
		symbols.put("[", START_LIST);
		symbols.put("]", END_LIST);
/*******/
		symbols.put("@", CONCAT);
		symbols.put("/", DIV);
/*******/
	}

	public StreamTokenizer(Reader reader) {
		scanner = new StreamScanner(regEx, reader);
	}

	private void checkType() 
	{
		tokenString = scanner.group();
		// IDENT or BOOL or a keyword
		if (scanner.group(IDENT.ordinal()) != null)
		{ 
			tokenType = keywords.get(tokenString);
			if (tokenType == null)
				tokenType = IDENT;
			if (tokenType == BOOL)
				boolValue = Boolean.parseBoolean(tokenString);
			return;
		}
		//literal numeric (octal or int)
		if (scanner.group(NUM.ordinal()) != null) 
		{
			tokenType = NUM;
/******/
			if(tokenString.charAt(0)=='0') // oct
				intValue = Integer.parseInt(tokenString,8);
/******/
			else //num
				intValue = Integer.parseInt(tokenString);
			return;
		}
		// blank spaces (SKIP)
		if (scanner.group(SKIP.ordinal()) != null) 
		{
			tokenType = SKIP;
			return;
		}
		// Symbol
		tokenType = symbols.get(tokenString); 
		if (tokenType == null)
			throw new AssertionError("Fatal error");
	}

	@Override
	public TokenType next() throws IOException, ScannerException {
		do {
			tokenType = null;
			tokenString = "";
			if (hasNext && !scanner.hasNext()) {
				hasNext = false;
				return tokenType = EOF;
			}
			scanner.next();
			checkType();
		} while (tokenType == SKIP);
		return tokenType;
	}

	private void checkValidToken() {
		if (tokenType == null)
			throw new IllegalStateException();
	}

	private void checkValidToken(TokenType ttype) {
		if (tokenType != ttype)
			throw new IllegalStateException("founded "+tokenType+" expected "+ttype);
	}

	@Override
	public String tokenString() {
		checkValidToken();
		return tokenString;
	}

	@Override
	public int intValue() {
		checkValidToken(NUM);
		return intValue;
	}

	@Override
	public boolean boolValue() {
		checkValidToken(BOOL);
		return boolValue;
	}

	@Override
	public TokenType tokenType() {
		checkValidToken();
		return tokenType;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public void close() throws IOException {
		scanner.close();
	}
}
