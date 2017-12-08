package _3_Ast;

import static java.util.Objects.requireNonNull;


import java.util.HashMap;
import java.util.List;

import _1_StreamScanner.ScannerException;
import _4_Visitors.Visitor;

public class SwitchStmt implements Stmt {

	private final Exp exp;
	private final HashMap<Exp, List<CaseStmt>> block;
	
	public SwitchStmt(Exp exp, HashMap<Exp,List<CaseStmt>> block) throws ScannerException {
		this.exp = requireNonNull(exp);
		requireNonNull(block);
		if(block.size()==0)
			throw new ScannerException("Any case founded");
		this.block = new HashMap<>(block);
	}


	public Exp getExp() {
		return exp;
	}

	public HashMap<Exp, List<CaseStmt>> getBlock() {
		return block;
	}

	@Override
	public String toString() {
		String result="";
		
		for (Exp iterable_element : block.keySet()) {
			for (CaseStmt signleCase : block.get(iterable_element)) {
				result+=signleCase.toString();	
			}		
		}
		return getClass().getSimpleName() + "(" + exp + "," + result + ")";
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitSwitchStmt(exp, block);
	}

}