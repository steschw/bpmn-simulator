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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.JLabel;

public class Label extends JLabel {

	private static final long serialVersionUID = 1L;

	private boolean alignCenter = true;

	private static final Font FONT = new Font("Tahoma", Font.PLAIN, 12); 

	public Label(final String text) {
		super(text, JLabel.LEADING);
		setFont(FONT);
		//setFont(getFont().deriveFont(Font.PLAIN));
	}

	@Override
	public void setText(final String text) {
		/*
		 * Scheinbar wird keine Aktualisierung durchgeführt,
		 * wenn der neu zu setzende Text dem alten entspricht.
		 * Die Methode setAlignCenter benötigt dieses Verhalten aber.
		 */
		super.setText("");
		super.setText(text);
	}

	public final void setAlignCenter(final boolean center) {
		this.alignCenter = center;
		setText(super.getText());
	}

	public final boolean isAlignCenter() {
		return alignCenter;
	}

	@Override
	public String getText() {
		final StringBuffer text = new StringBuffer("<html>");
		final boolean center = isAlignCenter();
		if (center) {
			text.append("<center>");
		}
		text.append(super.getText().replaceAll("\n", "<br>"));
		if (center) {
			text.append("</center>");
		}
		text.append("</html>");
		return text.toString();
	}

	public void setMaxWidth(final int width) {
		final Dimension maximumSize = getMaximumSize();
		maximumSize.width = width;
		setMaximumSize(maximumSize);
	}

	public void setCenterPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x - (size.width / 2), center.y - (size.height / 2));
	}

	public void setCenterTopPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x - (size.width / 2), center.y);
	}

	public void setLeftTopPosition(final Point center) {
		final Dimension size = getPreferredSize();
		setSize(size);
		setLocation(center.x, center.y);
	}

}
