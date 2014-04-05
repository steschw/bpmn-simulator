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
package com.googlecode.bpmn_simulator.bpmn.model.core.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import com.googlecode.bpmn_simulator.framework.utils.HtmlUtils;

@SuppressWarnings("serial")
public class Expression
		extends JCheckBox
		implements ItemListener {

	private static final Color COLOR_TRUE = new Color(0, 196, 0);
	private static final Color COLOR_FALSE = new Color(0, 0, 0);

	private boolean value;

	public Expression() {
		this(null);
	}

	public Expression(final String text) {
		super((String)null);

		setToolTipText(text);
		setVerticalAlignment(SwingConstants.TOP);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.TOP);
		setFocusable(false);
		setOpaque(false);
		setBorderPaintedFlat(true);
		setExpressionValue(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		setText(text);

		addItemListener(this);
	}

	@Override
	public String getText() {
		final String text = super.getText();
		if (text != null) {
			final StringBuilder html = new StringBuilder("<html>"); //$NON-NLS-1$
			html.append(HtmlUtils.nl2br(text));
			html.append("</html>"); //$NON-NLS-1$
			return html.toString();
		}
		return null;
	}

	protected void setExpressionValue(final boolean value) {
		setForeground(value ? COLOR_TRUE : COLOR_FALSE);
		synchronized (this) {
			this.value = value;
		}
	}

	public synchronized boolean isTrue() {
		return value;
	}

	@Override
	public void itemStateChanged(final ItemEvent event) {
		setExpressionValue(event.getStateChange() == ItemEvent.SELECTED);
	}

}
