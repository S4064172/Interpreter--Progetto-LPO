package _3_Ast;

import _4_Visitors.Visitor;

public class IntLiteral extends PrimLiteral<Integer> {

	public IntLiteral(int n) {
		super(n);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIntLiteral(value);
	}
	
//	@Override
//	public Type typecheck(GenEnvironment<Type> env) {
//		return INT;
//	}

}
