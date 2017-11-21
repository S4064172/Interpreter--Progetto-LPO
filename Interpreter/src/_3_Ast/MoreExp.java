package _3_Ast;

import _4_Visitors.Visitor;

public class MoreExp extends More<Exp, ExpSeq> implements ExpSeq {

	public MoreExp(Exp first, ExpSeq rest) {
		super(first, rest);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitMoreExp(first, rest);
	}

	// @Override
	// public Type typecheck(GenEnvironment<Type> env) throws
	// TypecheckerException {
	// Type expected = first.typecheck(env);
	// return rest.typecheck(env).checkEqual(expected);
	// }

}
