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

import javax.swing.Icon;

import com.googlecode.bpmn_simulator.animation.token.Token;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Message;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Visualization;
import com.googlecode.bpmn_simulator.framework.element.ElementRef;



public final class MessageEventDefinition
		extends EventDefinition {

	private final ElementRef<Message> messageRef;

	public MessageEventDefinition(final AbstractEvent event,
			final ElementRef<Message> messageRef) {
		super(event);
		this.messageRef = messageRef;
	}

	public Message getMessage() {
		return (messageRef != null) && messageRef.hasElement()
				? messageRef.getElement()
				: null;
	}

	@Override
	public Icon getIcon(final Visualization visualization, final boolean inverse) {
		return visualization.getIcon(inverse
				? Visualization.ICON_MESSAGE_INVERSE
				: Visualization.ICON_MESSAGE);
	}

	@Override
	public void throwTrigger(final Token token) {
		super.throwTrigger(token);

		final AbstractEvent event = getEvent();
		event.getModel().sendMessages(event, token.getInstance());
	}

}
