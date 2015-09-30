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
package com.googlecode.bpmn_simulator.bpmn.model.choreography;

import java.util.ArrayList;
import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElements;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElementsContainer;

public class Choreography
		extends Collaboration
		implements FlowElementsContainer {

	static {
		LogicalElements.register(Choreography.class, Messages.getString("choreography")); //$NON-NLS-1$
	}

	private final Collection<FlowElement> flowElements = new ArrayList<>();

	public Choreography(final String id, final String name, final boolean isClosed) {
		super(id, name, isClosed);
	}

	@Override
	public void addFlowElement(final FlowElement flowElement) {
		flowElements.add(flowElement);
	}

	@Override
	public Collection<FlowElement> getFlowElements() {
		return flowElements;
	}

}
