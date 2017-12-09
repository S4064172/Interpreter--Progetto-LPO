package _4_Visitors;


import java.util.HashMap;
import java.util.List;
import _3_Ast.CaseStmt;
import _3_Ast.Exp;
import _3_Ast.ExpSeq;
import _3_Ast.Ident;
import _3_Ast.IntLiteral;
import _3_Ast.Stmt;
import _3_Ast.StmtSeq;

public interface Visitor<T> {
	T visitAdd(Exp left, Exp right);

	T visitAnd(Exp left, Exp right);

	T visitAssignStmt(Ident ident, Exp exp);

	T visitBoolLiteral(boolean value);

	T visitEq(Exp left, Exp right);

	T visitForEachStmt(Ident ident, Exp exp, StmtSeq block);

	T visitIntLiteral(int value);

	T visitListLiteral(ExpSeq exps);

	T visitLth(Exp left, Exp right);

	T visitMoreExp(Exp first, ExpSeq rest);

	T visitMoreStmt(Stmt first, StmtSeq rest);

	T visitMul(Exp left, Exp right);

	T visitNot(Exp exp);

	T visitOr(Exp left, Exp right);

	T visitPop(Exp exp);

	T visitPrintStmt(Exp exp);

	T visitProg(StmtSeq stmtSeq);

	T visitPush(Exp left, Exp right);

	T visitSign(Exp exp);

	T visitIdent(String name);

	T visitSingleExp(Exp exp);

	T visitSingleStmt(Stmt stmt);

	T visitTop(Exp exp);

	T visitVarStmt(Ident ident, Exp exp);
/************/
	T visitWhileStmt(Exp exp, StmtSeq block);
	
	T visitIfStmt(Exp exp, StmtSeq ifBlock, StmtSeq elseBlock);
	
	T visitSwitchStmt(Exp exp, HashMap<Exp, List<CaseStmt>> block);
	
	T visitCaseStmt(Exp key, StmtSeq block);
	
	T visitConCat(Exp left, Exp right);
	
	T visitDiv(Exp left,Exp right);
	
	T vistSub(Exp left, Exp right);
	
	T visitLength(Exp exp);

	T visitPair(Exp first, Exp second);

	T visitFst(Exp exp);

	T visitSnd(Exp exp);

/************/
}
