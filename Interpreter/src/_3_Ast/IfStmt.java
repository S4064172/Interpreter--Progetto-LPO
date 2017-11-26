package _3_Ast;

import _4_Visitors.Visitor;
import static java.util.Objects.requireNonNull;

public class IfStmt implements Stmt {

	private final Exp exp;
	private final StmtSeq ifBlock;
	private final StmtSeq elseBlock;
	
	
	public IfStmt(Exp exp, StmtSeq ifBlock, StmtSeq elseBlock) {
		this.exp = requireNonNull(exp);
		this.ifBlock = requireNonNull(ifBlock);
		this.elseBlock = elseBlock;
	}


	public IfStmt(Exp exp, StmtSeq ifBlock) {
		this(exp,ifBlock,null);
	}


	public Exp getExp() {
		return exp;
	}

	public StmtSeq getifBlock() {
		return ifBlock;
	}

	public StmtSeq getelseBlock() {
		return elseBlock;
	}

	@Override
	public String toString(){
		return getClass().getSimpleName() + "("+exp+","+ifBlock+ (elseBlock==null? ")":","+elseBlock)+")" ; 
	}
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIfStmt(exp,ifBlock,elseBlock);
	}

}
