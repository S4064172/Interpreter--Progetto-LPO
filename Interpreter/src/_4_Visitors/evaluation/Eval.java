package _4_Visitors.evaluation;

import java.util.HashMap;
import java.util.List;
import _3_Ast.CaseStmt;
import _3_Ast.Exp;
import _3_Ast.ExpSeq;
import _3_Ast.Ident;
import _3_Ast.IntLiteral;
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
	public Value visitWhileStmt(Exp exp, StmtSeq block) {
		
		while (exp.accept(this).asBool())
		{
			env.enterScope();
			block.accept(this);
			env.exitScope();
		}
		return null;
	}
	
	@Override
	public Value visitSwitchStmt(Exp exp, HashMap<Integer, List<CaseStmt>> block) {
		Integer key = exp.accept(this).asInt();
		if(block.containsKey(key))
		{
			block.get(key).get(0).accept(this);
		}
		return null;
	}

	@Override
	public Value visitCaseStmt(IntLiteral key, StmtSeq block) {
		env.enterScope();
		block.accept(this);
		env.exitScope();
		return null;
	}
	
	@Override
	public Value visitIfStmt(Exp exp, StmtSeq ifBlock, StmtSeq elseBlock) {
		if(exp.accept(this).asBool()){
			env.enterScope();
			ifBlock.accept(this);
			env.exitScope();
		}
		else{
			if (elseBlock==null)
				return null;
			env.enterScope();
			elseBlock.accept(this);
			env.exitScope();
		}
		return null;
	}
	
	@Override
	public Value visitConCat(Exp left, Exp right) {
		ListValue result = new LinkedListValue(left.accept(this).asList());
		for (Value value : right.accept(this).asList())
			result = result.push(value);
		return result;
	}

	@Override
	public Value visitDiv(Exp left, Exp right) {
		int temp = right.accept(this).asInt();
		if (temp==0)
			throw new EvaluatorException("cannot divide by 0");
		return  new IntValue(left.accept(this).asInt()/temp);
	}


	@Override
	public Value vistSub(Exp left, Exp right) {
		
		return new IntValue(left.accept(this).asInt()-right.accept(this).asInt());
	}
	
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
