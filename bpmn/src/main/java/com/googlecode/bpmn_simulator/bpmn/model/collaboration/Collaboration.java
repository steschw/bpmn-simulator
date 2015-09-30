/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.model.collaboration;

import java.util.HashSet;
import java.util.Set;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElements;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.NamedElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.AbstractRootElement;

public class Collaboration
		extends AbstractRootElement
		implements NamedElement {

	static {
		LogicalElements.register(Collaboration.class, Messages.getString("collaboration")); //$NON-NLS-1$
	}

	private String name;

	private boolean isClosed;

	private final Set<Participant> participiants = new HashSet<>();

	public Collaboration(final String id, final String name, final boolean isClosed) {
		super(id);
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void addParticipant(final Participant participant) {
		participiants.add(participant);
	}

	@Override
	public String toString() {
		return firstNotEmpty(getName(), super.toString());
	}

}
