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
package com.googlecode.bpmn_simulator.bpmn.model.process.activities;

import com.googlecode.bpmn_simulator.animation.ref.Reference;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractFlowNode;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;

public abstract class AbstractActivity
		extends AbstractFlowNode
		implements Activity {

	private boolean forCompensation;

	private Reference<SequenceFlow> defaultSequenceFlow;

	private LoopCharacteristics loopCharacteristics;

	public AbstractActivity(final String id, final String name, final boolean isForCompensation) {
		super(id, name);
		forCompensation = isForCompensation;
	}

	@Override
	public void setDefaultSequenceFlow(final Reference<SequenceFlow> sequenceFlow) {
		defaultSequenceFlow = sequenceFlow;
	}

	@Override
	public SequenceFlow getDefaultSequenceFlow() {
		if (defaultSequenceFlow != null) {
			return defaultSequenceFlow.getReferenced();
		}
		return null;
	}

	@Override
	public void setLoopCharacteristics(final LoopCharacteristics loopCharacteristics) {
		this.loopCharacteristics = loopCharacteristics;
	}

	@Override
	public LoopCharacteristics getLoopCharacteristics() {
		return loopCharacteristics;
	}

	@Override
	public boolean isForCompensation() {
		return forCompensation;
	}

	@Override
	protected void forwardToken(final Token token) {
		copyTokenToOutgoing(token, false, this);
	}

}
