package _4_Visitors.typechecking;

import static _4_Visitors.typechecking.PrimtType.*;

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

public class TypeCheck implements Visitor<Type> {

	private final GenEnvironment<Type> env = new GenEnvironment<>();

	private void checkBinOp(Exp left, Exp right, Type type) {
		left.accept(this).checkEqual(type);
		right.accept(this).checkEqual(type);
	}

	@Override
	public Type visitAdd(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public Type visitAnd(Exp left, Exp right) {
		checkBinOp(left, right, BOOL);
		return BOOL;
	}

	@Override
	public Type visitAssignStmt(Ident ident, Exp exp) {
		Type expected = env.lookup(ident);
		exp.accept(this).checkEqual(expected);
		return null;
	}

	@Override
	public Type visitBoolLiteral(boolean value) {
		return BOOL;
	}

	@Override
	public Type visitEq(Exp left, Exp right) {
		Type expected = left.accept(this);
		right.accept(this).checkEqual(expected);
		return BOOL;
	}

	@Override
	public Type visitForEachStmt(Ident ident, Exp exp, StmtSeq block) {
		Type ty = exp.accept(this).checkList();
		env.enterScope();
		env.newFresh(ident, ty);
		block.accept(this);
		env.exitScope();
		return null;
	}

	@Override
	public Type visitIntLiteral(int value) {
		return INT;
	}

	@Override
	public Type visitListLiteral(ExpSeq exps) {
		return new ListType(exps.accept(this));
	}

	@Override
	public Type visitLth(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return BOOL;
	}

	@Override
	public Type visitMoreExp(Exp first, ExpSeq rest) {
		Type expected = first.accept(this);
		return rest.accept(this).checkEqual(expected);
	}

	@Override
	public Type visitMoreStmt(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	@Override
	public Type visitMul(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public Type visitNot(Exp exp) {
		return exp.accept(this).checkEqual(BOOL);
	}

	@Override
	public Type visitOr(Exp left, Exp right) {
		checkBinOp(left, right, BOOL);
		return BOOL;
	}

	@Override
	public Type visitPop(Exp exp) {
		Type type = exp.accept(this);
		type.checkList();
		return type;
	}

	@Override
	public Type visitPrintStmt(Exp exp) {
		exp.accept(this);
		return null;
	}

	@Override
	public Type visitProg(StmtSeq stmtSeq) {
		stmtSeq.accept(this);
		return null;
	}

	@Override
	public Type visitPush(Exp left, Exp right) {
		Type elType = left.accept(this);
		return right.accept(this).checkEqual(new ListType(elType));
	}

	@Override
	public Type visitSign(Exp exp) {
		return exp.accept(this).checkEqual(INT);
	}

	@Override
	public Type visitIdent(String name) {
		return env.lookup(new SimpleIdent(name));
	}

	@Override
	public Type visitSingleExp(Exp exp) {
		return exp.accept(this);
	}

	@Override
	public Type visitSingleStmt(Stmt stmt) {
		stmt.accept(this);
		return null;
	}

	@Override
	public Type visitTop(Exp exp) {
		return exp.accept(this).checkList();
	}

	@Override
	public Type visitVarStmt(Ident ident, Exp exp) {
		env.newFresh(ident, exp.accept(this));
		return null;
	}
	
/************/
	@Override
	public Type visitWhileStmt(Exp exp, StmtSeq block) {
		exp.accept(this).checkEqual(BOOL);
		env.enterScope();
		block.accept(this);
		env.exitScope();
		return null;
	}
	
	@Override
	public Type visitSwitchStmt(Exp exp, HashMap<Integer, List<CaseStmt>> block) {
		exp.accept(this).checkEqual(INT);
		for (Integer iterable_element : block.keySet()) {
			if( block.get(iterable_element).size() > 1)
				throw new TypecheckerException("Duplicate key Case");
			block.get(iterable_element).get(0).accept(this);
		}
		return null;
	}

	@Override
	public Type visitCaseStmt(IntLiteral key, StmtSeq block) {
		key.accept(this).checkEqual(INT);
		env.enterScope();
		block.accept(this);
		env.exitScope();
		return null;
	}
	
	
	@Override
	public Type visitIfStmt(Exp exp, StmtSeq ifBlock, StmtSeq elseBlock) {
		exp.accept(this).checkEqual(BOOL);
		env.enterScope();
		ifBlock.accept(this);
		env.exitScope();
		if(elseBlock != null)
		{	
			env.enterScope();
			elseBlock.accept(this);
			env.exitScope();
		}
		return null;
	}
	
	@Override
	public Type visitConCat(Exp left, Exp right) {
		Type type = left.accept(this);
		type.checkList();
		return right.accept(this).checkEqual(type);
	}
	
	public Type visitDiv(Exp left,Exp right){
		checkBinOp(left, right, INT);
		return INT;
	}

	
	@Override
	public Type vistSub(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}
	
	@Override
	public Type visitLength(Exp exp) {
		exp.accept(this).checkList();
		return INT;
	}

	@Override
	public Type visitPair(Exp first, Exp second) {
		return new PairType(first.accept(this), second.accept(this));
	}

	@Override
	public Type visitFst(Exp exp) {
		return ((PairType) exp.accept(this).checkPair()).getElTypeFirst();
	}

	@Override
	public Type visitSnd(Exp exp) {
		return ((PairType) exp.accept(this).checkPair()).getElTypeSecond();
	}
	
/************/

}
