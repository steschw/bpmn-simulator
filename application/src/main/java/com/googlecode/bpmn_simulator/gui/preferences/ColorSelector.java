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
package com.googlecode.bpmn_simulator.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import com.googlecode.bpmn_simulator.gui.Messages;

@SuppressWarnings("serial")
public class ColorSelector
		extends JButton
		implements ActionListener {

	private static final Dimension DEFAULT_SIZE = new Dimension(80, 26);

	private Color selectedColor;

	public ColorSelector() {
		this(null);
	}

	public ColorSelector(final String text) {
		super(text);

		addActionListener(this);

		updateTooltip();

		if (text == null) {
			setPreferredSize(DEFAULT_SIZE);
		}
		setMinimumSize(DEFAULT_SIZE);
	}

	private boolean isSelectedColorTransparent() {
		return getSelectedColor() == null;
	}

	public void setSelectedColor(final Color color) {
		selectedColor = color;
		repaint();
		updateTooltip();
	}

	public Color getSelectedColor() {
		return selectedColor;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Color color = getSelectedColor();
		if (color != null) {
			g.setColor(color);
			final Insets insets = getBorder().getBorderInsets(this);
			g.fillRect(insets.left,  insets.top,
					getWidth() - insets.left - insets.right,
					getHeight() - insets.top - insets.bottom);
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Color color =
				JColorChooser.showDialog(this, null, getSelectedColor());
		if (color != null) {
			setSelectedColor(color);
		}
	}

	protected String getColorToolTipText() {
		final StringBuilder html = new StringBuilder("<html><body>"); //$NON-NLS-1$

		html.append("<b>"); //$NON-NLS-1$
		html.append(getText());
		html.append("</b>"); //$NON-NLS-1$

		if (isSelectedColorTransparent()) {
			html.append("transparent");
		} else {
			final Color color = getSelectedColor();
			final int r = color.getRed();
			final int g = color.getGreen();
			final int b = color.getBlue();
			html.append("<table>"); //$NON-NLS-1$
			html.append("<tr><td>"); //$NON-NLS-1$
			html.append(Messages.getString("Color.hex")); //$NON-NLS-1$
			html.append(":</td><td>"); //$NON-NLS-1$
			html.append(String.format("#%02x%02x%02x", r, g, b)); //$NON-NLS-1$
			html.append("</td></tr>"); //$NON-NLS-1$
			html.append("<tr><td>"); //$NON-NLS-1$
			html.append(Messages.getString("Color.rgb")); //$NON-NLS-1$
			html.append(":</td><td>"); //$NON-NLS-1$
			html.append(String.format("%d, %d, %d", r, g, b)); //$NON-NLS-1$
			html.append("</td></tr>"); //$NON-NLS-1$
			html.append("</table>"); //$NON-NLS-1$
		}

		html.append("</body></html>"); //$NON-NLS-1$
		return html.toString();
	}

	protected void updateTooltip() {
		setToolTipText(getColorToolTipText());
	}

}
