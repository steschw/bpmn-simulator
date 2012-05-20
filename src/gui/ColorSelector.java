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
package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;

@SuppressWarnings("serial")
public class ColorSelector extends JButton implements ActionListener {

	public ColorSelector(final String text) {
		super(text);
		setBorder(BorderFactory.createRaisedBevelBorder());

		addActionListener(this);

		updateTooltip();
	}

	public void setSelectedColor(final Color color) {
		setBackground(color);
		updateTooltip();
	}

	public Color getSelectedColor() {
		return getBackground();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Color color = JColorChooser.showDialog(this, null, getSelectedColor());
		setSelectedColor(color);
	}

	protected void updateTooltip() {
		final StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append("<body>");
		html.append("<b>");
		html.append(getText());
		html.append(":</b>");
		final Color color = getSelectedColor();
		html.append("<table>");
		html.append("<tr>");
		html.append("<td>HEX</td><td>");
		html.append(String.format("%X", color.getRGB()));
		html.append("</td>");
		html.append("</tr>");
		html.append("<tr>");
		html.append("<td>RGB</td><td>");
		html.append(String.format("%d, %d, %d",
				color.getRed(), color.getBlue(), color.getBlue()));
		html.append("</td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</body>");
		html.append("</html>");
		setToolTipText(html.toString());
	}

}
