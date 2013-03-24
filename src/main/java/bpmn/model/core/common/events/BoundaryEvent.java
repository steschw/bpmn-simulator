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
package bpmn.model.core.common.events;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.Icon;

import bpmn.Messages;
import bpmn.instance.Instance;
import bpmn.instance.InstancePopupMenu;
import bpmn.model.ElementRef;
import bpmn.model.core.common.Visualization;
import bpmn.model.process.activities.AbstractActivity;
import bpmn.model.process.activities.Activity;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCatchingElement;

@SuppressWarnings("serial")
public final class BoundaryEvent
		extends AbstractEvent
		implements TriggerCatchingElement, MouseListener {

	public static final String ELEMENT_NAME = Messages.getString("boundaryEvent"); //$NON-NLS-1$

	private final boolean cancelActivity;
	private final ElementRef<AbstractActivity> attachedToRef;

	public BoundaryEvent(final String id, final String name,
			final boolean cancelActivity,
			final ElementRef<AbstractActivity> attachedToRef) {
		super(id, name, null);
		this.cancelActivity = cancelActivity;
		this.attachedToRef = attachedToRef;
		addMouseListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public boolean isInterrupting() {
		return cancelActivity;
	}

	protected ElementRef<AbstractActivity> getAttachedToRef() {
		return attachedToRef;
	}

	public AbstractActivity getAttachedTo() {
		final ElementRef<AbstractActivity> activityRef = getAttachedToRef();
		return ((activityRef == null) || !activityRef.hasElement())
				? null
				: activityRef.getElement();
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.EVENT_BOUNDARY);
	}

	@Override
	public Collection<Instance> getTriggerDestinationInstances() {
		return getAttachedTo().getInstances();
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		final Activity activity = getAttachedTo();
		if (activity != null) {
			final Instance destinationInstance = trigger.getDestinationInstance();
			final Instance parentInstance = destinationInstance.getParent();
			if (parentInstance != null) {
				parentInstance.addNewToken(this);
			}
			if (isInterrupting()) {
				destinationInstance.remove();
			}
		}
	}

	@Override
	protected Stroke getStroke() {
		return isInterrupting()
				? super.getStroke()
				: getVisualization().createStrokeDashed(getBorderWidth());
	}

	@Override
	public int getInnerBorderMargin() {
		return DEFAULT_INNER_MARGIN;
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), false);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		final Collection<Instance> instances = getTriggerDestinationInstances();
		InstancePopupMenu.selectToTrigger(this, this, instances);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public boolean canTriggerManual() {
		return true;
	}

}
