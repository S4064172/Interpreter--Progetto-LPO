package _3_Ast;

import _4_Visitors.Visitor;

public interface ASTNode {
	<T> T accept(Visitor<T> visitor);
}
