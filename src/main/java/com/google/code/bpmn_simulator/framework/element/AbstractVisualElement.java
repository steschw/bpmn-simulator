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
package com.google.code.bpmn_simulator.framework.element;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.google.code.bpmn_simulator.framework.element.geometry.Bounds;

@SuppressWarnings("serial")
public abstract class AbstractVisualElement
		extends JComponent
		implements VisualElement {

	protected static final int MARGIN = 10;

	private LogicalElement logicalElement;

	public AbstractVisualElement(final LogicalElement element) {
		super();
		setLogicalElement(element);
	}

	private void setLogicalElement(final LogicalElement element) {
		logicalElement = element;
		logicalElement.addVisualElement(this);
	}

	@Override
	public LogicalElement getLogicalElement() {
		return logicalElement;
	}

	public void setInnerBounds(final Bounds bounds) {
		bounds.grow(MARGIN, MARGIN);
		setBounds(bounds);
	}

	@Override
	public Bounds getInnerBounds() {
		final Bounds bounds = new Bounds(getBounds());
		bounds.grow(-MARGIN, -MARGIN);
		return bounds;
	}

	@Override
	public Bounds getOuterBounds() {
		return new Bounds(0, 0, getWidth(), getHeight());
	}

	@Override
	public void paint(final Graphics g) {
		paintElement(new GraphicsLayer((Graphics2D)g));
	}

	protected abstract void paintElement(final GraphicsLayer graphics);

}
