package _3_Ast;

import _4_Visitors.Visitor;

public class BoolLiteral extends PrimLiteral<Boolean> {

	public BoolLiteral(boolean b) {
		super(b);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitBoolLiteral(value);
	}
	
//	@Override
//	public Type typecheck(GenEnvironment<Type> env) {
//		return BOOL;
//	}

}
