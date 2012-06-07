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

import javax.swing.Icon;

import bpmn.Graphics;
import bpmn.element.ElementRef;
import bpmn.element.Message;
import bpmn.element.Rectangle;
import bpmn.element.Visualization;
import bpmn.token.Token;
import bpmn.trigger.Instantiable;
import bpmn.trigger.InstantiableNotifiySource;
import bpmn.trigger.Trigger;

@SuppressWarnings("serial")
public final class ReceiveTask
		extends AbstractMessageTask
		implements Instantiable, InstantiableNotifiySource {

	private static final int INSTANTIATE_MARGIN = 4;

	private final boolean instantiate;

	public ReceiveTask(final String id, final String name,
			final boolean instantiate,
			final ElementRef<Message> messageRef) {
		super(id, name, messageRef);
		this.instantiate = instantiate;
	}

	@Override
	public boolean isInstantiable() {
		return instantiate;
	}

	@Override
	public boolean isInstantiableNotifying() {
		return areAllIncommingFlowElementsInstantiableNotifyTargets();
	}

	@Override
	protected Icon getTypeIcon() {
		return getVisualization().getIcon(Visualization.ICON_RECEIVE);
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

	@Override
	protected boolean waitsForMessage(final Token token) {
		return super.waitsForMessage(token)
				&& !isInstantiable() && !isInstantiableNotifying();
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		if (isInstantiable()) {
			createCorrelationInstance(trigger.getSourceInstance()).newToken(this);
		} else if (isInstantiableNotifying()) {
			notifyInstantiableIncomingFlowElements(this, trigger);
		} else {
			super.catchTrigger(trigger);
		}
	}

}
