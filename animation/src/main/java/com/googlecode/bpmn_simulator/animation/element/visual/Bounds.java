package com.googlecode.bpmn_simulator.animation.element.visual;

public class Bounds {

	private final int x;
	private final int y;

	private final int width;
	private final int height;

	public Bounds(final int x, final int y,
			final int width, final int height) {
		super();
		this.x = x;
		this.y = y;
		assert width > 0;
		this.width = width;
		assert height > 0;
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getMinX() {
		return getX();
	}

	public float getCenterX() {
		return getX() + (getWidth() / 2.f);
	}

	public int getMaxX() {
		return getX() + getWidth();
	}

	public int getMinY() {
		return getY();
	}

	public float getCenterY() {
		return getY() + (getHeight() / 2.f);
	}

	public int getMaxY() {
		return getY() + getHeight();
	}

	public Bounds enlarge(final int n) {
		return new Bounds(getX() - n, getY() - n,
				getWidth() + (2 * n), getHeight() + (2 * n));
	}

}
