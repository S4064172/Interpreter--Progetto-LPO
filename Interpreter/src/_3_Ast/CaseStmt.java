package _3_Ast;

import static java.util.Objects.requireNonNull;

import _4_Visitors.Visitor;

public class CaseStmt implements Stmt {

	private final IntLiteral exp;
	private final StmtSeq block;
	
	public CaseStmt(IntLiteral exp, StmtSeq block) {
		this.exp = requireNonNull(exp);
		this.block = requireNonNull(block);
	}


	public IntLiteral getExp() {
		return exp;
	}

	public StmtSeq getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + exp + "," + block + ")";
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return null;
	}

}
