package _3_Ast;

import _4_Visitors.Visitor;

public class Div extends BinaryOp {

	public Div(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitDiv(left,right);
	}
	

}
