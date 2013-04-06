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
package com.google.code.bpmn_simulator.framework.element.geometry;

import java.awt.Dimension;
import java.awt.Point;

@SuppressWarnings("serial")
public class Bounds
		extends java.awt.Rectangle {

	public Bounds(final int x, final int y, final int width, final int height) {
		super(x, y, width, height);
	}

	public Bounds(final java.awt.Rectangle r) {
		super(r);
	}

	public Bounds(final Point p, final Dimension d) {
		super(p, d);
	}

	public Bounds(final Point p) {
		super(p);
	}

	public Bounds(final Point center, final int size) {
		super(center.x - size, center.y - size, size + size, size + size);
	}

	public int min() {
		return Math.min(width, height);
	}

	public Point getLeftTop() {
		return new Point((int)getMinX(), (int)getMinY());
	}

	public Point getLeftCenter() {
		return new Point((int)getMinX(), (int)getCenterY());
	}

	public Point getLeftBottom() {
		return new Point((int)getMinX(), (int)getMaxY());
	}

	public Point getCenter() {
		return new Point((int)getCenterX(), (int)getCenterY());
	}

	public Point getCenterTop() {
		return new Point((int)getCenterX(), (int)getMinY());
	}

	public Point getCenterBottom() {
		return new Point((int)getCenterX(), (int)getMaxY());
	}

	public Point getRightTop() {
		return new Point((int)getMaxX(), (int)getMinY());
	}

	public Point getRightCenter() {
		return new Point((int)getMaxX(), (int)getCenterY());
	}

	public Point getRightBottom() {
		return new Point((int)getMaxX(), (int)getMaxY());
	}

	public void shrinkLeft(final int n) {
		x += n;
		width -= n;
	}

	public void shrink(final int l, final int r, final int t, final int b) {
		x += l;
		y += t;
		width -= l + r;
		height -= t + b;
	}

	public void shrinkHalf() {
		final int leftRight = (int)getWidth() / 4;
		final int topBottom = (int)getHeight() / 4;
		shrink(leftRight, leftRight, topBottom, topBottom);
	}

}
