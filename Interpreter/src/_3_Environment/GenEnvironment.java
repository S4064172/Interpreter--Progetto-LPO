package _3_Environment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import _3_Ast.Ident;
import _4_Visitors.typechecking.TypecheckerException;

import static java.util.Objects.requireNonNull;

public class GenEnvironment<T> implements Environment<T> {

	private List<Map<Ident, T>> localEnvs = new LinkedList<>();

	/* create an environment with just one empty scope */
	public GenEnvironment() {
		enter();
	}

	/*
	 * enter a new nested scope; private method shared by enterScope() and the
	 * constructor
	 */
	private void enter() {
		localEnvs.add(0, new HashMap<>());
	}

	@Override
	public void enterScope() {
		enter();
	}

	/* removes (pops) the more nested scope */
	@Override
	public void exitScope() {
		localEnvs.remove(0);
	}

	/*
	 * updates map to associate id with val; id and val must be non-null
	 */
	private static <T> T put(Map<Ident, T> map, Ident id, T val) {
		return map.put(requireNonNull(id), requireNonNull(val));
	}

	/*
	 * looks up the inner-most scope defining id; throws TypecheckerException if
	 * no scope could be found
	 */
	protected Map<Ident, T> resolve(Ident id) throws TypecheckerException {
		for (Map<Ident, T> locEnv : localEnvs)
			if (locEnv.containsKey(id))
				return locEnv;
		throw new TypecheckerException("Undeclared variable " + id.getName());
	}

	/*
	 * updates the inner-most scope which defines id by associating with it the
	 * new value val; throws TypecheckerException if no scope could be found
	 */
	@Override
	public T update(Ident id, T val) throws TypecheckerException {
		return put(resolve(id), id, val);
	}

	/*
	 * updates the inner-most scope by associating id with val; id and val must
	 * be non-null; id is allowed to be already defined in the inner-most scope
	 */
	@Override
	public T newFresh(Ident id, T val) throws TypecheckerException {
		Map<Ident, T> locEnv = localEnvs.get(0);
		return put(locEnv, id, val);
	}

	/*
	 * lookups the value associated with id in the inner-most scope which
	 * defines it; throws TypecheckerException if no scope could be found
	 */
	@Override
	public T lookup(Ident id) throws TypecheckerException {
		return resolve(id).get(id);
	}
}
