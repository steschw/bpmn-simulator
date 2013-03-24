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
package bpmn.model.core.common.artifacts;

import java.awt.Point;

import bpmn.Graphics;
import bpmn.Messages;
import bpmn.Rectangle;
import bpmn.model.ClickThroughMouseListener;
import bpmn.model.core.common.AbstractFlowElement;
import bpmn.model.core.common.Label;

@SuppressWarnings("serial")
public class TextAnnotation
		extends AbstractFlowElement {

	public static final String ELEMENT_NAME = Messages.getString("textAnnotation"); //$NON-NLS-1$

	private static final int BRACKET_WIDTH = 10;

	private String text;

	public TextAnnotation(final String id) {
		this(id, null);
	}

	public TextAnnotation(final String id, final String text) {
		super(id, null);
		setText(text);
		addMouseListener(new ClickThroughMouseListener());
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public final void setText(final String text) {
		this.text = text;
	}

	public final String getText() {
		return text;
	}

	@Override
	public Label createElementLabel() {
		final String text = getText();
		Label label = null;
		if ((text != null) && !text.isEmpty()) {
			label = new Label(this, text);
			label.setAlignCenter(false);
		}
		return label;
	}

	@Override
	public void updateElementLabelPosition() {
		final Rectangle innerBounds = getInnerBounds();
		innerBounds.shrinkLeft(4);
		getElementLabel().setBounds(innerBounds);
	}

	@Override
	protected int getBorderWidth() {
		return 2;
	}

	@Override
	protected void paintElement(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		final int x = (int)bounds.getMinX();
		final int y = (int)bounds.getMinY();
		g.drawLine(new Point(x, y), new Point(x + BRACKET_WIDTH, y));
		g.drawLine(new Point(x, y), new Point(x, y + bounds.height));
		g.drawLine(new Point(x, y + bounds.height), new Point(x + BRACKET_WIDTH, y + bounds.height));
	}

}
