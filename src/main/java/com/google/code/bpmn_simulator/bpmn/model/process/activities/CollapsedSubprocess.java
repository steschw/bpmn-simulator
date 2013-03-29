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
package com.google.code.bpmn_simulator.bpmn.model.process.activities;

import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;

import javax.swing.Icon;

import com.google.code.bpmn_simulator.bpmn.Messages;
import com.google.code.bpmn_simulator.bpmn.model.core.common.AbstractFlowElement;
import com.google.code.bpmn_simulator.bpmn.model.core.common.Visualization;
import com.google.code.bpmn_simulator.framework.Graphics;
import com.google.code.bpmn_simulator.framework.Rectangle;


@SuppressWarnings("serial")
public class CollapsedSubprocess
		extends AbstractFlowElement {

	public static final String ELEMENT_NAME = Messages.getString("collapsedSubprocess"); //$NON-NLS-1$

	protected static final int ARC_LENGTH = 10;

	private final Subprocess expandedSubprocess;

	public CollapsedSubprocess(final Subprocess expandedProcess) {
		super(expandedProcess.getId(), expandedProcess.getName());
		this.expandedSubprocess = expandedProcess;
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	protected Color getElementDefaultBackground() {
		return expandedSubprocess.getElementDefaultBackground();
	}

	@Override
	public int getInnerBorderMargin() {
		return expandedSubprocess.getInnerBorderMargin();
	}

	@Override
	public Color getForeground() {
		return (expandedSubprocess == null)
				? super.getForeground()
				: expandedSubprocess.getForeground();
	}

	@Override
	protected Stroke getStroke() {
		return expandedSubprocess.getStroke();
	}

	@Override
	protected void paintBackground(final Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	@Override
	protected void paintElement(final Graphics g) {

		final Rectangle innerBounds = getElementInnerBounds();
		g.drawRoundRect(innerBounds, ARC_LENGTH, ARC_LENGTH);

		final int innerMargin = getInnerBorderMargin();
		if (innerMargin > 0) {
			innerBounds.grow(-innerMargin, -innerMargin);
			g.drawRoundRect(innerBounds, ARC_LENGTH, ARC_LENGTH);
		}

		drawSymbol(g);
	}

	protected void drawSymbol(final Graphics g) {
		final Icon icon = getVisualization().getIcon(Visualization.ICON_COLLAPSED);
		if (icon != null) {
			final Point position = getElementInnerBounds().getCenterBottom();
			position.translate(
					-(icon.getIconWidth() / 2),
					-(icon.getIconHeight() + getInnerBorderMargin()));
			g.drawIcon(icon, position);
		}
	}

	@Override
	protected void paintTokens(final Graphics g) {
		super.paintTokens(g);

		expandedSubprocess.getIncomingTokens().paintVertical(g, getElementInnerBounds().getLeftCenter());
		expandedSubprocess.getAllInnerTokens().paintHorizontal(g, getElementInnerBounds().getCenterTop());
		expandedSubprocess.getOutgoingTokens().paintVertical(g, getElementInnerBounds().getRightCenter());
	}

}
