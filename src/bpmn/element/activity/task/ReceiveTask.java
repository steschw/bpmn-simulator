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
package bpmn.element.activity.task;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;

import bpmn.Graphics;
import bpmn.element.ElementRef;
import bpmn.element.Message;
import bpmn.element.Visualization;
import bpmn.instance.Instance;
import bpmn.instance.InstanceListener;
import bpmn.instance.InstancePopupMenu;
import bpmn.token.Token;
import bpmn.trigger.StoringTriggerCatchElement;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCollection;

@SuppressWarnings("serial")
public final class ReceiveTask
		extends AbstractMessageTask
		implements StoringTriggerCatchElement, MouseListener, InstanceListener {

	private TriggerCollection triggers = new TriggerCollection();

	public ReceiveTask(final String id, final String name,
			final ElementRef<Message> messageRef) {
		super(id, name, messageRef);
		addMouseListener(this);
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
	protected Icon getTypeIcon() {
		return getVisualization().getIcon(Visualization.ICON_RECEIVE);
	}

	@Override
	public boolean canTriggerManual() {
		return false;
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		if (getBehavior().getKeepTriggers()) {
			triggers.add(trigger);
			trigger.getDestinationInstance().addInstanceListener(this);
		}
		notifyTriggerNotifyEvents(this, trigger);
		passFirstInstanceTokenToAllNextElements(trigger.getDestinationInstance());
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
	protected boolean canForwardTokenToOutgoing(final Token token) {
		return isGatewayCondition()
				|| (triggers.first(token.getInstance()) != null);
	}

	@Override
	protected void forwardTokenFromInner(final Token token) {
		super.forwardTokenFromInner(token);
		if (!isGatewayCondition()) {
			triggers.removeFirst(token.getInstance());
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (canTriggerManual()) {
			InstancePopupMenu.selectToTrigger(this, this, getProcess().getInstances());
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
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		triggers.paint(g, getElementInnerBounds().getRightTop());
	}

}
