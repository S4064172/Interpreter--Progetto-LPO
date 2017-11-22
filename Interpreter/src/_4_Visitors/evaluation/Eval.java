package _4_Visitors.evaluation;

import _3_Ast.Exp;
import _3_Ast.ExpSeq;
import _3_Ast.Ident;
import _3_Ast.SimpleIdent;
import _3_Ast.Stmt;
import _3_Ast.StmtSeq;
import _3_Environment.GenEnvironment;
import _4_Visitors.Visitor;

public class Eval implements Visitor<Value> {

	private final GenEnvironment<Value> env = new GenEnvironment<>();

	@Override
	public Value visitAdd(Exp left, Exp right) {
		return new IntValue(left.accept(this).asInt() + right.accept(this).asInt());
	}

	@Override
	public Value visitAnd(Exp left, Exp right) {
		return new BoolValue(left.accept(this).asBool() && right.accept(this).asBool());
	}

	@Override
	public Value visitAssignStmt(Ident ident, Exp exp) {
		env.update(ident, exp.accept(this));
		return null;
	}

	@Override
	public Value visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	}

	@Override
	public Value visitEq(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public Value visitForEachStmt(Ident ident, Exp exp, StmtSeq block) {
		ListValue list = exp.accept(this).asList();
		for (Value val : list) {
			env.enterScope();
			env.newFresh(ident, val);
			block.accept(this);
			env.exitScope();
		}
		return null;
	}

	@Override
	public Value visitIntLiteral(int value) {
		return new IntValue(value);
	}

	@Override
	public Value visitListLiteral(ExpSeq exps) {
		return exps.accept(this);
	}

	@Override
	public Value visitLth(Exp left, Exp right) {
		return new BoolValue(left.accept(this).asInt() < right.accept(this).asInt());
	}

	@Override
	public Value visitMoreExp(Exp first, ExpSeq rest) {
		return new LinkedListValue(first.accept(this), rest.accept(this).asList());
	}

	@Override
	public Value visitMoreStmt(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	@Override
	public Value visitMul(Exp left, Exp right) {
		return new IntValue(left.accept(this).asInt() * right.accept(this).asInt());
	}

	@Override
	public Value visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).asBool());
	}

	@Override
	public Value visitOr(Exp left, Exp right) {
		return new BoolValue(left.accept(this).asBool() || right.accept(this).asBool());
	}

	@Override
	public Value visitPop(Exp exp) {
		return exp.accept(this).asList().pop();
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		System.out.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitProg(StmtSeq stmtSeq) {
		stmtSeq.accept(this);
		return null;
	}

	@Override
	public Value visitPush(Exp left, Exp right) {
		Value el = left.accept(this);
		return right.accept(this).asList().push(el);
	}

	@Override
	public Value visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).asInt());
	}

	@Override
	public Value visitIdent(String name) {
		return env.lookup(new SimpleIdent(name));
	}

	@Override
	public Value visitSingleExp(Exp exp) {
		return new LinkedListValue(exp.accept(this), new LinkedListValue());
	}

	@Override
	public Value visitSingleStmt(Stmt stmt) {
		stmt.accept(this);
		return null;
	}

	@Override
	public Value visitTop(Exp exp) {
		return exp.accept(this).asList().top();
	}

	@Override
	public Value visitVarStmt(Ident ident, Exp exp) {
		env.newFresh(ident, exp.accept(this));
		return null;
	}
/************/
	@Override
	public Value visitLength(Exp exp) {
		int count=0;
		for (@SuppressWarnings("unused")Value value : exp.accept(this).asList())
			count++;
		return new IntValue(count);
	}
	
	@Override
	public Value visitPair(Exp first, Exp second) {
		return new PairValueImpl(first.accept(this),second.accept(this));
	}

	@Override
	public Value visitFst(Exp exp) {
		return exp.accept(this).asPair().first();
	}

	@Override
	public Value visitSnd(Exp exp) {
		return exp.accept(this).asPair().second();
	}
	
	
/************/
}
