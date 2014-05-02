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
package com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;

import javax.swing.Icon;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.InstancesListener;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Label;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Visualization;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.AbstractActivity;
import com.googlecode.bpmn_simulator.bpmn.trigger.StoringTriggerCatchingElement;
import com.googlecode.bpmn_simulator.bpmn.trigger.Trigger;
import com.googlecode.bpmn_simulator.bpmn.trigger.TriggerCollection;
import com.googlecode.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.googlecode.bpmn_simulator.framework.element.visual.geometry.Bounds;



@SuppressWarnings("serial")
public class Task
		extends AbstractActivity
		implements StoringTriggerCatchingElement, InstancesListener {

	public static final String ELEMENT_NAME = Messages.getString("task"); //$NON-NLS-1$

	private static final int TYPEICON_MARGIN = 6;

	private static final int ARC_LENGTH = 10;

	private final TriggerCollection messageTriggers = new TriggerCollection();

	public Task(final String id, final String name) {
		super(id, name);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	protected int getStepCount() {
		return 50;
	}

	@Override
	public Trigger getFirstTrigger(final Instance instance) {
		return messageTriggers.first(instance);
	}

	@Override
	public void removeFirstTrigger(final Instance instance) {
		messageTriggers.removeFirst(instance);
		repaint();
	}

	@Override
	public void instanceAdded(final Instance instance) {
	}

	@Override
	public void instanceRemoved(final Instance instance) {
		messageTriggers.removeInstanceTriggers(instance);
		repaint();
	}

	@Override
	public boolean canTriggerManual() {
		return false;
	}

	@Override
	public Collection<Instance> getTriggerDestinationInstances() {
		return getContainerActivity().getInstances();
	}

	@Override
	protected void forwardTokenFromIncoming(final Token token) {
		super.forwardTokenFromIncoming(token);

		token.assignToNewChildInstance();

		getModel().sendMessages(this, token.getInstance());
	}

	@Override
	protected void forwardTokenFromInner(final Token token) {
		messageTriggers.removeFirst(token.getInstance());

		final Instance instance = token.getInstance();
		token.assignToParentInstance();
		instance.remove();

		super.forwardTokenFromInner(token);
	}

	@Override
	protected boolean canForwardTokenToOutgoing(final Token token) {
		return super.canForwardTokenToOutgoing(token)
				&& !waitsForMessage(token);
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.TASK);
	}

	@Override
	protected void paintBackground(final GraphicsLayer g) {
		g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	protected Icon getTypeIcon() {
		return null;
	}

	public void paintTypeIcon(final GraphicsLayer g, final Icon icon, final Point position) {
		g.drawIcon(icon, position);
	}

	@Override
	protected void paintElement(final GraphicsLayer g) {
		final Bounds innerBounds = getElementInnerBounds();

		g.drawRoundRect(innerBounds, ARC_LENGTH, ARC_LENGTH);

		final Icon typeIcon = getTypeIcon();
		if (typeIcon != null) {
			final Point position = innerBounds.getLeftTop();
			position.translate(TYPEICON_MARGIN, TYPEICON_MARGIN);
			paintTypeIcon(g, typeIcon, position);
		}

		messageTriggers.paint(g, getElementInnerBounds().getRightTop());
	}

	@Override
	protected void updateElementLabelPosition() {
		final Label label = getElementLabel();
		if (label != null) {
			final Bounds bounds = getElementInnerBounds();
			label.setMaxWidth(bounds.width);
		}
		super.updateElementLabelPosition();
	}

	private boolean canReceiveMessages() {
		for (final Collaboration collaboration : getModel().getCollaborations()) {
			if (collaboration.hasMessageTarget(this)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		if (getBehavior().getKeepTriggers() && !areAllIncommingFlowElementsInstantiableNotifyTargets()) {
			messageTriggers.add(trigger);
			trigger.getDestinationInstance().addInstanceListener(this);
		} else {
			passFirstInstanceTokenToAllNextElements(trigger.getDestinationInstance());
		}
		repaint();
	}

	private boolean hasStoredTrigger(final Instance instance) {
		return messageTriggers.first(instance) != null;
	}

	protected boolean waitsForMessage(final Token token) {
		return canReceiveMessages()
				&& !hasStoredTrigger(token.getInstance());
	}

}
