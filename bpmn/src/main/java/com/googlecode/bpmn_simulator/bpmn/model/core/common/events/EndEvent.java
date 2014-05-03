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
package com.googlecode.bpmn_simulator.bpmn.model.core.common.events;

import java.awt.Color;

import javax.swing.Icon;

import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Visualization;
import com.googlecode.bpmn_simulator.framework.instance.InstanceManager;



@SuppressWarnings("serial")
public final class EndEvent
		extends AbstractEvent
		implements ThrowEvent {

	public static final String ELEMENT_NAME = Messages.getString("endEvent"); //$NON-NLS-1$

	public EndEvent(final String id, final String name,
			final InstanceManager instanceManager) {
		super(id, name, instanceManager);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.EVENT_END);
	}

	@Override
	protected int getBorderWidth() {
		return 3;
	}

	@Override
	protected void tokenForwardToNextElement(final Token token, final Instance instance) {
		final EventDefinition definition = getDefinition();
		if (definition != null) {
			definition.throwTrigger(token);
		}
		super.tokenForwardToNextElement(token, instance);
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), true);
	}

}
