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

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import com.google.code.bpmn_simulator.framework.element.ClickThroughMouseListener;
import com.google.code.bpmn_simulator.framework.element.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.element.geometry.Bounds;
import com.google.code.bpmn_simulator.framework.element.geometry.GeometryUtil;
import com.google.code.bpmn_simulator.framework.element.geometry.Waypoint;


@SuppressWarnings("serial")
public abstract class AbstractConnectingElement
		extends AbstractFlowElement {

	private final Deque<Waypoint> waypoints = new LinkedList<Waypoint>();

	public AbstractConnectingElement(final String id, final String name) {
		super(id, name);
		addMouseListener(new ClickThroughMouseListener());
	}

	public void addWaypoint(final Waypoint point) {
		waypoints.add(point);
		updateBounds();
	}

	protected Collection<Waypoint> getWaypoints() {
		return waypoints;
	}

	protected void updateBounds() {
		if (waypoints.isEmpty()) {
			assert false;
			return;
		}
		int minX = waypoints.getFirst().x;
		int maxX = minX + 1;
		int minY = waypoints.getFirst().y;
		int maxY = minY + 1;
		for (final Point point : waypoints) {
			if (point.x < minX) {
				minX = point.x;
			}
			if (point.x > maxX) {
				maxX = point.x;
			}
			if (point.y < minY) {
				minY = point.y;
			}
			if (point.y > maxY) {
				maxY = point.y;
			}
		}
		final int width = maxX - minX;
		final int height = maxY - minY;
		setInnerBounds(new Bounds(minX, minY, width, height));
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	protected Waypoint waypointToRelative(final Waypoint point) {
		return new Waypoint(SwingUtilities.convertPoint(getParent(), point, this));
	}

	@Override
	protected void paintElement(final GraphicsLayer g) {
		Waypoint lastPoint = null;
		final Iterator<Waypoint> i = getWaypoints().iterator();
		Waypoint currentPoint = null;
		if (i.hasNext()) {
			lastPoint = i.next();
			boolean first = true;
			while (i.hasNext()) {
				currentPoint = i.next();
				final Waypoint fromPoint = waypointToRelative(lastPoint);
				final Waypoint toPoint = waypointToRelative(currentPoint);
				g.drawLine(fromPoint, toPoint);
				if (first) {
					g.setStroke(getStartEndStroke());
					paintConnectingStart(g, toPoint, fromPoint);
					g.setStroke(getStroke());
					first = false;
				}
				if (!i.hasNext()) {
					g.setStroke(getStartEndStroke());
					paintConnectingEnd(g, fromPoint, toPoint);
					g.setStroke(getStroke());
				}
				lastPoint = currentPoint;
			}
		}
	}

	protected Stroke getStartEndStroke() {
		return new BasicStroke(getBorderWidth());
	}

	protected abstract void paintConnectingStart(final GraphicsLayer g, final Waypoint from, final Waypoint start);

	protected abstract void paintConnectingEnd(final GraphicsLayer g, final Waypoint from, final Waypoint end);

	protected int getLength() {
		int steps = 0;
		Point last = null;
		for (final Point current : getWaypoints()) {
			if (last != null) {
				steps += (int)last.distance(current);
			}
			last = current;
		}
		return steps;
	}

	protected Waypoint getPosition(final int length) {
		int position = 0;
		Waypoint last = null;
		for (final Waypoint current : getWaypoints()) {
			if (last != null) {
				final int distance = (int)last.distance(current);
				if ((position + distance) >= length) {
					return GeometryUtil.polarToCartesian(
							last,
							position - length,
							GeometryUtil.getAngle(current, last));
				}
				position += distance;
			}
			last = current;
		}
		assert last != null;
		return last;
	}

	@Override
	protected Point getElementCenter() {
		return getPosition(getLength() / 2);
	}

	@Override
	public void updateElementLabelPosition() {
		getElementLabel().setCenterTopPosition(getElementCenter());
	}

}
