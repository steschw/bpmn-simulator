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
package com.googlecode.bpmn_simulator.bpmn.model.core.common;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.framework.element.ElementRef;


@SuppressWarnings("serial")
public abstract class AbstractTokenFlowElementWithDefault
		extends AbstractTokenFlowElement
		implements ElementWithDefaultSequenceFlow {

	private ElementRef<SequenceFlow> defaultSequenceFlowRef;

	public AbstractTokenFlowElementWithDefault(final String id, final String name) {
		super(id, name);
	}

	@Override
	public void setDefaultSequenceFlowRef(
			final ElementRef<SequenceFlow> sequenceFlowRef) {
		defaultSequenceFlowRef = sequenceFlowRef;
	}

	@Override
	public ElementRef<SequenceFlow> getDefaultSequenceFlowRef() {
		return defaultSequenceFlowRef;
	}

	@Override
	public SequenceFlow getDefaultSequenceFlow() {
		final ElementRef<SequenceFlow> sequenceFlowRef = getDefaultSequenceFlowRef();
		return (sequenceFlowRef == null) ? null : sequenceFlowRef.getElement();
	}

	protected final boolean passTokenToFirstOutgoing(final Token token) {
		return passTokenToFirstOutgoing(token, token.getInstance());
	}

	protected final boolean passTokenToFirstOutgoing(final Token token, final Instance instance) {
		if (isEndNode()) {
			notifyTokenReachedEndNode(token);
			return false;
		} else {
			if (passTokenToFirstSequenceFlow(token, instance)) {
				return true;
			} else {
				return passTokenToDefaultSequenceFlow(token, instance);
			}
		}
	}

	@Override
	protected boolean passTokenToAllNextElements(final Token token, final Instance instance) {
		if (isEndNode()) {
			notifyTokenReachedEndNode(token);
			return false;
		} else {
			if (passTokenToAllOutgoingSequenceFlows(token, instance) == 0) {
				return passTokenToDefaultSequenceFlow(token, instance);
			} else {
				return true;
			}
		}
	}

	protected boolean passTokenToDefaultSequenceFlow(final Token token, final Instance instance) {
		final SequenceFlow defaultSequenceFlow = getDefaultSequenceFlow();
		if (defaultSequenceFlow != null) {
			token.passTo(defaultSequenceFlow, instance);
			return true;
		} else {
			return false;
		}
	}

}
