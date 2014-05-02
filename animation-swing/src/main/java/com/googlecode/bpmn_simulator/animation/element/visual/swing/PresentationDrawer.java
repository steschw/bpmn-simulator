package com.googlecode.bpmn_simulator.animation.element.visual.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoint;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoints;
import com.googlecode.bpmn_simulator.animation.token.Token;

public class PresentationDrawer {

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

	public static Image loadImage(final String name) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = ClassLoader.getSystemClassLoader();
		}
		if (cl != null) {
			final URL url = cl.getResource(name);
			if (url != null) {
				try {
					return ImageIO.read(url);
				} catch (IOException e) {
				}
			}
			
		}
		return null;
	}

	public synchronized void init(final Graphics2D g) {
		g.setRenderingHints(renderingHints);
	}

	public void drawImage(final Graphics g, final Image image, final Bounds bounds) {
		g.drawImage(image, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), null);
	}

	public void drawLine(final Graphics g, final Waypoint from, final Waypoint to) {
		g.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
	}

	public void drawPolyline(final Graphics g, final Waypoints points) {
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
		final float centerX = bounds.getCenterX();
		final float centerY = bounds.getCenterY();
		final Path2D diamond = new Path2D.Float();
		diamond.moveTo(bounds.getMinX(), centerY);
		diamond.lineTo(centerX, bounds.getMinY());
		diamond.lineTo(bounds.getMaxX(), centerY);
		diamond.lineTo(centerX, bounds.getMaxY());
		g.draw(diamond);
	}

	public void drawToken(final Graphics g, final Token token, final int centerX, final int centerY) {
		
	}

}
