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
package com.google.code.bpmn_simulator.bpmn.model.core.common.events;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.Icon;

import com.google.code.bpmn_simulator.bpmn.Messages;
import com.google.code.bpmn_simulator.bpmn.trigger.InstantiableNotifiySource;
import com.google.code.bpmn_simulator.bpmn.trigger.StoringTriggerCatchingElement;
import com.google.code.bpmn_simulator.bpmn.trigger.Trigger;
import com.google.code.bpmn_simulator.bpmn.trigger.TriggerCollection;
import com.google.code.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.gui.InstancePopupMenu;
import com.google.code.bpmn_simulator.framework.instance.Instance;
import com.google.code.bpmn_simulator.framework.instance.InstanceListener;
import com.google.code.bpmn_simulator.framework.token.Token;



@SuppressWarnings("serial")
public final class IntermediateCatchEvent
		extends AbstractIntermediateEvent
		implements StoringTriggerCatchingElement, InstantiableNotifiySource,
				MouseListener, InstanceListener {

	public static final String ELEMENT_NAME = Messages.getString("intermediateCatchEvent"); //$NON-NLS-1$

	private final TriggerCollection triggers = new TriggerCollection();

	public IntermediateCatchEvent(final String id, final String name) {
		super(id, name);
		addMouseListener(this);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public boolean isInstantiableNotifying() {
		return areAllIncommingFlowElementsInstantiableNotifyTargets();
	}

	@Override
	public Trigger getFirstTrigger(final Instance instance) {
		return triggers.first(instance);
	}

	@Override
	public void removeFirstTrigger(final Instance instance) {
		triggers.removeFirst(instance);
		repaint();
	}

	@Override
	public boolean canTriggerManual() {
		return isPlain() || isTimer() || isConditional();
	}

	protected void updateCursor() {
		if (canTriggerManual()) {
			setCursor(
					getInnerTokens().isEmpty()
							? Cursor.getDefaultCursor()
							: Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public Collection<Instance> getTriggerDestinationInstances() {
		return getContainerActivity().getInstances();
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		final Instance destinationInstance = trigger.getDestinationInstance();
		if (isInstantiableNotifying()) {
			notifyInstantiableIncomingFlowElements(this, trigger);
		} else if (getBehavior().getKeepTriggers()
				&& (destinationInstance != null) && hasIncoming()) {
			triggers.add(trigger);
			destinationInstance.addInstanceListener(this);
		} else {
			final EventDefinition definition = getDefinition();
			if (definition == null) {
				if (destinationInstance == null) {
					passAllTokenToAllNextElements();
				} else {
					passFirstInstanceTokenToAllNextElements(destinationInstance);
				}
			} else {
				definition.catchTrigger(trigger);
			}
		}
		repaint();
	}

	@Override
	public void instanceAdded(final Instance instance) {
	}

	@Override
	public void instanceRemoved(final Instance instance) {
		triggers.removeInstanceTriggers(instance);
		repaint();
	}

	@Override
	protected boolean canForwardTokenToNextElement(final Token token) {
		return isGatewayCondition()
				|| (triggers.first(token.getInstance()) != null);
	}

	@Override
	protected void tokenForwardToNextElement(final Token token, final Instance instance) {
		super.tokenForwardToNextElement(token, instance);

		triggers.removeFirst(token.getInstance());
	}

	@Override
	public void tokenEnter(final Token token) {
		super.tokenEnter(token);
		updateCursor();
	}

	@Override
	public void tokenExit(final Token token) {
		super.tokenExit(token);
		updateCursor();
	}


	@Override
	public void mouseClicked(final MouseEvent e) {
		if (canTriggerManual()) {
			if (isInstantiableNotifying()) {
				this.catchTrigger(new Trigger(null, null));
			} else {
				InstancePopupMenu.selectToTrigger(this, this, getContainerActivity().getInstances());
			}
		}
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
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), false);
	}

	@Override
	protected void paintElement(final GraphicsLayer g) {
		super.paintElement(g);

		triggers.paint(g, getElementInnerBounds().getRightTop());
	}

}
