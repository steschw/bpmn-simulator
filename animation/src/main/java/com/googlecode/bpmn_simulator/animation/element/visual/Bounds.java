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
package com.googlecode.bpmn_simulator.animation.element.visual;

public class Bounds {

	private final int x;
	private final int y;

	private final int width;
	private final int height;

	public Bounds(final int x, final int y,
			final int width, final int height) {
		super();
		assert x >= 0;
		this.x = x;
		assert y >= 0;
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

	public Point getCenter() {
		return new Point((int) getCenterX(), (int) getCenterY());
	}

	public Bounds enlarge(final int lr, final int tb) {
		return new Bounds(getX() - lr, getY() - tb,
				getWidth() + (2 * lr), getHeight() + (2 * tb));
	}

	public Bounds enlarge(final int n) {
		return enlarge(n, n);
	}

	public Bounds scaleSize(final float factor) {
		final int lr = Math.round(((getWidth() * factor) - getWidth()) / 2.f);
		final int tb = Math.round(((getHeight() * factor) - getHeight()) / 2.f);
		return enlarge(lr, tb);
	}

	public Bounds shrink(final int lr, final int tb) {
		return enlarge(-lr, -tb);
	}

	public Bounds shrink(final int n) {
		return shrink(n, n);
	}

	public Bounds top(final int size) {
		return new Bounds(getMinX(), getMinY(), getWidth(), size);
	}

	public Bounds bottom(final int size) {
		return new Bounds(getMinX(), getMaxY() - size, getWidth(), size);
	}

	public Bounds centerBottom(final int width, final int height) {
		return new Bounds(
				(int) Math.round(getCenterX() - width / 2.),
				getMaxY() - height,
				width, height);
	}

}
