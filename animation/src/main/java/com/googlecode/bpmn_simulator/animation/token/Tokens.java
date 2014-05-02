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
package com.googlecode.bpmn_simulator.animation.token;

import java.util.ArrayList;
import java.util.Collection;

public class Tokens
		extends ArrayList<Token> {

	private static final long serialVersionUID = 3081643367990940828L;

	public Tokens() {
		super();
	}

	public Tokens(final Collection<? extends Token> c) {
		super(c);
	}

	@Override
	public synchronized boolean addAll(final Collection<? extends Token> c) {
		for (final Token token : c) {
			if (contains(token)) {
				assert false;
			}
		}
		return super.addAll(c);
	}

	@Override
	public synchronized boolean add(final Token token) {
		assert !contains(token);
		return super.add(token);
	}

	public synchronized boolean remove(final Token token) {
		assert contains(token);
		return super.remove(token);
	}

	public synchronized Collection<Instance> getInstances() {
		final Collection<Instance> instances = new ArrayList<Instance>();
		for (final Token token : this) {
			final Instance instance = token.getInstance();
			if (!instances.contains(instance)) {
				instances.add(instance);
			}
		}
		return instances;
	}

	public synchronized Tokens getByInstance(final Instance instance) {
		final Tokens tokens = new Tokens();
		for (final Token token : this) {
			final Instance tokenInstance = token.getInstance();
			if (tokenInstance.equals(instance)
					&& !tokens.contains(token)) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public synchronized Tokens getByCurrentTokenFlow(final TokenFlow tokenFlow) {
		final Tokens tokens = new Tokens();
		for (final Token token : this) {
			final TokenFlow tokenTokenFlow = token.getCurrentTokenFlow();
			if (tokenTokenFlow.equals(tokenFlow)
					&& !tokens.contains(token)) {
				tokens.add(token);
			}
		}
		return tokens;
	}

}