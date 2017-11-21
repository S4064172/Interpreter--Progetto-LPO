package _3_Ast;

import _4_Visitors.Visitor;

public class Eq extends BinaryOp {
	public Eq(Exp left, Exp right) {
		super(left, right);
	}
	
	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitEq(left, right);
	}
	
//	@Override
//	public Type typecheck(GenEnvironment<Type> env) throws TypecheckerException {
//		Type ty=left.typecheck(env);
//		return right.typecheck(env).checkEqual(ty);
//	}
}
