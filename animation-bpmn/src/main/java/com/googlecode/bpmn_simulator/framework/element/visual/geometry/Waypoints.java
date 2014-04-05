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
package com.googlecode.bpmn_simulator.framework.element.visual.geometry;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class Waypoints
		extends LinkedList<Waypoint> {

	public Bounds getBounds() {
		if (isEmpty()) {
			return null;
		}
		int minX = getFirst().x;
		int maxX = minX + 1;
		int minY = getFirst().y;
		int maxY = minY + 1;
		for (final Waypoint point : this) {
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
		return new Bounds(minX, minY, width, height);
	}

	public int getLength() {
		int steps = 0;
		Waypoint last = null;
		for (final Waypoint current : this) {
			if (last != null) {
				steps += (int)last.distance(current);
			}
			last = current;
		}
		return steps;
	}

	public Waypoint getPosition(final int length) {
		int position = 0;
		Waypoint last = null;
		for (final Waypoint current : this) {
			if (last != null) {
				final int distance = (int)last.distance(current);
				if ((position + distance) >= length) {
					return GeometryUtil.polarToCartesian(last, position
							- length, GeometryUtil.getAngle(current, last));
				}
				position += distance;
			}
			last = current;
		}
		assert last != null;
		return last;
	}

}
