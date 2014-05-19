/*
 * Copyright (C) 2014 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events;

import java.awt.Graphics2D;
import java.awt.Image;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ConditionalEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ErrorEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.Event;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.LinkEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.MessageEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TerminateEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TimerEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNTokenShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
abstract class AbstractEventShape<E extends Event>
		extends AbstractBPMNTokenShape<E> {

	public AbstractEventShape(final E element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillOval(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		getPresentation().drawOval(g, getInnerBoundsRelative());
		paintElementIcon(g);
	}

	protected Image getIconImage(final boolean inverted) {
		final EventDefinition definition = getLogicalElement().getEventDefinition();
		if (definition instanceof ConditionalEventDefinition) {
			assert !inverted;
			return Appearance.getDefault().getImage(Appearance.IMAGE_CONDITIONAL);
		} else if (definition instanceof ErrorEventDefinition) {
			return Appearance.getDefault().getImage(inverted
					? Appearance.IMAGE_ERROR_INVERSE : Appearance.IMAGE_ERROR);
		} else if (definition instanceof LinkEventDefinition) {
			return Appearance.getDefault().getImage(inverted
					? Appearance.IMAGE_LINK_INVERSE : Appearance.IMAGE_LINK);
		} else if (definition instanceof MessageEventDefinition) {
			return Appearance.getDefault().getImage(inverted
					? Appearance.IMAGE_MESSAGE_INVERSE : Appearance.IMAGE_MESSAGE);
		} else if (definition instanceof TerminateEventDefinition) {
			assert !inverted;
			return Appearance.getDefault().getImage(Appearance.IMAGE_TERMINATE);
		} else if (definition instanceof TimerEventDefinition) {
			assert !inverted;
			return Appearance.getDefault().getImage(Appearance.IMAGE_TIMER);
		}
		return null;
	}

	protected Image getIconImage() {
		return getIconImage(false);
	}

	private void paintElementIcon(final Graphics2D g) {
		final Image image = getIconImage();
		if (image != null) {
			getPresentation().drawImage(g, image, getInnerBoundsRelative().shrink(6));
		}
	}

	@Override
	protected void paintTokens(Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
