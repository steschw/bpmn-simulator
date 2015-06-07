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

import java.util.Map;

public class Token
		implements DataHolder {

	private final Instance instance;

	private final TokenFlow currentTokenFlow;

//	private final TokenFlow previousFlow;

	private int position;

	private Map<? extends Object, ? extends Object> data = null;

	protected Token(final Instance instance,
			final TokenFlow current) {
		super();
		if (instance == null) {
			throw new IllegalArgumentException();
		}
		this.instance = instance;
		if (current == null) {
			throw new IllegalArgumentException();
		}
		this.currentTokenFlow = current;
	}

	public Instance getInstance() {
		return instance;
	}

	public TokenFlow getCurrentTokenFlow() {
		return currentTokenFlow;
	}

	@Override
	public Map<? extends Object, ? extends Object> getData() {
		return data;
	}

	@Override
	public void setData(final Map<? extends Object, ? extends Object> data) {
		this.data = data;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(final int position) {
		assert position >= 0;
		this.position = position;
	}

	public void step(final int count) {
		setPosition(getPosition() + count);
		getCurrentTokenFlow().tokenDispatch(this);
	}

	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder('[');
		buffer.append(super.toString());
		buffer.append(", "); //$NON-NLS-1$
		buffer.append(instance);
		buffer.append(", "); //$NON-NLS-1$
		buffer.append(currentTokenFlow);
		buffer.append(']');
		return buffer.toString();
	}

	public void remove() {
		getInstance().removeToken(this);
	}

	public void copyTo(final TokenFlow tokenFlow) {
		assert getCurrentTokenFlow() != tokenFlow;
		getInstance().createNewToken(tokenFlow);
	}

	public void moveTo(final TokenFlow tokenFlow) {
		copyTo(tokenFlow);
		remove();
	}

}
