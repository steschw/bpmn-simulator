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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.Icon;

import bpmn.Messages;
import bpmn.instance.Instance;
import bpmn.instance.InstanceManager;
import bpmn.model.core.common.Visualization;
import bpmn.model.process.activities.AbstractContainerActivity;
import bpmn.trigger.Instantiable;
import bpmn.trigger.TriggerCatchingElement;
import bpmn.trigger.Trigger;

@SuppressWarnings("serial")
public final class StartEvent
		extends AbstractEvent
		implements TriggerCatchingElement, Instantiable, MouseListener {

	public static final String ELEMENT_NAME = Messages.getString("startEvent"); //$NON-NLS-1$

	public StartEvent(final String id, final String name,
			final InstanceManager instanceManager) {
		super(id, name, instanceManager);
		addMouseListener(this);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public boolean canTriggerManual() {
		final AbstractContainerActivity containerActivity = getContainerActivity();
		return (getInstanceManager() != null)
				&& (containerActivity != null) && !containerActivity.hasIncoming()
				&& (isPlain() || isTimer() || isConditional()); 
	}

	@Override
	public Collection<Instance> getTriggerDestinationInstances() {
		return getContainerActivity().getInstances();
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		assert trigger.getDestinationInstance() == null;
		final Instance sourceInstance = trigger.getSourceInstance();
		if (sourceInstance == null) {
			getContainerActivity().createInstance(null).addNewToken(this);
		} else {
			getContainerActivity().createCorrelationInstance(sourceInstance).addNewToken(this);
		}
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.EVENT_START);
	}

	@Override
	protected void setInstanceManager(final InstanceManager manager) {
		super.setInstanceManager(manager);
		updateCursor();
	}

	@Override
	public void setContainerActivity(final AbstractContainerActivity parentActivity) {
		super.setContainerActivity(parentActivity);
		updateCursor();
	}

	protected void updateCursor() {
		setCursor(canTriggerManual()
				? new Cursor(Cursor.HAND_CURSOR)
				: Cursor.getDefaultCursor());
	}

	@Override
	public void mouseClicked(final MouseEvent event) {
		if (canTriggerManual()) {
			catchTrigger(new Trigger(null, null));
		}
	}

	@Override
	public void mouseEntered(final MouseEvent event) {
	}

	@Override
	public void mouseExited(final MouseEvent event) {
	}

	@Override
	public void mousePressed(final MouseEvent event) {
	}

	@Override
	public void mouseReleased(final MouseEvent event) {
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), false);
	}

	@Override
	public boolean isInstantiable() {
		return true;
	}

}
