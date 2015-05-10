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
package com.googlecode.bpmn_simulator.animation.execution;

import java.util.Iterator;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.RootInstances;
import com.googlecode.bpmn_simulator.animation.token.Token;

public class Animator
		extends AbstractAnimator {

	private RootInstances instances;

	public Animator(final RootInstances instances) {
		super();
		setInstances(instances);
		start();
	}

	protected synchronized void setInstances(final RootInstances instances) {
		this.instances = instances;
	}

	public RootInstances getInstances() {
		return instances;
	}

	private void stepToken(final Token token, final int count) {
		token.getCurrentTokenFlow().tokenDispatch(token);
	}

	private void stepInstance(final Instance instance, final int count) {
		final Iterator<Instance> childInstances = instance.iterator();
		while (childInstances.hasNext()) {
			final Instance childInstance = childInstances.next();
			final Iterator<Token> tokens = childInstance.getTokens(false).iterator();
			while (tokens.hasNext()) {
				final Token token = tokens.next();
				stepToken(token, count);
			}
			stepInstance(childInstance, count);
		}
	}

	@Override
	public synchronized void step(final int count) {
		final Iterator<Instance> i = getInstances().iterator();
		while (i.hasNext()) {
			final Instance instance = i.next();
			stepInstance(instance, count);
		}
	}

}
