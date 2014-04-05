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

import java.util.Collection;

import javax.swing.Icon;

import com.googlecode.bpmn_simulator.bpmn.model.BPMNModel;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Visualization;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.AbstractContainerActivity;
import com.googlecode.bpmn_simulator.bpmn.trigger.Trigger;
import com.googlecode.bpmn_simulator.bpmn.trigger.TriggerCatching;
import com.googlecode.bpmn_simulator.bpmn.trigger.TriggerCatchingElement;
import com.googlecode.bpmn_simulator.bpmn.trigger.TriggerThrowing;
import com.googlecode.bpmn_simulator.framework.instance.Instance;
import com.googlecode.bpmn_simulator.framework.token.Token;



public abstract class EventDefinition
		implements TriggerCatching, TriggerThrowing {

	private final AbstractEvent event;

	public EventDefinition(final AbstractEvent event) {
		super();
		this.event = event;
	}

	protected AbstractEvent getEvent() {
		return event;
	}

	public abstract Icon getIcon(final Visualization visualization, final boolean inverse);

	protected static AbstractContainerActivity getContainerActivityByToken(final Token token) {
		return ((AbstractFlowElement)token.getCurrentFlow()).getContainerActivity();
	}

	@Override
	public void throwTrigger(final Token token) {
	}

	protected void throwTriggerToEqualEvents(final Token token) {
		final BPMNModel model = getContainerActivityByToken(token).getModel();
		final Collection<TriggerCatchingElement> catchEvents =  model.getCatchEvents();
		for (final TriggerCatchingElement catchEvent : catchEvents) {
			if (catchEvent instanceof Event) {
				final Event event = (Event)catchEvent;
				if (equals(event.getDefinition())) {
					catchEvent.catchTrigger(new Trigger(token.getInstance(), null));
				}
			}
		}
	}

	@Override
	public void catchTrigger(final Trigger trigger) {
		final Instance destinationInstance = trigger.getDestinationInstance();
		if (destinationInstance == null) {
			getEvent().passAllTokenToAllNextElements();
		} else {
			getEvent().passFirstInstanceTokenToAllNextElements(destinationInstance);
		}
	}

}
