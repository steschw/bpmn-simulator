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
package com.googlecode.bpmn_simulator.bpmn.model.core.common;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.bpmn_simulator.animation.execution.Animator;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.animation.token.TokenFlowListener;
import com.googlecode.bpmn_simulator.animation.token.Tokens;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.AbstractBaseElementNamed;

public abstract class AbstractFlowElement
		extends AbstractBaseElementNamed
		implements FlowElement {

	private static final int DEFAULT_STEP_COUNT = Animator.secondsToSteps(3.);

	private int stepCount = DEFAULT_STEP_COUNT;

	private final Set<TokenFlowListener> tokenFlowListeners
			= new HashSet<>();

	private final Tokens tokens = new Tokens();

	public AbstractFlowElement(final String id, final String name) {
		super(id, name);
	}

	@Override
	public void addTokenFlowListener(final TokenFlowListener listener) {
		assert !tokenFlowListeners.contains(listener);
		tokenFlowListeners.add(listener);
	}

	@Override
	public void removeTokenFlowListener(final TokenFlowListener listener) {
		tokenFlowListeners.add(listener);
	}

	protected void notifyTokenChanged(final Token token) {
		for (final TokenFlowListener listener : tokenFlowListeners) {
			listener.tokenChanged(token);
		}
	}

	@Override
	public Tokens getTokens() {
		return tokens;
	}

	@Override
	public void tokenEnter(final Token token) {
		tokens.add(token);
		notifyTokenChanged(token);
	}

	@Override
	public void tokenExit(final Token token) {
		tokens.remove(token);
		notifyTokenChanged(token);
	}

	@Override
	public int getStepCount() {
		return stepCount;
	}

	@Override
	public void setStepCount(final int count) {
		assert stepCount >= 0;
		stepCount = count;
	}

	protected abstract void tokenComplete(final Token token);

	@Override
	public void tokenDispatch(final Token token) {
		assert getTokens().contains(token);
		if (token.getPosition() > getStepCount()) {
			tokenComplete(token);
		} else {
			notifyTokenChanged(token);
		}
	}

}
