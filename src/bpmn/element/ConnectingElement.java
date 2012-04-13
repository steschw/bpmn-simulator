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

import java.awt.Point;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

public abstract class ConnectingElement extends BaseElement {

	private static final long serialVersionUID = 1L;

	private final Deque<Point> waypoints = new LinkedList<Point>();

	private ElementRef<FlowElement> sourceRef;
	private ElementRef<FlowElement> targetRef;

	public ConnectingElement(final String id, final String name,
			final ElementRef<FlowElement> source, final ElementRef<FlowElement> target) {
		super(id, name);
		setSourceRef(source);
		setTarget(target);
	}

	protected void setSourceRef(final ElementRef<FlowElement> elementRef) {
		assert (elementRef != null);
		this.sourceRef = elementRef;
	}

	protected ElementRef<FlowElement> getSourceRef() {
		return sourceRef;
	}

	protected static final <E extends BaseElement> E getElementFromElementRef(
			final ElementRef<E> elementRef) {
		if ((elementRef != null) && elementRef.hasElement()) {
			return elementRef.getElement();
		}
		return null;
	}

	public FlowElement getSource() {
		return getElementFromElementRef(getSourceRef());
	}

	protected void setTarget(final ElementRef<FlowElement> elementRef) {
		assert (elementRef != null);
		this.targetRef = elementRef;
	}

	protected ElementRef<FlowElement> getTargetRef() {
		return targetRef;
	}

	public FlowElement getTarget() {
		return getElementFromElementRef(getTargetRef());
	}

	public void addWaypoint(final Point point) {
		waypoints.add(point);
		updateBounds();
	}

	protected Collection<Point> getWaypoints() {
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
		for (Point point : waypoints) {
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
		setInnerBounds(new Rectangle(minX, minY, width, height));
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	protected Point waypointToRelative(final Point point) {
		return SwingUtilities.convertPoint(getParent(), point, this);
	}

	@Override
	protected void paintElement(final Graphics g) {
		Point lastPoint = null;
		final Iterator<Point> i = getWaypoints().iterator();
		Point currentPoint = null;
		while (i.hasNext()) {
			currentPoint = i.next();
			if (lastPoint != null) {
				final Point fromPoint = waypointToRelative(lastPoint);
				final Point toPoint = waypointToRelative(currentPoint);
				g.drawLine(fromPoint, toPoint);
			}
			lastPoint = currentPoint;
		}
	}

	protected int getLength() {
		int steps = 0;
		Point last = null;
		for (Point current : getWaypoints()) {
			if (last != null) {
				steps += (int)last.distance(current);
			}
			last = current;
		}
		return steps;
	}

	protected Point getPosition(final int length) {
		int position = 0;
		Point last = null;
		for (Point current : getWaypoints()) {
			if (last != null) {
				final int distance = (int)last.distance(current); 
				if ((position + distance) >= length) {
					return Graphics.polarToCartesian(
							last,
							position - length,
							Graphics.getAngle(current, last));
				}
				position += distance;
			}
			last = current;
		}
		return null;
	}

	@Override
	protected Point getElementCenter() {
		return getPosition(getLength() / 2);
	}

	@Override
	protected void initLabel(final Label label) {
		label.setCenterTopPosition(getElementCenter());
	}

}
