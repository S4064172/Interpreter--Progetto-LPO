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
 * 			 'if' '('Exp')' '{' StmtSeq '}' ('else' '{'StmtSeq'}')?	(V) 
 * 			 'while' '('Exp')' '{'StmtSeq'}'						(V)
 * 	ExpSeq ::= Exp (',' ExpSeq)?									(V)			
 * 	Exp ::=  And ('||' And)* 										(V)
 * 	And ::= Eq ('&&' Eq)*											(V)
 * 	Eq ::= Lth ('==' Lth)*											(V)
 * 	Lth ::= ConCat ('<' ConCat)*									(V)
 * 	ConCat ::= PlusOrSub ('@' PlusOrSub)*							(V) 
 * 	PlusOrSub ::= TimesOrDiv ( ('+'|'-') TimesOrDiv)*				(V)
 * 	TimesOrDiv ::= Atom ( ('*'|'/') Atom)*							(V)
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
 * 			 'length' Atom 											(V)
 * 			 'pair' '('Exp','Exp')'|								(V)
 * 			 'fst' Atom |											(V)
 * 			 'snd' Atom												(V)
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
/*******/
		case WHILE:
			return parseWhileStmt();
		case IF:
			return parseIfStmt();
/******/
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
/**********/
	private WhileStmt parseWhileStmt() throws IOException, ScannerException, ParserException {
		consume(WHILE);
		Exp exp = parseExp();
		consume(START_BLOCK);
		StmtSeq stmts = parseStmtSeq();
		consume(END_BLOCK);
		return new WhileStmt(exp, stmts);
	}
	
	private IfStmt parseIfStmt() throws IOException, ScannerException, ParserException {
		consume(IF);
		Exp exp = parseExp();
		consume(START_BLOCK);
		StmtSeq ifStmts = parseStmtSeq();
		consume(END_BLOCK);
		StmtSeq esleStmts = null;
		if (tokenizer.tokenType() == ELSE)
		{
			tokenizer.next();
			consume(START_BLOCK);
			esleStmts = parseStmtSeq();
			consume(END_BLOCK);
		}
		return new IfStmt(exp, ifStmts,esleStmts);
	}
/**********/
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
	
/************/
	private Exp parseLth() throws IOException, ScannerException, ParserException {

		Exp exp = parseConCat();
		while (tokenizer.tokenType() == LTH) {
			tokenizer.next();
			exp = new Lth(exp, parseConCat());
		}
		return exp;
	}
	

	private Exp parseConCat() throws IOException, ScannerException, ParserException {
		Exp exp = parseAddOrSub();
		while (tokenizer.tokenType() == CONCAT) {
			tokenizer.next();
			exp = new ConCat(exp, parseAddOrSub());
		}
		return exp;
	}

	
	private Exp parseAddOrSub() throws IOException, ScannerException, ParserException {

		Exp exp = parseTimesOrDiv();

		while (tokenizer.tokenType() == PLUS || tokenizer.tokenType() == MINUS) {
			TokenType op = tokenizer.tokenType(); 
			tokenizer.next();
			exp = (op == PLUS ? new Add(exp, parseTimesOrDiv()) : new Sub(exp, parseTimesOrDiv()));

		}
		return exp;
	}


	private Exp parseTimesOrDiv() throws IOException, ScannerException, ParserException {

		Exp exp = parseAtom();

		while (tokenizer.tokenType() == TIMES || tokenizer.tokenType() == DIV) {
			TokenType op = tokenizer.tokenType(); 
			tokenizer.next();
			exp = ( op == TIMES ? new Mul(exp, parseAtom() ) : (new Div(exp, parseAtom()) ) );

		}
		return exp;
	}
/************/
		
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
/******/
			case LENGTH:
				return parseLength();
			case PAIR:
				return parsePair();
			case FST:
				return parseFst();
			case SND:
				return parseSnd();
/*****/
		}
	}
	
/*******/
	private Length parseLength()  throws IOException, ScannerException, ParserException {
		consume(LENGTH);
		Exp exp = parseAtom();
		return new Length(exp);
	}
	
	private Pair parsePair()  throws IOException, ScannerException, ParserException {
		consume(PAIR);
		consume(OPEN_PAR);
		Exp firs = parseExp();
		consume(EXP_SEP);
		Exp second = parseExp();
		consume(CLOSED_PAR);
		return new Pair(firs, second);
	}
	
	
	private Fst parseFst() throws IOException, ScannerException, ParserException	{
		consume(FST);
		Exp exp = parseAtom();
		return new Fst(exp);	
	}
	
	private Snd parseSnd() throws IOException, ScannerException, ParserException	{
		consume(SND);
		Exp exp = parseAtom();
		return new Snd(exp);	
	}
/******/
	
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
