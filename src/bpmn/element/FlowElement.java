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

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("serial")
public abstract class FlowElement extends VisibleElement {

	protected static final int DEFAULT_INNER_MARGIN = 4;
	protected static final int NO_INNER_BORDER = 0;

	private final Collection<ElementRef<SequenceFlow>> incoming = new ArrayList<ElementRef<SequenceFlow>>(); 
	private final Collection<ElementRef<SequenceFlow>> outgoing = new ArrayList<ElementRef<SequenceFlow>>(); 

	public FlowElement(final String id, final String name) {
		super(id, name);
	}

	protected Collection<ElementRef<SequenceFlow>> getIncoming() {
		return incoming;
	}

	protected Collection<ElementRef<SequenceFlow>> getOutgoing() {
		return outgoing;
	}

	public boolean hasIncoming() {
		return !getIncoming().isEmpty();
	}

	public boolean hasOutgoing() {
		return !getOutgoing().isEmpty();
	}

	public void addIncoming(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !incoming.contains(element)) {
			incoming.add(element);
		}
	}

	public void addOutgoing(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !outgoing.contains(element)) {
			outgoing.add(element);
		}
	}

	public int getInnerBorderMargin() {
		return NO_INNER_BORDER;
	}

}
