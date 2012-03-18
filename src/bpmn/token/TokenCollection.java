package bpmn.token;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class TokenCollection extends Vector<Token> {

	private static final long serialVersionUID = 1L;

	public TokenCollection() {
		super();
	}

	public TokenCollection(Collection<? extends Token> c) {
		super(c);
	}

	@Override
	public synchronized boolean add(final Token token) {
		assert(!contains(token));
		return super.add(token);
	}

	public synchronized boolean remove(final Token token) {
//		assert(contains(token));
		return super.remove(token);
	}

	public int getCount() {
		return size();
	}

	public synchronized Token merge() {
		Iterator<Token> i = iterator();
		if (i.hasNext()) {
			Token mergedToken = i.next();
			while (i.hasNext()) {
				mergedToken.merge(i.next());
			}
			return mergedToken;
		}
		return null;
	}

	public synchronized Collection<Instance> getInstances() {
		Vector<Instance> instances = new Vector<Instance>();
		for (Token token : this) {
			final Instance instance = token.getInstance(); 
			if (!instances.contains(instance)) {
				instances.add(instance);
			}
		}
		return instances;
	}

	public synchronized TokenCollection byInstance(final Instance instance) {
		final TokenCollection tokens = new TokenCollection();
		for (Token token : this) {
			if (token.getInstance().equals(instance)) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public synchronized TokenCollection byCurrentFlow(final TokenFlow tokenFlow) {
		final TokenCollection tokens = new TokenCollection();
		for (Token token : this) {
			if (tokenFlow.equals(token.getCurrentFlow())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public synchronized TokenCollection byPreviousFlow(final TokenFlow tokenFlow) {
		final TokenCollection tokens = new TokenCollection();
		for (Token token : this) {
			if (tokenFlow.equals(token.getPreviousFlow())) {
				tokens.add(token);
			}
		}
		return tokens;
	}

}
