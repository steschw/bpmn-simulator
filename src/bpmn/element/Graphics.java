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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Graphics {

	private static final double RAD_FULL = 2. * Math.PI;
	private static final double RAD_30 = RAD_FULL / 12.;

	private static final double CONNECTING_SYMBOL_LENGTH = 12.;

	private static final RenderingHints QUALITY = new RenderingHints(null);

	private static boolean antialiasing = true; 

	private final Graphics2D graphics;

	private Stroke storedStroke;
	private Paint storedPaint;
	private Font storedFont;

	static {
		QUALITY.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		QUALITY.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		QUALITY.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		//QUALITY.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
	}

	public static void setAntialiasing(final boolean antialiasing) {
		Graphics.antialiasing = antialiasing;
	}

	public static boolean isAntialiasing() {
		return Graphics.antialiasing;
	}


	public static Icon loadIcon(final String filename) {
		final URL url = Graphics.class.getResource(filename);
		return (url == null) ? null : new ImageIcon(url); 
	}

	public Graphics(final Graphics2D graphics) {
		super();
		this.graphics = graphics;
		initGraphics();
	}

	protected final void initGraphics() {
		if (isAntialiasing()) {
			graphics.addRenderingHints(QUALITY);
		}
	}

	public final void push() {
		storedStroke = graphics.getStroke();
		storedPaint = graphics.getPaint();
		storedFont = graphics.getFont();
	}

	public final void pop() {
		graphics.setStroke(storedStroke);
		graphics.setPaint(storedPaint);
		graphics.setFont(storedFont);
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

	protected final int getMultilineTextHeight(final Rectangle bounds, final String text) {
		int height = 0;
		final AttributedString attributetString = new AttributedString(text);
		final AttributedCharacterIterator characterIterator = attributetString.getIterator();
		final FontRenderContext fontRenderContext = graphics.getFontRenderContext();
		final LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
		TextLayout textLayout;
		while (measurer.getPosition() < characterIterator.getEndIndex()) {
			textLayout = measurer.nextLayout(bounds.width);
			height += textLayout.getAscent();
		}
		return height;
	}

	/*
	 * Text beginnt oben links
	 */
	public void drawMultilineText(final Rectangle bounds, final String text, final boolean alignCenter, final boolean alignMiddle) {
		if ((text != null) && !text.isEmpty()) {
			int x = bounds.x;
			int y = bounds.y;

			if (alignMiddle) {
				y = bounds.y + (int)((bounds.height - getMultilineTextHeight(bounds, text)) / 2.);
			}

			final AttributedString attributetString = new AttributedString(text);
			final AttributedCharacterIterator characterIterator = attributetString.getIterator();
			final FontRenderContext fontRenderContext = graphics.getFontRenderContext();
			final LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, fontRenderContext);
			TextLayout textLayout;
			while (measurer.getPosition() < characterIterator.getEndIndex()) {
				textLayout = measurer.nextLayout(bounds.width);
				y += textLayout.getAscent();
				if (alignCenter) {
					x = bounds.x + (int)((bounds.getWidth() - textLayout.getBounds().getWidth()) /2.);
				}
				textLayout.draw(graphics, x, y);
				y += textLayout.getDescent() + textLayout.getLeading();
			} 
		}
	}

	/*
	 * Text beginnt unten links
	 */
	public void drawMultilineTextVertical(final Rectangle bounds, final String text, final boolean alignCenter, final boolean alignMiddle) {
		final AffineTransform transformation = graphics.getTransform();

		graphics.translate(bounds.getMinX(), bounds.getMaxY());
		graphics.rotate(-Math.PI/2., 0, 0);

		final Rectangle rotatedRect = new Rectangle(0, 0, (int)bounds.getHeight(), (int)bounds.getWidth());
		drawMultilineText(rotatedRect, text, alignCenter, alignMiddle);

		graphics.setTransform(transformation);
	}

	public void drawIcon(final Icon icon, final Point position) {
		icon.paintIcon(null, graphics, position.x, position.y);
//		graphics.drawImage(icon.getImage(), position.x, position.y, null);
	}

	public void draw(final Shape shape) {
		graphics.draw(shape);
	}

	public void fill(final Shape shape) {
		graphics.fill(shape);
	}

	public void drawRoundRect(final Rectangle rect, final int arcWidth, final int arcHeight) {
		graphics.drawRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcHeight);
	}

	public void fillRoundRect(final Rectangle rect, final int arcWidth, final int arcHeight) {
		graphics.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcHeight);
	}

	public void fillOval(final Rectangle size) {
		graphics.fillOval(size.x, size.y, size.width, size.height);
	}

	public void drawOval(final Rectangle size) {
		graphics.drawOval(size.x, size.y, size.width, size.height);
	}

	public void fillRect(final Rectangle rect) {
		graphics.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	public void drawRect(final Rectangle rect) {
		graphics.drawRect(rect.x, rect.y, rect.width, rect.height);
	}

	protected static Polygon createDiamond(final Rectangle size) {
		final Polygon polygon = new Polygon();
		polygon.addPoint((int)size.getMinX(), (int)size.getCenterY());
		polygon.addPoint((int)size.getCenterX(), (int)size.getMinY());
		polygon.addPoint((int)size.getMaxX(), (int)size.getCenterY());
		polygon.addPoint((int)size.getCenterX(), (int)size.getMaxY());
		return polygon;
	}

	public void fillDiamond(final Rectangle size) {
		fill(createDiamond(size));
	}

	public void drawDiamond(final Rectangle size) {
		draw(createDiamond(size));
	}

	public void drawCross(final Rectangle rect, final boolean rotated) {
		if (rotated) {
			graphics.drawLine((int)rect.getMinX(), (int)rect.getMinY(), (int)rect.getMaxX(), (int)rect.getMaxY());
			graphics.drawLine((int)rect.getMinX(), (int)rect.getMaxY(), (int)rect.getMaxX(), (int)rect.getMinY());
		} else {
			graphics.drawLine((int)rect.getMinX(), (int)rect.getCenterY(), (int)rect.getMaxX(), (int)rect.getCenterY());
			graphics.drawLine((int)rect.getCenterX(), (int)rect.getMinY(), (int)rect.getCenterX(), (int)rect.getMaxY());
		}
	}

	public void drawLine(final Point from, final Point to) {
		graphics.drawLine(from.x, from.y, to.x, to.y);
	}

	public static final double getAngle(final Point from, final Point to) {
		return Math.atan2(to.x - from.x, to.y - from.y);
	}

	public static Point polarToCartesian(final Point orgin,
			final double radius, final double angle) {
		final int x = (int)Math.round(radius * Math.sin(angle));
		final int y = (int)Math.round(radius * Math.cos(angle));
		return new Point(orgin.x + x, orgin.y + y);
	}

	public static GeneralPath createArrowPath(final Point from, final Point to) {
		return createArrowPath(from, to, RAD_30, 10.);
	}

	public static GeneralPath createArrowPath(final Point from, final Point to,
			final double d, final double length) {
		final GeneralPath path = new GeneralPath();
		final double angle = getAngle(to, from);
		final Point point1 = polarToCartesian(to, length, angle - d);
		path.moveTo(point1.x, point1.y);
		path.lineTo(to.x, to.y);
		final Point point2 = polarToCartesian(to, length, angle + d);
		path.lineTo(point2.x, point2.y);
		return path;
	}

	public void fillArrow(final Point from, final Point to) {
		graphics.fill(createArrowPath(from, to));
	}

	public void drawArrow(final Point from, final Point to) {
		final double angle = getAngle(to, from);
		drawLine(to, polarToCartesian(to, 10., angle - RAD_30));
		drawLine(to, polarToCartesian(to, 10., angle + RAD_30));
	}

	protected static Polygon createStar(final Rectangle size, final int corners) {
		final Polygon polygon = new Polygon();
		final Point center = new Point(size.x + size.width / 2, size.y + size.height / 2);
		final double r = size.width / 2.;
		Point point = null;
		for (int i = 0; i < corners; ++i) {
			point = polarToCartesian(center, r, (RAD_FULL / corners) * i - (RAD_FULL / corners) / 2.);
			polygon.addPoint(point.x, point.y);
			point = polarToCartesian(center, r * 0.5, (RAD_FULL / corners) * i);
			polygon.addPoint(point.x, point.y);
		}
		return polygon;
	}

	public void drawStar(final Rectangle size, final int corners) {
		graphics.drawPolygon(createStar(size, corners));
	}

	public void fillStar(final Rectangle size, final int corners) {
		graphics.fillPolygon(createStar(size, corners));
	}

	protected static Polygon createConditionalSymbol(final Point orgin, final double a) {
		final Polygon polygon = new Polygon();
		polygon.addPoint(orgin.x, orgin.y);
		Point to = polarToCartesian(orgin, CONNECTING_SYMBOL_LENGTH, a - RAD_30);
		polygon.addPoint(to.x, to.y);
		to = polarToCartesian(to, CONNECTING_SYMBOL_LENGTH, a + RAD_30);
		polygon.addPoint(to.x, to.y);
		to = polarToCartesian(orgin, CONNECTING_SYMBOL_LENGTH, a + RAD_30);
		polygon.addPoint(to.x, to.y);
		return polygon;
	}

	public void drawConditionalSymbol(final Point from, final Point to) {
		final Polygon symbol = createConditionalSymbol(from, getAngle(from, to));

		graphics.setPaint(Color.WHITE);
		graphics.fill(symbol);
		graphics.setPaint(Color.BLACK);
		graphics.draw(symbol);
	}

	public void drawDefaultSymbol(final Point from, final Point to) {
		final double a = getAngle(from, to);
		final Point orgin = polarToCartesian(from, 6., a);

		final double angle = Math.PI / 1.5;
		final Point symbolFrom = polarToCartesian(orgin, CONNECTING_SYMBOL_LENGTH / 2., a - angle);
		final Point symbolTo = polarToCartesian(symbolFrom, CONNECTING_SYMBOL_LENGTH, a - angle - Math.PI);

		drawLine(symbolFrom, symbolTo);
	}

}
