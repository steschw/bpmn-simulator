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
package bpmn.element.activity.task;

import java.awt.Color;
import java.awt.Point;

import javax.swing.Icon;

import bpmn.element.Graphics;
import bpmn.element.Label;
import bpmn.element.Rectangle;
import bpmn.element.VisualConfig;
import bpmn.element.activity.Activity;

public class Task extends Activity {

	private static final long serialVersionUID = 1L;

	private static final int TYPEICON_MARGIN = 6;

	private static final int ARC_LENGTH = 10;

	public Task(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected int getStepCount() {
		return 50;
	}

	@Override
	protected Color getElementBackgroundColor() {
		return getVisualConfig().getBackground(VisualConfig.Element.TASK);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	protected Icon getTypeIcon() {
		return null;
	}

	public void paintTypeIcon(final Graphics g) {
		final Icon typeIcon = getTypeIcon();
		if (typeIcon != null) {
			final Rectangle innerBounds = getElementInnerBounds();
			final Point position = innerBounds.getLeftTop();
			position.translate(TYPEICON_MARGIN, TYPEICON_MARGIN);
			g.drawIcon(typeIcon, position);
		}
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
		paintTypeIcon(g);
	}

	@Override
	protected void updateElementLabelPosition() {
		final Label label = getElementLabel();
		if (label != null) {
			final Rectangle bounds = getElementInnerBounds();
			label.setMaxWidth(bounds.width);
		}
		super.updateElementLabelPosition();
	}

}
