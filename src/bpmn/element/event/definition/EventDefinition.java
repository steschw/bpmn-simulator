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
package bpmn.element.event.definition;

import java.util.Collection;

import javax.swing.Icon;

import bpmn.Model;
import bpmn.element.VisibleElement;
import bpmn.element.Visualization;
import bpmn.element.activity.ExpandedProcess;
import bpmn.element.event.AbstractEvent;
import bpmn.element.event.Event;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCatching;
import bpmn.trigger.TriggerCatchingElement;
import bpmn.trigger.TriggerThrowing;

public abstract class EventDefinition
	implements TriggerCatching, TriggerThrowing{

	private final AbstractEvent event;

	public EventDefinition(final AbstractEvent event) {
		super();
		this.event = event;
	}

	protected AbstractEvent getEvent() {
		return event;
	}

	public abstract Icon getIcon(final Visualization visualization, final boolean inverse);

	protected static ExpandedProcess getProcessByToken(final Token token) {
		return ((VisibleElement)token.getCurrentFlow()).getProcess();		
	}

	@Override
	public void throwTrigger(final Token token) {
	}

	protected void throwTriggerToEqualEvents(final Token token) {
		final Model model = getProcessByToken(token).getModel();
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
