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
package com.google.code.bpmn_simulator.framework.element;

import java.util.Collection;

import javax.swing.SwingUtilities;

import com.google.code.bpmn_simulator.framework.element.geometry.Bounds;
import com.google.code.bpmn_simulator.framework.element.geometry.Waypoint;
import com.google.code.bpmn_simulator.framework.element.geometry.Waypoints;

@SuppressWarnings("serial")
public abstract class AbstractVisualEdgeElement
		extends AbstractVisualElement
		implements VisualEdgeElement {

	private final Waypoints waypoints = new Waypoints();

	public AbstractVisualEdgeElement(final LogicalElement element) {
		super(element);
	}

	@Override
	public void addWaypoint(final Waypoint point) {
		waypoints.add(point);
		updateInnerBounds();
	}

	protected Collection<Waypoint> getWaypoints() {
		return waypoints;
	}

	private void updateInnerBounds() {
		final Bounds innerBounds = waypoints.getBounds();
		assert innerBounds != null;
		setInnerBounds(innerBounds);
	}

	protected Waypoint waypointToRelative(final Waypoint point) {
		return new Waypoint(SwingUtilities.convertPoint(getParent(), point, this));
	}

}
