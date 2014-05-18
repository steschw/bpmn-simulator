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
package com.googlecode.bpmn_simulator.bpmn.model.process.activities;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.References;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElementsContainer;

public class SubProcess
		extends AbstractActivity
		implements FlowElementsContainer {

	public static final String ELEMENT_NAME = Messages.getString("subprocess"); //$NON-NLS-1$

	private final boolean triggeredByEvent;

	private References<FlowElement> flowElements;

	public SubProcess(final String id, final String name, final boolean triggeredByEvent) {
		super(id, name);
		this.triggeredByEvent = triggeredByEvent;
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public boolean isTriggeredByEvent() {
		return triggeredByEvent;
	}

	@Override
	public void setFlowElements(final References<FlowElement> flowElements) {
		this.flowElements = flowElements;
	}

}