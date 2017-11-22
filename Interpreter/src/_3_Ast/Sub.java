package _3_Ast;

import _4_Visitors.Visitor;

public class Sub extends BinaryOp {

	public Sub(Exp left, Exp right) {
		super(left, right);
	
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.vistSub(left,right);
	}

}
