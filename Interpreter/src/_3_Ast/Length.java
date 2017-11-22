package _3_Ast;

import _4_Visitors.Visitor;

public class Length extends UnaryOp {

	public Length(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitLength(exp);
	}
}
