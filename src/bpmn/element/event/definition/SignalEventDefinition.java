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
import bpmn.element.ElementRef;
import bpmn.element.Signal;
import bpmn.element.Visualization;
import bpmn.element.event.AbstractEvent;
import bpmn.element.event.CatchEvent;
import bpmn.token.Instance;
import bpmn.token.Token;

public final class SignalEventDefinition extends EventDefinition {

	private final ElementRef<Signal> signalRef;

	public SignalEventDefinition(final AbstractEvent event, final ElementRef<Signal> signalRef) {
		super(event);
		this.signalRef = signalRef; 
	}

	public ElementRef<Signal> getSignalRef() {
		return signalRef;
	}

	@Override
	public Icon getIcon(final Visualization visualization, final boolean inverse) {
		return visualization.getIcon(inverse
				? Visualization.ICON_SIGNAL_INVERSE
				: Visualization.ICON_SIGNAL);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof SignalEventDefinition) {
			final SignalEventDefinition definition = (SignalEventDefinition)obj;
			return super.equals(obj)
					|| (getSignalRef().equals(definition.getSignalRef()));
		}
		return false;
	}

	@Override
	public void throwHappen(final Token token) {
		super.throwHappen(token);

		final Instance instance = token.getInstance();
		final Model model = getProcessByToken(token).getModel();
		final Collection<CatchEvent> catchEvents =  model.getCatchEvents();
		for (final CatchEvent catchEvent : catchEvents) {
			if (equals(catchEvent)) {
				catchEvent.happen(instance);
			}
		}
	}

}
