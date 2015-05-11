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

import com.googlecode.bpmn_simulator.animation.ref.Reference;
import com.googlecode.bpmn_simulator.bpmn.Messages;

public final class SequenceFlow
		extends AbstractFlowElement {

	public static final String ELEMENT_NAME = Messages.getString("sequenceFlow"); //$NON-NLS-1$

	private final Reference<FlowNode> source;
	private final Reference<FlowNode> target;

	private Expression conditionExpression;

	public SequenceFlow(final String id, final String name,
			Reference<FlowNode> source, Reference<FlowNode> target) {
		super(id, name);
		this.source = source;
		this.target = target;
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public Reference<FlowNode> getSource() {
		return source;
	}

	public Reference<FlowNode> getTarget() {
		return target;
	}

	public void setConditionExpression(final Expression expression) {
		this.conditionExpression = expression;
	}

	protected Expression getCondition() {
		return conditionExpression;
	}

}
