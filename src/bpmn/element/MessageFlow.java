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

import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.Stroke;
import java.util.Iterator;

public class MessageFlow extends ConnectingElement {

	private static final long serialVersionUID = 1L;

	public MessageFlow(final String id, final ElementRef<FlowElement> source,
			final ElementRef<FlowElement> target) {
		super(id, null, source, target);
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 3.f, 6.f }, 0); 
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		g.setStroke(new BasicStroke(1));
		final Iterator<Point> waypoints = getWaypoints().iterator();
		if (waypoints.hasNext()) {
			Point prevPoint = waypointToRelative(waypoints.next());
			if (waypoints.hasNext()) {
				// Startsymbol
				final Rectangle point = new Rectangle(prevPoint);
				point.grow(3, 3);
				g.setPaint(getBackground());
				g.fillOval(point);
				g.setPaint(getForeground());
				g.drawOval(point);
				// Endepfeil
				Point curPoint = waypointToRelative(waypoints.next());
				while (waypoints.hasNext()) {
					prevPoint = curPoint;
					curPoint = waypointToRelative(waypoints.next());
				}
				g.setPaint(getBackground());
				g.fillArrow(prevPoint, curPoint);
				g.setPaint(getForeground());
				g.drawArrow(prevPoint, curPoint);
			}
		}
	}

}
