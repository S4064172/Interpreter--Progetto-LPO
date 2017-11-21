package _3_Environment;

import _3_Ast.Ident;
import _4_Visitors.typechecking.TypecheckerException;

public interface Environment<T> {

	void enterScope();

	void exitScope();

	T lookup(Ident id) throws TypecheckerException;

	T update(Ident id, T info) throws TypecheckerException;
	
	T newFresh(Ident id, T info) throws TypecheckerException;

}
