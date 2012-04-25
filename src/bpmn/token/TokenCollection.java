/*
 * Copyright (C) 2012 Stefan Schweitzer
 *
 * This software was created by Stefan Schweitzer as a student's project at
 * Fachhochschule Kaiserslautern (University of Applied Sciences).
 * Supervisor: Professor Dr. Thomas Allweyer. For more information please see
 * http://www.fh-kl.de/~allweyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bpmn.token;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class TokenCollection extends Vector<Token> {

	private static final long serialVersionUID = 1L;

	public TokenCollection() {
		super();
	}

	public TokenCollection(final Collection<? extends Token> c) {
		super(c);
	}

	@Override
	public synchronized boolean add(final Token token) {
		assert !contains(token);
		return super.add(token);
	}

	public synchronized boolean remove(final Token token) {
//		assert(contains(token));
		return super.remove(token);
	}

	public synchronized void moveTo(final TokenCollection collection,
			final Token token) {
		remove(token);
		collection.add(token);
	}

	public int getCount() {
		return size();
	}

	public synchronized Token merge() {
		final Iterator<Token> i = iterator();
		if (i.hasNext()) {
			final Token mergedToken = i.next();
			while (i.hasNext()) {
				mergedToken.merge(i.next());
			}
			return mergedToken;
		}
		return null;
	}

	public synchronized Collection<Instance> getInstances() {
		final Collection<Instance> instances = new Vector<Instance>();
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
