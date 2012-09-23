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
package bpmn.model.process.data;

import java.awt.Color;
import java.awt.Point;

import javax.swing.Icon;

import bpmn.Graphics;
import bpmn.Messages;
import bpmn.Rectangle;
import bpmn.model.core.common.AbstractFlowElement;
import bpmn.model.core.common.Visualization;

@SuppressWarnings("serial")
public class DataObject
		extends AbstractFlowElement {

	public static final String ELEMENT_NAME = Messages.getString("dataObject"); //$NON-NLS-1$

	private boolean isCollection;

	public DataObject(final String id, final String name) {
		super(id, name);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public void setCollection(final boolean isCollection) {
		this.isCollection = isCollection;
	}

	public boolean isCollection() {
		return isCollection;
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.DATA_OBJECT);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fill(Graphics.createDataObjectShape(getElementInnerBounds()));
	}

	@Override
	protected void paintElement(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		g.drawDataObject(bounds);

		if (isCollection()) {
			final Icon icon = getVisualization().getIcon(Visualization.ICON_COLLECTION);
			final Point position = bounds.getCenterBottom();
			position.translate(
					-icon.getIconWidth() / 2,
					-icon.getIconHeight());
			g.drawIcon(icon, position);
		}
	}

}
