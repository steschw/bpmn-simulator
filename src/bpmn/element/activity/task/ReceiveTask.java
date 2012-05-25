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

import bpmn.element.ElementRef;
import bpmn.element.FlowElement;
import bpmn.element.Message;
import bpmn.element.SequenceFlow;
import bpmn.element.Visualization;
import bpmn.element.event.CatchEvent;
import bpmn.element.gateway.EventBasedGateway;
import bpmn.token.Instance;
import bpmn.token.Token;

@SuppressWarnings("serial")
public final class ReceiveTask
		extends AbstractMessageTask
		implements CatchEvent, MouseListener {

	public ReceiveTask(final String id, final String name,
			final ElementRef<Message> messageRef) {
		super(id, name, messageRef);
		addMouseListener(this);
	}

	@Override
	protected Icon getTypeIcon() {
		return getVisualization().getIcon(Visualization.ICON_RECEIVE);
	}

	protected int notifyEventBasedGatewaysEventHappen(final Instance instance) {
		int count = 0;
		for (final ElementRef<SequenceFlow> incomingRef : getIncoming()) {
			if (incomingRef.hasElement()) {
				final SequenceFlow incoming = incomingRef.getElement();
				final FlowElement flowElement = incoming.getSource();
				if (flowElement instanceof EventBasedGateway) {
					((EventBasedGateway)flowElement).eventHappen(this, instance);
					++count;
				}
			}
		}
		return count;
	}

	@Override
	public boolean canHappenManual() {
		return false;
	}

	@Override
	public void happen(final Instance instance) {
		notifyEventBasedGatewaysEventHappen(instance);
		passFirstTokenToAllOutgoing();
	}

	@Override
	protected boolean canForwardTokenToOutgoing(final Token token) {
		return isGatewayCondition();
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		happen(null);
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

}
