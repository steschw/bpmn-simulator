package bpmn.element;

import java.awt.Dimension;
import java.awt.Point;

public class Rectangle extends java.awt.Rectangle {

	private static final long serialVersionUID = 1L;

	public Rectangle(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}

	public Rectangle(final java.awt.Rectangle r) {
		super(r);
	}

	public Rectangle(final Point p, final Dimension d) {
		super(p, d);
	}

	public Rectangle(final Point p) {
		super(p);
	}

	public Rectangle(final Point p1, final Point p2) {
		this(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
	}

	public Point getRightTop() {
		return new Point((int)getMaxX(), (int)getMinY());
	}

	public Point getLeftTop() {
		return new Point((int)getMinX(), (int)getMinY());
	}

	public Point getCenter() {
		return new Point((int)getCenterX(), (int)getCenterY());
	}

	public Point getLeftCenter() {
		return new Point((int)getMinX(), (int)getCenterY());
	}

	public Point getRightCenter() {
		return new Point((int)getMaxX(), (int)getCenterY());
	}

	public void shrinkLeft(final int n) {
		x += n;
		width -= n;
	}

}
