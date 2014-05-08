/*
 * Copyright (C) 2014 Stefan Schweitzer
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

import com.googlecode.bpmn_simulator.animation.token.TokenFlowListener;
import com.googlecode.bpmn_simulator.animation.token.Tokens;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.AbstractBaseElement;

public abstract class AbstractFlowElement
		extends AbstractBaseElement
		implements FlowElement {

	private final Set<TokenFlowListener> tokenFlowListeners
			= new HashSet<TokenFlowListener>();

	private final String name;

	public AbstractFlowElement(final String id, final String name) {
		super(id);
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	public boolean hasName() {
		final String name = getName();
		return (name != null) && !name.isEmpty();
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

	@Override
	public Tokens getTokens() {
		return null;
	}

}
