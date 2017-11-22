package _3_Ast;

import _4_Visitors.Visitor;

public class ConCat extends BinaryOp {

	public ConCat(Exp left, Exp right){
		super(left, right);
		
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitConCat(left, right);
	}

}
