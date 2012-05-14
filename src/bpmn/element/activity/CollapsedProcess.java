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
package bpmn.element.activity;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;

import bpmn.element.FlowElement;
import bpmn.element.Graphics;
import bpmn.element.VisualConfig;
import bpmn.token.Instance;
import bpmn.token.Token;

@SuppressWarnings("serial")
public class CollapsedProcess extends FlowElement {

	protected static final int TOKEN_MARGIN = 5;

	private static final int ARC_LENGTH = 10;

	private final Collection<Instance> instances = new ArrayList<Instance>(); 

	public CollapsedProcess(final ExpandedProcess expandedProcess) {
		super(expandedProcess.getId(), expandedProcess.getName());
	}

	public void addInstance(final Instance instance) {
		assert !instances.contains(instance);
		instances.add(instance);
		repaint();
	}

	public void removeInstance(final Instance instance) {
		assert instances.contains(instance);
		instances.remove(instance);
		repaint();
	}

	protected Collection<Instance> getInstances() {
		return instances;
	}

	@Override
	public Color getForeground() {
		final Collection<Instance> instances = getInstances();
		if ((instances != null) && !instances.isEmpty()) {
			return Token.HIGHLIGHT_COLOR;
		}
		return super.getForeground();
	}

	@Override
	protected void paintBackground(final Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getProcess().getElementDefaultBackground();
	}

	@Override
	protected void paintElement(final Graphics g) {

		g.drawRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);

		drawSymbol(g);
	}

	protected void drawSymbol(final Graphics g) {
		final Icon icon = getVisualConfig().getIcon(VisualConfig.ICON_COLLAPSED);
		if (icon != null) {
			final Point position = getElementInnerBounds().getCenterBottom();
			position.translate(-(icon.getIconWidth() / 2), -icon.getIconHeight());
			g.drawIcon(icon, position);
		}
	}

	@Override
	protected void paintTokens(final Graphics g) {
		super.paintTokens(g);

		final Point point = getElementInnerBounds().getRightTop();
		for (Instance instance : getInstances()) {
			instance.paint(g, point);
			point.translate(-TOKEN_MARGIN, 0);
		}
	}

}
