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
package com.google.code.bpmn_simulator.bpmn.model.core.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import com.google.code.bpmn_simulator.bpmn.Messages;
import com.google.code.bpmn_simulator.bpmn.model.core.common.gateways.AbstractGateway;
import com.google.code.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.google.code.bpmn_simulator.bpmn.model.core.common.gateways.InclusiveGateway;
import com.google.code.bpmn_simulator.framework.element.ElementRef;
import com.google.code.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.element.visual.geometry.Waypoint;


@SuppressWarnings("serial")
public final class SequenceFlow
		extends AbstractTokenConnectingElement {

	public static final String ELEMENT_NAME = Messages.getString("sequenceFlow"); //$NON-NLS-1$

	private Expression condition;

	public SequenceFlow(final String id, final String name,
			final ElementRef<AbstractTokenFlowElement> source,
			final ElementRef<AbstractTokenFlowElement> target) {
		super(id, name, source, target);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public void setCondition(final Expression condition) {
		this.condition = condition;
		if (condition != null) {
			add(condition);
			updateConditionPosition();
		}
	}

	protected Expression getCondition() {
		return condition;
	}

	protected boolean hasCondition() {
		return getCondition() != null;
	}

	public boolean isConditional() {
		return (hasCondition()
				|| isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing())
					&& !isDefault();
	}

	public boolean acceptsToken() {
		return !isConditional() || getCondition().isTrue();
	}

	@Override
	public void initSubElements() {
		super.initSubElements();
		initExpressionControl();
	}

	protected void initExpressionControl() {
		if (isConditional()) {
			if (!hasCondition()) {
				setCondition(new Expression());
			}
		}
	}

	private void updateConditionPosition() {
		assert hasCondition();
		if (getParent() != null) {
			final Point center = getElementCenter();
			if (center != null) {
				final Point position = center; //waypointToRelative(center);
				final Dimension preferredSize = getCondition().getPreferredSize();
				getCondition().setBounds(
						position.x - (preferredSize.width / 2),
						position.y - (int)((preferredSize.height / 3.) * 2.),
						preferredSize.width, preferredSize.height);
				getParent().setComponentZOrder(getCondition(), 0);
			}
		}
	}

	@Override
	protected void updateBounds() {
		super.updateBounds();
		if (hasCondition()) {
			updateConditionPosition();
		}
	}

	public boolean isDefault() {
		final AbstractFlowElement flowElement = getSource();
		if (flowElement instanceof ElementWithDefaultSequenceFlow) {
			final ElementWithDefaultSequenceFlow element = (ElementWithDefaultSequenceFlow)flowElement;
			final ElementRef<SequenceFlow> defaultElementFlowRef = element.getDefaultSequenceFlowRef();
			if (defaultElementFlowRef != null) {
				return defaultElementFlowRef.equalsElement(this);
			}
		}
		return false;
	}

	protected boolean isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing() {
		final AbstractFlowElement sourceElement = getSource();
		if ((sourceElement instanceof InclusiveGateway)
				|| (sourceElement instanceof ExclusiveGateway)) {
			return ((AbstractGateway)sourceElement).getOutgoing().size() > 1;
		}
		return false;
	}

	@Override
	protected void paintConnectingStart(final GraphicsLayer g, final Waypoint from, final Waypoint start) {
		if (isDefault()) {
			g.drawDefaultSymbol(start, from);
		} else if (!isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing()
				&& isConditional()) {
			g.setPaint(Color.red);
			g.drawConditionalSymbol(start, from);
		}
	}

	@Override
	protected void paintConnectingEnd(final GraphicsLayer g, final Waypoint from, final Waypoint end) {
		g.fillArrow(from, end);
	}

}
