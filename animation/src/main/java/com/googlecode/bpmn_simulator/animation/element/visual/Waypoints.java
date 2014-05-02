package com.googlecode.bpmn_simulator.animation.element.visual;

import java.util.ArrayList;
import java.util.Iterator;

public class Waypoints
		extends ArrayList<Waypoint> {

	private static final long serialVersionUID = 8433732582598038968L;

	public boolean isValid() {
		return size() >= 2;
	}

	public Waypoint last() {
		if (size() >= 1)  {
			return get(size() - 1);
		}
		return null;
	}

	public Waypoint nextToLast() {
		if (size() >= 2)  {
			return get(size() - 2);
		}
		return null;
	}

	public Bounds getBounds() {
		final Iterator<Waypoint> i = iterator();
		if (i.hasNext()) {
			Waypoint waypoint = i.next();
			int x = waypoint.getX();
			int y = waypoint.getY();
			int minX = x;
			int maxX = x;
			int minY = y;
			int maxY = y;
			while (i.hasNext()) {
				waypoint = i.next();
				x = waypoint.getX();
				y = waypoint.getY();
				if (x < minX) {
					minX = x;
				}
				if (x > maxX) {
					maxX = x;
				}
				if (y < minY) {
					minY = y;
				}
				if (y > maxY) {
					maxY = y;
				}
			}
			return new Bounds(minX, minY, maxX - minX, maxY - minY);
		}
		throw new IllegalStateException();
	}

}
