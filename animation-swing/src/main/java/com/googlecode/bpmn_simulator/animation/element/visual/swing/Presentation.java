/*
 * Copyright (C) 2014 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.animation.element.visual.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Iterator;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.GeometryUtils;
import com.googlecode.bpmn_simulator.animation.element.visual.Point;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoint;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoints;
import com.googlecode.bpmn_simulator.animation.token.Token;

public class Presentation {

	private static final int PENTAGON_CORNERS = 5;

	private static final RenderingHints QUALITY = new RenderingHints(null);
	private static final RenderingHints SPEED = new RenderingHints(null);

	private static final double ARROW_ANGLE = 30.;
	private static final int ARROW_LENGTH = 10;

	static {
		QUALITY.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		QUALITY.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		QUALITY.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		SPEED.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);
		SPEED.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);
		SPEED.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		SPEED.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		SPEED.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
	}

	private static RenderingHints renderingHints = QUALITY;

	public synchronized void init(final Graphics2D g) {
		g.setRenderingHints(renderingHints);
	}

	public void drawImage(final Graphics g, final Image image, final Bounds bounds) {
		g.drawImage(image, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), null);
	}

	public void drawLine(final Graphics g, final Waypoint from, final Waypoint to) {
		g.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
	}

	public void drawLine(final Graphics g, final Waypoints points) {
		Waypoint from;
		Waypoint to = null;
		final Iterator<Waypoint> i = points.iterator();
		while (i.hasNext()) {
			from = to;
			to = i.next();
			if (from != null) {
				drawLine(g, from, to);
			}
		}
	}

	public void drawArrowEnd(final Graphics g, final Waypoints points) {
		if (points.isValid()) {
			drawArrowEnd(g, points.nextToLast(), points.last());
		}
	}

	public void drawArrowEnd(final Graphics g, final Waypoint from, final Waypoint to) {
	}

	public void drawRect(final Graphics g, final Bounds bounds) {
		g.drawRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public void drawRoundRect(final Graphics g, final Bounds bounds, final int arcSize) {
		g.drawRoundRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), arcSize, arcSize);
	}

	public void drawOval(final Graphics g, final Bounds bounds) {
		g.drawOval(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public void drawArc(final Graphics g, final Bounds bounds, final int startAngle, final int arcAngle) {
		g.drawArc(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), startAngle, arcAngle);
	}

	public void drawDiamond(final Graphics2D g, final Bounds bounds) {
		g.draw(createDiamond(bounds));
	}

	public void drawToken(final Graphics g, final Token token, final int centerX, final int centerY) {
		
	}

	public static Shape createPentagon(final Bounds bounds) {
		final Polygon polygon = new Polygon();
		final Point center = bounds.getCenter();
		final double r = bounds.getWidth() / 2.;
		final double radPerCorner = GeometryUtils.RAD_FULL / PENTAGON_CORNERS;
		Point point = null;
		for (int i = 0; i < PENTAGON_CORNERS; ++i) {
			point = GeometryUtils.polarToCartesian(
					center, r,
					radPerCorner * i - radPerCorner / 2.);
			polygon.addPoint(point.getX(), point.getY());
		}
		return polygon;
	}

	public static Shape createStar(final Bounds bounds, final int cornerCount) {
		final Polygon polygon = new Polygon();
		final Point center = bounds.getCenter();
		final double r = bounds.getWidth() / 2.;
		final double radPerCorner = GeometryUtils.RAD_FULL / cornerCount;
		Point point = null;
		for (int i = 0; i < cornerCount; ++i) {
			final double rad = radPerCorner * i;
			point = GeometryUtils.polarToCartesian(center, r, rad - radPerCorner / 2.);
			polygon.addPoint(point.getX(), point.getY());
			point = GeometryUtils.polarToCartesian(center, r * 0.5, rad);
			polygon.addPoint(point.getX(), point.getY());
		}
		return polygon;
	}

	public static Shape createDiamond(final Bounds bounds) {
		final Polygon polygon = new Polygon();
		polygon.addPoint((int) bounds.getMinX(), (int) bounds.getCenterY());
		polygon.addPoint((int) bounds.getCenterX(), (int) bounds.getMinY());
		polygon.addPoint((int) bounds.getMaxX(), (int) bounds.getCenterY());
		polygon.addPoint((int) bounds.getCenterX(), (int) bounds.getMaxY());
		return polygon;
	}

	public static Shape createArrowPath(
			final Waypoint from, final Waypoint to,
			final double d, final double length) {
		final Path2D.Float path = new Path2D.Float();
		final double angle = to.angleTo(from);
		final Point point1 = GeometryUtils.polarToCartesian(to, length, angle - d);
		path.moveTo(point1.getX(), point1.getY());
		path.lineTo(to.getX(), to.getY());
		final Point point2 = GeometryUtils.polarToCartesian(to, length, angle + d);
		path.lineTo(point2.getX(), point2.getY());
		return path;
	}

}
