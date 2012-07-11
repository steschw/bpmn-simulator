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
import java.awt.Point;

import bpmn.Graphics;

@SuppressWarnings("serial")
public final class Message
		extends AbstractFlowElement {

	public Message(final String id, final String name) {
		super(id, name);
		setElementBackground(Color.WHITE);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		final Rectangle bounds = getElementInnerBounds();
		g.fillRect(bounds);
	}

	@Override
	protected void paintElement(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		g.drawRect(bounds);
		final Point center = bounds.getCenter();
		g.drawLine(bounds.getLeftTop(), center);
		g.drawLine(bounds.getRightTop(), center);
	}

}
