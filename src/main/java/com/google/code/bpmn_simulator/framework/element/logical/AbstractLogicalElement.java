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
package com.google.code.bpmn_simulator.framework.element.logical;

import java.util.HashSet;
import java.util.Set;

import com.google.code.bpmn_simulator.framework.token.Token;
import com.google.code.bpmn_simulator.framework.token.TokenListener;

public abstract class AbstractLogicalElement
		implements LogicalElement {

	private final Set<TokenListener> tokenListeners =
			new HashSet<TokenListener>();

	public AbstractLogicalElement() {
		super();
	}

	@Override
	public void addTokenListener(final TokenListener listener) {
		synchronized (tokenListeners) {
			tokenListeners.add(listener);
		}
	}

	@Override
	public void removeTokenListener(final TokenListener listener) {
		synchronized (tokenListeners) {
			tokenListeners.remove(listener);
		}
	}

	protected void notifyTokenAdded(final Token token) {
		synchronized (tokenListeners) {
			for (final TokenListener listener : tokenListeners) {
				listener.tokenAdded(token);
			}
		}
	}

	protected void notifyTokenRemoved(final Token token) {
		synchronized (tokenListeners) {
			for (final TokenListener listener : tokenListeners) {
				listener.tokenRemoved(token);
			}
		}
	}

}
