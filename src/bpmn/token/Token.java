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

import java.awt.Color;

public class Token implements Cloneable {

	public static final Color HIGHLIGHT_COLOR = new Color(128, 32, 32);

	private Instance instance = null;

	private TokenFlow previousFlow = null;

	private TokenFlow currentFlow = null;

	private int steps = 0;

	public Token(final Instance instance) {
		super();
		setInstance(instance);
	}

	public Token(final Instance instance, final TokenFlow currentTokenFlow) {
		this(instance);
		setCurrentFlow(currentTokenFlow);
	}

	protected void setInstance(final Instance instance) {
		assert(instance != null);
		this.instance = instance;
	}

	public Instance getInstance() {
		assert(instance != null);
		return instance;
	}

	protected void setCurrentFlow(final TokenFlow flow) {
		assert(flow != null);
		setPreviousFlow(currentFlow);
		currentFlow = flow;
		reset();
		currentFlow.tokenEnter(this);
	}

	protected TokenFlow getCurrentFlow() {
		return currentFlow;		
	}

	protected void setPreviousFlow(final TokenFlow flow) {
		previousFlow = flow;
	}

	public TokenFlow getPreviousFlow() {
		return previousFlow;
	}

	public void setSteps(final int steps) {
		assert(steps >= 0);
		this.steps = steps;
	}

	public int getSteps() {
		return steps;
	}

	protected void reset() {
		setSteps(0);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		final Token token = new Token(getInstance());
		token.previousFlow = previousFlow;
		token.currentFlow = currentFlow;
		return token;
	}

	public Token copyTo(final Instance instance) {
		final Token token = instance.cloneToken(this);
		token.setInstance(instance);
		return token;
	}

	public synchronized void merge(final Token token) {
		assert(getInstance() == token.getInstance());
		assert(getCurrentFlow() == token.getCurrentFlow());
		token.remove();
	}

	public synchronized void step(final int count) {
		setSteps(getSteps() + count);
		final TokenFlow tokenFlow = getCurrentFlow();
		assert(tokenFlow != null);
		if (tokenFlow != null) {
			tokenFlow.tokenDispatch(this);
		}
	}

	public synchronized void remove() {
		getInstance().removeToken(this);
		final TokenFlow tokenFlow = getCurrentFlow();
		assert(tokenFlow != null);
		if (tokenFlow != null) {
			tokenFlow.tokenExit(this);
		}
	}

	/**
	 * Gibt eine Kopie des Token an ein anderes Element weiter
	 */
	public synchronized void passTo(final TokenFlow tokenFlow, final Instance instance) {
		assert(tokenFlow != null);
		if (tokenFlow != null) {
			copyTo(instance).setCurrentFlow(tokenFlow);
		}
	}

	public synchronized void passTo(final TokenFlow tokenFlow) {
		passTo(tokenFlow, getInstance());
	}

}
