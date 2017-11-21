package _2_StreamParser;

import static _2_TokenType.TokenType.*;

import java.io.IOException;

import _1_StreamScanner.ScannerException;
import _2_TokenType.TokenType;
import _2_Tokenizer.Tokenizer;
import _3_Ast.*;

/*
 * Grammatica Progetto :
 *	Prog ::= StmtSeq 'EOF'											(V)
 *	StmtSeq ::= Stmt (';' StmtSeq)?									(V)
 * 	Stmt ::= 'var'? ID '=' Exp | 									(V)
 * 			 'print' Exp |  										(V)
 * 			 'for' ID 'in' Exp '{' StmtSeq '}' |					(V)
 * 			 'if' '('Exp')' '{' StmtSeq '}' ('else' '{'StmtSeq'}')?	(X) 
 * 			 'while' '('Exp')' '{'StmtSeq'}'						(X)
 * 	ExpSeq ::= Exp (',' ExpSeq)?									(V)			
 * 	Exp ::=  And ('||' And)* 										(V)
 * 	And ::= Eq ('&&' Eq)*											(V)
 * 	Eq ::= Lth ('==' Lth)*											(V)
 * 	Lth ::= ConCat ('<' ConCat)*									(X)
 * 	ConCat ::= PlusOrSub ('@' PlusOrSub)*							(X) 
 * 	PlusOrSub ::= TimesOrDiv ( ('+'|'-') TimesOrDiv)*				(X)
 * 	TimesOrDiv ::= Atom ( ('*'|'/') Atom)*							(X)
 * 	Atom ::= '!' Atom 												(V)
 * 			 '-' Atom | 											(V)
 * 			 'top' Atom | 											(V)
 * 			 'pop' Atom | 											(V)
 * 			 'push' '('Exp,Exp')' | 								(V)
 * 			 '[' ExpSeq ']' | 										(V)
 * 			 NUM | 													(V)
 * 			 BOOL | 												(V)
 * 			 ID | 													(V)
 * 			 '(' Exp ')' |											(V)
 * 			 'length' Atom 											(X)
 * 			 'pair' '('Exp','Exp')'|								(X)
 * 			 'fst' Atom |											(X)
 * 			 'snd' Atom												(X)
 * 
 */

public class StreamParser implements Parser {

	private final Tokenizer tokenizer;

	public StreamParser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	@Override
	public Prog parseProg() throws IOException, ScannerException, ParserException {
		tokenizer.next(); // one look-ahead symbol
		Prog prog = new ProgClass(parseStmtSeq());
		match(EOF);
		return prog;
	}

	private StmtSeq parseStmtSeq() throws IOException, ScannerException, ParserException {
		Stmt stmt = parseStmt();
		if (tokenizer.tokenType() == STMT_SEP) {
			tokenizer.next();
			return new MoreStmt(stmt, parseStmtSeq());
		}
		return new SingleStmt(stmt);
	}

	private ExpSeq parseExpSeq() throws IOException, ScannerException, ParserException {
		Exp exp = parseExp();
		if (tokenizer.tokenType() == EXP_SEP) {
			tokenizer.next();
			return new MoreExp(exp, parseExpSeq());
		}
		return new SingleExp(exp);
	}

	private Stmt parseStmt() throws IOException, ScannerException, ParserException {
		switch (tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case PRINT:
			return parsePrintStmt();
		case VAR:
			return parseVarStmt();
		case IDENT:
			return parseAssignStmt();
		case FOR:
			return parseForEachStmt();
		}
	}

	private PrintStmt parsePrintStmt() throws IOException, ScannerException, ParserException {
		consume(PRINT);
		return new PrintStmt(parseExp());
	}

	private VarStmt parseVarStmt() throws IOException, ScannerException, ParserException {
		consume(VAR);
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new VarStmt(ident, parseExp());
	}

	private AssignStmt parseAssignStmt() throws IOException, ScannerException, ParserException {
		Ident ident = parseIdent();
		consume(ASSIGN);
		return new AssignStmt(ident, parseExp());
	}

	private ForEachStmt parseForEachStmt() throws IOException, ScannerException, ParserException {
		consume(FOR);
		Ident ident = parseIdent();
		consume(IN);
		Exp exp = parseExp();
		consume(START_BLOCK);
		StmtSeq stmts = parseStmtSeq();
		consume(END_BLOCK);
		return new ForEachStmt(ident, exp, stmts);
	}

	private Exp parseExp() throws IOException, ScannerException, ParserException {
		Exp exp = parseAnd();
		while (tokenizer.tokenType() == OR) {
			tokenizer.next();
			exp = new Or(exp, parseAnd());
		}
		return exp;
	}

	private Exp parseAnd() throws IOException, ScannerException, ParserException {
		Exp exp = parseEq();
		while (tokenizer.tokenType() == AND) {
			tokenizer.next();
			exp = new And(exp, parseEq());
		}
		return exp;
	}

	private Exp parseEq() throws IOException, ScannerException, ParserException {
		Exp exp = parseLth();
		while (tokenizer.tokenType() == EQ) {
			tokenizer.next();
			exp = new Eq(exp, parseLth());
		}
		return exp;
	}

	private Exp parseLth() throws IOException, ScannerException, ParserException {
		Exp exp = parseAdd();
		while (tokenizer.tokenType() == LTH) {
			tokenizer.next();
			exp = new Lth(exp, parseAdd());
		}
		return exp;
	}

	private Exp parseAdd() throws IOException, ScannerException, ParserException {
		Exp exp = parseTimes();
		while (tokenizer.tokenType() == PLUS) {
			tokenizer.next();
			exp = new Add(exp, parseTimes());
		}
		return exp;
	}

	private Exp parseTimes() throws IOException, ScannerException, ParserException {
		Exp exp = parseAtom();
		while (tokenizer.tokenType() == TIMES) {
			tokenizer.next();
			exp = new Mul(exp, parseAtom());
		}
		return exp;
	}

	private Exp parseAtom() throws IOException, ScannerException, ParserException {
		switch (tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case NUM:
			return parseNum();
		case BOOL:
			return parseBool();
		case IDENT:
			return parseIdent();
		case NOT:
			return parseNot();
		case MINUS:
			return parseMinus();
		case POP:
			return parsePop();
		case TOP:
			return parseTop();
		case PUSH:
			return parsePushStmt();
		case START_LIST:
			return parseList();
		case OPEN_PAR:
			tokenizer.next();
			Exp exp = parseExp();
			consume(CLOSED_PAR);
			return exp;
		}
	}

	private IntLiteral parseNum() throws IOException, ScannerException, ParserException {
		int val = tokenizer.intValue();
		consume(NUM);
		return new IntLiteral(val);
	}

	private PrimLiteral<Boolean> parseBool() throws IOException, ScannerException, ParserException {
		boolean val = tokenizer.boolValue();
		consume(BOOL);
		return new BoolLiteral(val);
	}

	private Ident parseIdent() throws IOException, ScannerException, ParserException {
		String name = tokenizer.tokenString();
		consume(IDENT);
		return new SimpleIdent(name);
	}

	private Not parseNot() throws IOException, ScannerException, ParserException {
		consume(NOT);
		return new Not(parseAtom());
	}

	private Sign parseMinus() throws IOException, ScannerException, ParserException {
		consume(MINUS);
		return new Sign(parseAtom());
	}

	private Top parseTop() throws IOException, ScannerException, ParserException {
		consume(TOP);
		Exp exp = parseAtom();
		return new Top(exp);
	}

	private Pop parsePop() throws IOException, ScannerException, ParserException {
		consume(POP);
		Exp exp = parseAtom();
		return new Pop(exp);
	}

	private Push parsePushStmt() throws IOException, ScannerException, ParserException {
		consume(PUSH);
		consume(OPEN_PAR);
		Exp left = parseExp();
		consume(EXP_SEP);
		Exp right = parseExp();
		consume(CLOSED_PAR);
		return new Push(left, right);
	}

	private ListLiteral parseList() throws IOException, ScannerException, ParserException {
		consume(START_LIST);
		ExpSeq exps = parseExpSeq();
		consume(END_LIST);
		return new ListLiteral(exps);
	}

	private void unexpectedTokenError() throws ParserException {
		throw new ParserException("Unexpected token " + tokenizer.tokenType() + "('" + tokenizer.tokenString() + "')");
	}

	private void match(TokenType expected) throws ParserException {
		final TokenType found = tokenizer.tokenType();
		if (found != expected)
			throw new ParserException(
					"Expecting " + expected + ", found " + found + "('" + tokenizer.tokenString() + "')");
	}

	private void consume(TokenType expected) throws IOException, ScannerException, ParserException {
		match(expected);
		tokenizer.next();
	}
}
