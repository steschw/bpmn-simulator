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
package bpmn.element.event;

import java.awt.Point;

import javax.swing.Icon;

import bpmn.element.Graphics;
import bpmn.element.TokenFlowElement;
import bpmn.token.InstanceController;

@SuppressWarnings("serial")
public abstract class Event extends TokenFlowElement {

	private InstanceController instanceController;

	private EventDefinition definition;

	public Event(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name);

		setTokenController(tockenController);
	}

	protected void setTokenController(final InstanceController controller) {
		instanceController = controller;
	}

	protected InstanceController getInstanceController() {
		return instanceController;
	}

	public void setDefinition(final EventDefinition definition) {
		this.definition = definition;
	}

	protected EventDefinition getDefinition() {
		return definition; 
	}

	public boolean isTimer() {
		return getDefinition() instanceof TimerEventDefinition;
	}

	public boolean isMessage() {
		return getDefinition() instanceof MessageEventDefinition;
	}

	public boolean isLink() {
		return getDefinition() instanceof LinkEventDefinition;
	}

	public boolean isTerminate() {
		return getDefinition() instanceof TerminateEventDefinition;
	}

	public boolean isPlain() {
		return getDefinition() == null;
	}

	@Override
	protected int getStepCount() {
		return 5;
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fillOval(getElementInnerBounds());
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawOval(getElementInnerBounds());

		if (!isPlain()) {
			paintIcon(g);
		}
	}

	protected abstract Icon getTypeIcon();

	protected void paintIcon(final Graphics g) {
		final Icon icon = getTypeIcon();
		if (icon != null) {
			final Point position = getElementInnerBounds().getCenter();
			position.translate(-icon.getIconWidth() / 2, -icon.getIconHeight() / 2);
			g.drawIcon(icon, position);
		}
	}

	@Override
	public void updateElementLabelPosition() {
		getElementLabel().setCenterTopPosition(getInnerBounds().getCenterBottom());
	}

}
