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
package com.googlecode.bpmn_simulator.framework.element.visual;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import com.googlecode.bpmn_simulator.framework.element.visual.geometry.Bounds;
import com.googlecode.bpmn_simulator.framework.element.visual.geometry.GeometryUtil;
import com.googlecode.bpmn_simulator.framework.element.visual.geometry.Waypoint;

public class GraphicsLayer {

	private static final double RAD_30 = GeometryUtil.RAD_FULL / 12.;

	private static final double ARROW_TRIANGLE_LENGTH = 10.;

	private static final double CONNECTING_SYMBOL_LENGTH = 12.;

	private static final double DEFAULT_SYMBOL_ANGLE =
			GeometryUtil.RAD_FULL / 3.;

	private static final RenderingHints QUALITY = new RenderingHints(null);

	private static final float CONTRAST_BIRGHTNESS_THRESHOLD = 0.4f;

	private final Graphics2D graphics;

	private Stroke storedStroke;
	private Paint storedPaint;

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
		/*
		 * QUALITY.put(RenderingHints.KEY_STROKE_CONTROL,
		 * RenderingHints.VALUE_STROKE_PURE);
		 */
	}

	public GraphicsLayer(final Graphics2D g) {
		super();
		this.graphics = g;
	}

	public static Color contrastColor(final Color from) {
		if (from == null) {
			return Color.BLACK;
		} else {
			final float[] hsbvals =
					Color.RGBtoHSB(from.getRed(), from.getBlue(),
							from.getGreen(), null);
			if (hsbvals[2] < CONTRAST_BIRGHTNESS_THRESHOLD) {
				return Color.WHITE;
			} else {
				return Color.BLACK;
			}
		}
	}

	public void enableAntialiasing() {
		graphics.addRenderingHints(QUALITY);
	}

	public final void push() {
		storedStroke = graphics.getStroke();
		storedPaint = graphics.getPaint();
	}

	public final void pop() {
		graphics.setStroke(storedStroke);
		graphics.setPaint(storedPaint);
	}

	public final void setPaint(final Paint paint) {
		graphics.setPaint(paint);
	}

	public final Paint getPaint() {
		return graphics.getPaint();
	}

	public final void setStroke(final Stroke stroke) {
		graphics.setStroke(stroke);
	}

	public void drawIcon(final Icon icon, final Point positionTopLeft) {
		icon.paintIcon(null, graphics, positionTopLeft.x, positionTopLeft.y);
	}

	public void draw(final Shape shape) {
		graphics.draw(shape);
	}

	public void fill(final Shape shape) {
		graphics.fill(shape);
	}

	public void drawRoundRect(final Bounds rect, final int arcWidth,
			final int arcHeight) {
		graphics.drawRoundRect(rect.x, rect.y, rect.width, rect.height,
				arcWidth, arcHeight);
	}

	public void fillRoundRect(final Bounds rect, final int arcWidth,
			final int arcHeight) {
		graphics.fillRoundRect(rect.x, rect.y, rect.width, rect.height,
				arcWidth, arcHeight);
	}

	public void fillOval(final Bounds size) {
		graphics.fillOval(size.x, size.y, size.width, size.height);
	}

	public void drawOval(final Bounds size) {
		graphics.drawOval(size.x, size.y, size.width, size.height);
	}

	public void fillRect(final Bounds rect) {
		graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	public void drawRect(final Bounds rect) {
		graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
	}

	public void fillDiamond(final Bounds size) {
		fill(GeometryUtil.createDiamond(size));
	}

	public void drawDiamond(final Bounds size) {
		draw(GeometryUtil.createDiamond(size));
	}

	public void drawCross(final Bounds rect, final boolean rotated) {
		if (rotated) {
			graphics.drawLine((int)rect.getMinX(), (int)rect.getMinY(),
					(int)rect.getMaxX(), (int)rect.getMaxY());
			graphics.drawLine((int)rect.getMinX(), (int)rect.getMaxY(),
					(int)rect.getMaxX(), (int)rect.getMinY());
		} else {
			graphics.drawLine((int)rect.getMinX(), (int)rect.getCenterY(),
					(int)rect.getMaxX(), (int)rect.getCenterY());
			graphics.drawLine((int)rect.getCenterX(), (int)rect.getMinY(),
					(int)rect.getCenterX(), (int)rect.getMaxY());
		}
	}

	public void drawLine(final Point from, final Point to) {
		graphics.drawLine(from.x, from.y, to.x, to.y);
	}

	public static GeneralPath createArrowPath(final Waypoint from,
			final Waypoint to) {
		return GeometryUtil.createArrowPath(from, to, RAD_30,
				ARROW_TRIANGLE_LENGTH);
	}

	public void fillArrow(final Waypoint from, final Waypoint to) {
		graphics.fill(createArrowPath(from, to));
	}

	public void drawArrow(final Waypoint from, final Waypoint to) {
		final double angle = GeometryUtil.getAngle(to, from);
		drawLine(
				to,
				GeometryUtil.polarToCartesian(to, ARROW_TRIANGLE_LENGTH, angle
						- RAD_30));
		drawLine(
				to,
				GeometryUtil.polarToCartesian(to, ARROW_TRIANGLE_LENGTH, angle
						+ RAD_30));
	}

	public void drawStar(final Bounds size, final int corners) {
		graphics.drawPolygon(GeometryUtil.createStar(size, corners));
	}

	public void fillStar(final Bounds size, final int corners) {
		graphics.fillPolygon(GeometryUtil.createStar(size, corners));
	}

	public void drawPentagon(final Bounds size) {
		graphics.drawPolygon(GeometryUtil.createPentagon(size));
	}

	protected static Polygon createConditionalSymbol(final Waypoint orgin,
			final double a) {
		final Polygon polygon = new Polygon();
		polygon.addPoint(orgin.x, orgin.y);
		Waypoint to =
				GeometryUtil.polarToCartesian(orgin, CONNECTING_SYMBOL_LENGTH,
						a - RAD_30);
		polygon.addPoint(to.x, to.y);
		to =
				GeometryUtil.polarToCartesian(to, CONNECTING_SYMBOL_LENGTH, a
						+ RAD_30);
		polygon.addPoint(to.x, to.y);
		to =
				GeometryUtil.polarToCartesian(orgin, CONNECTING_SYMBOL_LENGTH,
						a + RAD_30);
		polygon.addPoint(to.x, to.y);
		return polygon;
	}

	public void drawConditionalSymbol(final Waypoint from, final Waypoint to) {
		final Polygon symbol =
				createConditionalSymbol(from, GeometryUtil.getAngle(from, to));

		graphics.setPaint(Color.WHITE);
		graphics.fill(symbol);
		graphics.setPaint(Color.BLACK);
		graphics.draw(symbol);
	}

	public void drawDefaultSymbol(final Waypoint from, final Waypoint to) {
		final double a = GeometryUtil.getAngle(from, to);
		final double halfConnectingSymbolLength = CONNECTING_SYMBOL_LENGTH / 2;
		final Waypoint orgin =
				GeometryUtil.polarToCartesian(from, halfConnectingSymbolLength,
						a);

		final Waypoint symbolFrom =
				GeometryUtil.polarToCartesian(orgin,
						halfConnectingSymbolLength, a - DEFAULT_SYMBOL_ANGLE);
		final Waypoint symbolTo =
				GeometryUtil.polarToCartesian(symbolFrom,
						CONNECTING_SYMBOL_LENGTH, a - DEFAULT_SYMBOL_ANGLE
								- Math.PI);

		drawLine(symbolFrom, symbolTo);
	}

	protected static double getNForDataObjectShape(final Bounds bounds) {
		return Math.min(bounds.getWidth(), bounds.getHeight()) * 0.3;
	}

	public static Shape createDataObjectShape(final Bounds bounds) {
		final double n = getNForDataObjectShape(bounds);
		final Path2D path = new Path2D.Float();

		path.moveTo(bounds.getMinX(), bounds.getMinY());
		path.lineTo(bounds.getMinX(), bounds.getMaxY());
		path.lineTo(bounds.getMaxX(), bounds.getMaxY());
		path.lineTo(bounds.getMaxX(), bounds.getMinY() + n);
		path.lineTo(bounds.getMaxX() - n, bounds.getMinY());
		path.closePath();

		return path;
	}

	public void drawDataObject(final Bounds bounds) {
		final double n = getNForDataObjectShape(bounds);
		graphics.draw(createDataObjectShape(bounds));
		graphics.drawLine((int)(bounds.getMaxX() - n),
				(int)(bounds.getMinY() + n), (int)bounds.getMaxX(),
				(int)(bounds.getMinY() + n));
		graphics.drawLine((int)(bounds.getMaxX() - n),
				(int)(bounds.getMinY() + n), (int)(bounds.getMaxX() - n),
				(int)bounds.getMinY());
	}

	protected static int getNForDataStoreShape(final Bounds bounds) {
		return (int)(bounds.getHeight() / 6.);
	}

	public static Shape createDataStoreShape(final Bounds bounds) {
		final int n = getNForDataStoreShape(bounds);

		final Path2D path = new Path2D.Float();
		path.append(new Arc2D.Double(bounds.getMinX(), bounds.getMaxY() - n,
				bounds.getWidth(), n, 180, 180, Arc2D.OPEN), true);
		path.append(
				new Arc2D.Double(bounds.getMinX(), bounds.getMinY(), bounds
						.getWidth(), n, 0, 180, Arc2D.OPEN), true);
		path.closePath();

		return path;
	}

	public void drawDataStore(final Bounds bounds) {
		final int n = getNForDataStoreShape(bounds);

		graphics.draw(createDataStoreShape(bounds));

		graphics.drawArc((int)bounds.getMinX(), (int)bounds.getMinY(),
				(int)bounds.getWidth(), n, 180, 180);
		graphics.drawArc((int)bounds.getMinX(), (int)bounds.getMinY() + n / 2,
				(int)bounds.getWidth(), n, 180, 180);
		graphics.drawArc((int)bounds.getMinX(), (int)bounds.getMinY() + n,
				(int)bounds.getWidth(), n, 180, 180);
	}

	public void drawText(final Bounds bounds, final String text) {
		final FontMetrics metrics = graphics.getFontMetrics();
		final Rectangle2D textBounds =
				graphics.getFontMetrics().getStringBounds(text, graphics);
		final int x =
				bounds.x + (bounds.width - (int)textBounds.getWidth()) / 2;
		final int textHeight = (int)textBounds.getHeight();
		final int yOffset = (bounds.height - textHeight) / 2;
		final int y = bounds.y + yOffset + metrics.getAscent();
		graphics.drawString(text, x, y);
	}

}
