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
package com.google.code.bpmn_simulator.framework.token;

import java.awt.Color;

import com.google.code.bpmn_simulator.framework.instance.Instance;


public class Token
		implements Cloneable {

	public static final Color HIGHLIGHT_COLOR = new Color(128, 32, 32);

	private Instance instance;

	private TokenFlow previousFlow;

	private TokenFlow currentFlow;

	private int steps;

	public Token(final Instance instance) {
		super();
		assert instance != null;
		setInstance(instance);
	}

	public Token(final Instance instance, final TokenFlow currentTokenFlow) {
		this(instance);
		setCurrentFlow(currentTokenFlow);
	}

	public void setInstance(final Instance instance) {
		if (instance != this.instance) {
			if (this.instance != null) {
				this.instance.removeToken(this);
			}
			this.instance = instance;
			if (this.instance != null) {
				this.instance.addToken(this);
			}
		}
	}

	public Instance getInstance() {
		return instance;
	}

	public void assignTokenFlow(final TokenFlow flow) {
		currentFlow = flow;
		reset();
	}

	protected void setCurrentFlow(final TokenFlow flow) {
		if (flow != currentFlow) {
			if (currentFlow != null) {
				currentFlow.tokenExit(this);
			}
			assignTokenFlow(flow);
			if (currentFlow != null) {
				currentFlow.tokenEnter(this);
			}
		}
	}

	public TokenFlow getCurrentFlow() {
		return currentFlow;
	}

	protected void setPreviousFlow(final TokenFlow flow) {
		previousFlow = flow;
	}

	public TokenFlow getPreviousFlow() {
		return previousFlow;
	}

	public void setSteps(final int steps) {
		assert steps >= 0;
		this.steps = steps;
	}

	public int getSteps() {
		return steps;
	}

	protected void reset() {
		setSteps(0);
	}
/*
	@Override
	public Object clone() throws CloneNotSupportedException {
		final Token token = (Token)super.clone();
//		token.previousFlow = previousFlow;
//		token.currentFlow = currentFlow;
		return token;
	}
*/
	public synchronized void merge(final Token token) {
		assert getInstance() == token.getInstance();
		assert getCurrentFlow() == token.getCurrentFlow();
		token.remove();
	}

	public synchronized void step(final int count) {
		setSteps(getSteps() + count);
		final TokenFlow tokenFlow = getCurrentFlow();
//		assert tokenFlow != null;
		if (tokenFlow != null) {
			tokenFlow.tokenDispatch(this);
		}
	}

	public synchronized void remove() {
		setInstance(null);
		setCurrentFlow(null);
	}

	/**
	 * Gibt eine Kopie des Token an ein anderes Element weiter
	 */
	public synchronized void passTo(final TokenFlow tokenFlow, final Instance instance) {
		assert tokenFlow != null;
		if (tokenFlow != null) {
			final Token passToken = new Token(instance);
			passToken.setPreviousFlow(getCurrentFlow());
			passToken.setCurrentFlow(tokenFlow);
		}
	}

	public synchronized void passTo(final TokenFlow tokenFlow) {
		passTo(tokenFlow, getInstance());
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder('[');
		buffer.append(super.toString());
		buffer.append(", "); //$NON-NLS-1$
		buffer.append(instance);
		buffer.append(", "); //$NON-NLS-1$
		buffer.append(currentFlow);
		buffer.append(", "); //$NON-NLS-1$
		buffer.append(previousFlow);
		buffer.append(']');
		return buffer.toString();
	}

	public boolean hasEndNodeReached() {
		final TokenFlow tokenFlow = getCurrentFlow();
		return tokenFlow.isEndNode() && tokenFlow.isTokenAtEnd(this);
	}

	public void assignToParentInstance() {
		final Instance instance = getInstance();
		final Instance parentInstance = instance.getParent();
		assert parentInstance != null;
		if (parentInstance != null) {
			setInstance(parentInstance);
		}
	}

	public void assignToNewChildInstance() {
		final Instance instance = getInstance();
		final Instance childInstance = instance.newChildInstance(instance.getActivity());
		setInstance(childInstance);
	}

}
