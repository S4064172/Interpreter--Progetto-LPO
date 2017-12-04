package _3_Ast;

import static java.util.Objects.requireNonNull;

import _4_Visitors.Visitor;

public class CaseStmt implements Stmt {

	private final IntLiteral key;
	private final StmtSeq block;
	
	public CaseStmt(IntLiteral key, StmtSeq block) {
		this.key = requireNonNull(key);
		this.block = requireNonNull(block);
	}


	public IntLiteral getExp() {
		return key;
	}

	public StmtSeq getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + key + "," + block + ")";
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitCaseStmt(key, block);
	}

}
