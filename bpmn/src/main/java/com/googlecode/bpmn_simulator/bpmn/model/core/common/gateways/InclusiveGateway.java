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
package com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.Reference;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.DefaultSequenceFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;

public final class InclusiveGateway
		extends AbstractMergingGateway
		implements DefaultSequenceFlowElement {

	public static final String ELEMENT_NAME = Messages.getString("inclusiveGateway"); //$NON-NLS-1$

	private Reference<SequenceFlow> defaultSequenceFlow;

	public InclusiveGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
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

}
