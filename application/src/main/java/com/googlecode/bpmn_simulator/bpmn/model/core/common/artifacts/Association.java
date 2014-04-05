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
package com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts;

import java.awt.Stroke;

import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractConnectingElement;
import com.googlecode.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.googlecode.bpmn_simulator.framework.element.visual.geometry.Waypoint;


@SuppressWarnings("serial")
public class Association
		extends AbstractConnectingElement {

	public static final String ELEMENT_NAME = Messages.getString("association"); //$NON-NLS-1$

	public static enum Direction {
		NONE,
		ONE,
		BOTH;

		public static Direction byValue(final String value) {
			if ("none".equalsIgnoreCase(value)) { //$NON-NLS-1$
				return NONE;
			} else if ("one".equalsIgnoreCase(value)) { //$NON-NLS-1$
				return ONE;
			} else if ("both".equalsIgnoreCase(value)) { //$NON-NLS-1$
				return BOTH;
			} else {
				return null;
			}
		}

	}

	private Direction direction = Direction.NONE;

	public Association(final String id) {
		super(id, null);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	@Override
	protected Stroke getStroke() {
		return getVisualization().createStrokeDotted(getBorderWidth());
	}

	@Override
	protected void paintConnectingStart(final GraphicsLayer g, final Waypoint from,
			final Waypoint start) {
		if (Direction.BOTH.equals(getDirection())) {
			g.drawArrow(from, start);
		}
	}

	@Override
	protected void paintConnectingEnd(final GraphicsLayer g, final Waypoint from,
			final Waypoint end) {
		final Direction direction = getDirection();
		if (Direction.ONE.equals(direction) || Direction.BOTH.equals(direction)) {
			g.drawArrow(from, end);
		}
	}

}
