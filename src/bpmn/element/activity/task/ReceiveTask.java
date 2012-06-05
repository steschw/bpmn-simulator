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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;

import bpmn.Graphics;
import bpmn.element.ElementRef;
import bpmn.element.Message;
import bpmn.element.Rectangle;
import bpmn.element.Visualization;
import bpmn.instance.Instance;
import bpmn.instance.InstanceListener;
import bpmn.instance.InstancePopupMenu;
import bpmn.token.Token;
import bpmn.trigger.Instantiable;
import bpmn.trigger.StoringTriggerCatchingElement;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCollection;

@SuppressWarnings("serial")
public final class ReceiveTask
		extends AbstractMessageTask
		implements StoringTriggerCatchingElement, Instantiable,
				MouseListener, InstanceListener {

	private static final int INSTANTIATE_MARGIN = 4;

	private TriggerCollection triggers = new TriggerCollection();

	private final boolean instantiate;

	public ReceiveTask(final String id, final String name,
			final boolean instantiate,
			final ElementRef<Message> messageRef) {
		super(id, name, messageRef);
		this.instantiate = instantiate;
		addMouseListener(this);
	}

	@Override
	public boolean isInstantiable() {
		return instantiate || areAllIncommingFlowElementsInstantiable();
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
		final boolean incomingInstantiable = areAllIncommingFlowElementsInstantiable(); 
		if (isInstantiable() && !incomingInstantiable) {
			getProcess().createInstance(null).newToken(this);
		} else {
			if (getBehavior().getKeepTriggers() && !incomingInstantiable) {
				triggers.add(trigger);
				trigger.getDestinationInstance().addInstanceListener(this);
			} else {
				passFirstInstanceTokenToAllNextElements(trigger.getDestinationInstance());
				notifyTriggerNotifyEvents(this, trigger);
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
	protected boolean canForwardTokenToOutgoing(final Token token) {
		return super.canForwardTokenToOutgoing(token)
				&& (isInstantiable() || isGatewayCondition()
				|| (triggers.first(token.getInstance()) != null));
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

	@Override
	public void paintTypeIcon(final Graphics g, final Icon icon, final Point position) {
		super.paintTypeIcon(g, icon, position);

		if (isInstantiable()) {
			final Rectangle rect = new Rectangle(position,
					new Dimension(icon.getIconWidth(), icon.getIconWidth()));
			rect.grow(INSTANTIATE_MARGIN, INSTANTIATE_MARGIN);
			g.drawOval(rect);
		}
	}

}
