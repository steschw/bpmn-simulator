/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.animation.token;

import java.util.HashSet;
import java.util.Set;

public final class Instance
		extends AbstractInstancesContainer {

	private final Instance parent;

	private final Tokens tokens = new Tokens();

	private final Set<TokensListener> tokensListeners = new HashSet<>();

	private int color;

	private Instance(final Instance parent, final int color) {
		super();
		this.parent = parent;
		this.color = color;
	}

	protected Instance(final int color) {
		this(null, color);
	}

	public int getColor() {
		return color;
	}

	public void addTokensListener(final TokensListener listener) {
		synchronized (tokensListeners) {
			tokensListeners.add(listener);
		}
	}

	public void removeTokensListener(final TokensListener listener) {
		synchronized (tokensListeners) {
			tokensListeners.remove(listener);
		}
	}

	private void notifyTokenAdded(final Token token) {
		synchronized (tokensListeners) {
			for (final TokensListener listener : tokensListeners) {
				listener.tokenAdded(token);
			}
		}
	}

	private void notifyTokenRemoved(final Token token) {
		synchronized (tokensListeners) {
			for (final TokensListener listener : tokensListeners) {
				listener.tokenRemoved(token);
			}
		}
	}

	public Instance getParentInstance() {
		return parent;
	}

	public Instance getTopLevelInstance() {
		final Instance parent = getParentInstance();
		if (parent == null) {
			return this;
		} else {
			return parent.getTopLevelInstance();
		}
	}

	@Override
	protected Instance createNewChildInstance() {
		return new Instance(this, color);
	}

	public Tokens getTokens(final boolean recursive) {
		final Tokens allTokens = new Tokens();
		allTokens.addAll(tokens);
		if (recursive) {
			for (final Instance childInstance : this) {
				allTokens.addAll(childInstance.getTokens(recursive));
			}
		}
		return allTokens;
	}

	private void addToken(final Token token) {
		tokens.add(token);
		notifyTokenAdded(token);
	}

	public Token createNewToken(final TokenFlow tokenFlow) {
		final Token token = new Token(this, tokenFlow);
		addToken(token);
		return token;
	}

	public void removeToken(final Token token) {
		assert tokens.contains(token);
		tokens.remove(token);
		notifyTokenRemoved(token);
	}

}
