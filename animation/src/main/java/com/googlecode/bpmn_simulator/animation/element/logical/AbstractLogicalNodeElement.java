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
package com.googlecode.bpmn_simulator.animation.element.logical;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.ReferenceUtils;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.References;

public abstract class AbstractLogicalNodeElement
		extends AbstractLogicalElement
		implements LogicalNodeElement {

	private static final References<LogicalEdgeElement> EMPTY_REFERENCES
			= ReferenceUtils.<LogicalEdgeElement>emptyReferences();

	private References<LogicalEdgeElement> incoming;
	private References<LogicalEdgeElement> outgoing;

	@Override
	public void setIncoming(final References<LogicalEdgeElement> incoming) {
		this.incoming = incoming;
	}

	@Override
	public References<LogicalEdgeElement> getIncoming() {
		if (incoming != null) {
			return incoming;
		}
		return EMPTY_REFERENCES;
	}

	@Override
	public void setOutgoing(final References<LogicalEdgeElement> outgoing) {
		this.outgoing = outgoing;
	}

	@Override
	public References<LogicalEdgeElement> getOutgoing() {
		if (outgoing != null) {
			return outgoing;
		}
		return EMPTY_REFERENCES;
	}

}
