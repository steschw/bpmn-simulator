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
package bpmn.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.JCheckBox;

import bpmn.element.gateway.ExclusiveGateway;
import bpmn.element.gateway.Gateway;
import bpmn.element.gateway.InclusiveGateway;

public class SequenceFlow extends TokenConnectingElement {

	private static final long serialVersionUID = 1L;

	private static final Color COLOR_TRUE = new Color(0, 196, 0);
	private static final Color COLOR_FALSE = new Color(0, 0, 0);

	private class ConditionExpression extends JCheckBox {

		private static final long serialVersionUID = 1L;

		private boolean value = false;

		public ConditionExpression(final String text) {
			super((String)null);

			setToolTipText(text);
			setVerticalAlignment(TOP);
			setFocusable(false);
			setOpaque(false);
			setValue(false);

			addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(final ItemEvent event) {
					setValue(event.getStateChange() == ItemEvent.SELECTED);
				}
			});
		}

		protected void setValue(final boolean value) {
			final Label label = getElementLabel();
			if (label != null) {
				label.setForeground(value ? COLOR_TRUE : COLOR_FALSE);
			}
			synchronized (this) {
				this.value = value;
			}
		}

		public synchronized boolean getValue() {
			return value;
		}

	}

	private ConditionExpression conditionExpression = null;
	private String expression = null;

	public SequenceFlow(final String id, final String name,
			final ElementRef<FlowElement> source, final ElementRef<FlowElement> target) {
		super(id, name, source, target);
	}

	public void setConditionExpression(final String expression) {
		assert(expression != null);
		this.expression = expression;
	}

	protected String getConditionExpression() {
		return expression;
	}

	@Override
	public String getName() {
		final String name = super.getName(); 
		if ((name == null) || name.isEmpty()) {
			return getConditionExpression();
		}
		return name;
	}

	protected boolean hasExpression() {
		final String expression = getConditionExpression();
		return ((expression != null) && !expression.isEmpty());
	}

	public boolean isConditional() {
		return ((hasExpression() || isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing())
				&& !isDefault());
	}

	public boolean acceptsToken() {
		return (!isConditional() || conditionExpression.getValue());
	}

	@Override
	public Label createElementLabel() {
		createExpressionControl();
		return super.createElementLabel();
	}

	protected void createExpressionControl() {
		if (isConditional()) {
			conditionExpression = new ConditionExpression(expression);
			add(conditionExpression, BorderLayout.CENTER);
			repositionConditionExpression();
		}
	}

	private void repositionConditionExpression() {
		assert(conditionExpression != null);
		if (getParent() != null) {
			final Point center = getElementCenter();
			if (center != null) {
				final Point position = waypointToRelative(center);
				final Dimension preferredSize = conditionExpression.getPreferredSize();
				conditionExpression.setBounds(
						position.x - (preferredSize.width / 2),
						position.y - (int)((preferredSize.height / 3.) * 2.),
						preferredSize.width, preferredSize.height);
			}
		}
	}

	@Override
	protected void updateBounds() {
		super.updateBounds();
		if (conditionExpression != null) {
			repositionConditionExpression();
		}
	}

	public boolean isDefault() {
		final ElementRef<FlowElement> sourceRef = getSourceRef();
		if ((sourceRef != null) && sourceRef.hasElement()) {
			final FlowElement flowElement = sourceRef.getElement();
			if (flowElement instanceof ElementWithDefaultSequenceFlow) {
				final ElementWithDefaultSequenceFlow element = (ElementWithDefaultSequenceFlow)flowElement;
				final ElementRef<SequenceFlow> defaultElementFlowRef = element.getDefaultElementFlowRef();
				if (defaultElementFlowRef != null) {
					return defaultElementFlowRef.equals(this);
				}
			}
		}
		return false;  
	}

	protected boolean isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing() {
		final ElementRef<FlowElement> sourceRef = getSourceRef();
		if ((sourceRef != null) && sourceRef.hasElement()) {
			final FlowElement sourceElement = sourceRef.getElement();
			if ((sourceElement instanceof InclusiveGateway)
					|| (sourceElement instanceof ExclusiveGateway)) {
				final Gateway gateway = (Gateway)sourceElement;
				return (gateway.getOutgoing().size() > 1);
			}
		}
		return false;
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		final Iterator<Point> waypoints = getWaypoints().iterator();
		if (waypoints.hasNext()) {
			Point prevPoint = waypointToRelative(waypoints.next());
			if (waypoints.hasNext()) {
				// Startsymbol
				Point curPoint = waypointToRelative(waypoints.next());
				if (isDefault()) {
					g.drawDefaultSymbol(prevPoint, curPoint);
				} else if (!isSourceElementInclusiveOrExclusiveGatewayAndHasMoreThanOnceOutgoing()
						&& isConditional()) {
					g.drawConditionalSymbol(prevPoint, curPoint);
				}
				// Endepfeil
				while (waypoints.hasNext()) {
					prevPoint = curPoint;
					curPoint = waypointToRelative(waypoints.next());
				}
				g.fillArrow(prevPoint, curPoint);
			}
		}
	}

}
