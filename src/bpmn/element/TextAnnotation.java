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

import java.awt.Point;

public class TextAnnotation extends Artifact {

	private static final long serialVersionUID = 1L;

	private static final int MARGIN = 10;

	private String text = null;

	public TextAnnotation(final String id, final String text) {
		super(id);
		setText(text);
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
		if ((text != null) && !text.isEmpty()) {
			final Label label = new Label(text);
			initLabel(label);
			return label;
		}
		return null;
	}

	@Override
	protected void initLabel(final Label label) {
		label.setAlignCenter(false);
		final Rectangle innerBounds = getInnerBounds();
		innerBounds.shrinkLeft(4);
		label.setBounds(innerBounds);
	}

	@Override
	protected void paintElement(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		final int x = (int)bounds.getMinX();
		final int y = (int)bounds.getMinY();
		g.drawLine(new Point(x, y), new Point(x + MARGIN, y));
		g.drawLine(new Point(x, y), new Point(x, y + bounds.height));
		g.drawLine(new Point(x, y + bounds.height), new Point(x + MARGIN, y + bounds.height));
	}

}
