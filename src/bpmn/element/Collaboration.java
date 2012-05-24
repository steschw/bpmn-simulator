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
package bpmn.element;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Scrollable;

import bpmn.element.event.CatchEvent;
import bpmn.token.Instance;

@SuppressWarnings("serial")
public class Collaboration extends FlowElement implements Scrollable {

	private final Collection<MessageFlow> messageFlows = new ArrayList<MessageFlow>();

	public Collaboration(final String id) {
		super(id, null);
	}

	public void addMessageFlow(final MessageFlow messageFlow) {
		messageFlows.add(messageFlow);
	}

	public void sendMessagesFrom(final FlowElement source, final Instance instance) {
		for (final MessageFlow messageFlow : messageFlows) {
			if (source.equals(messageFlow.getSource())) {
				final FlowElement target = messageFlow.getTarget();
				if (target instanceof CatchEvent) {
					((CatchEvent)target).happen(null);
				}
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByInnerComponents();
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(final Rectangle arg0,
			final int arg1, final int arg2) {
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(final Rectangle arg0,
			final int arg1, final int arg2) {
		return 0;
	}

	@Override
	protected void paintElement(final Graphics g) {
	}

}
