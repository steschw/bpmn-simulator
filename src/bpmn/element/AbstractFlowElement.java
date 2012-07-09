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
public abstract class AbstractFlowElement
		extends VisibleElement {

	protected static final int DEFAULT_INNER_MARGIN = 4;
	protected static final int NO_INNER_BORDER = 0;

	private final Collection<ElementRef<SequenceFlow>> incomingRefs
		= new ArrayList<ElementRef<SequenceFlow>>(); 

	private final Collection<ElementRef<SequenceFlow>> outgoingRefs
		= new ArrayList<ElementRef<SequenceFlow>>(); 

	public AbstractFlowElement(final String id, final String name) {
		super(id, name);
	}

	private Collection<ElementRef<SequenceFlow>> getIncomingRefs() {
		return incomingRefs;
	}

	private Collection<ElementRef<SequenceFlow>> getOutgoingRefs() {
		return outgoingRefs;
	}

	protected final <E extends SequenceFlow> Collection<E>
			getElementsFromElementRefs(final Collection<ElementRef<E>> elementRefs) {
		final Collection<E> elements = new ArrayList<E>();
		for (ElementRef<E> elementRef : elementRefs) {
			if ((elementRef != null) && elementRef.hasElement()) {
				elements.add(elementRef.getElement());
			}
		}
		return elements;
	}

	public Collection<SequenceFlow> getIncoming() {
		return getElementsFromElementRefs(incomingRefs);
	}

	public Collection<SequenceFlow> getOutgoing() {
		return getElementsFromElementRefs(outgoingRefs);
	}

	public boolean hasIncoming() {
		return !getIncomingRefs().isEmpty();
	}

	public boolean hasOutgoing() {
		return !getOutgoingRefs().isEmpty();
	}

	public void addIncomingRef(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !incomingRefs.contains(element)) {
			incomingRefs.add(element);
		}
	}

	public void addOutgoingRef(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !outgoingRefs.contains(element)) {
			outgoingRefs.add(element);
		}
	}

	protected Collection<AbstractFlowElement> getIncomingFlowElements() {
		final Collection<AbstractFlowElement> incomingFlowElements = new ArrayList<AbstractFlowElement>();
		for (final SequenceFlow incoming : getIncoming()) {
			final AbstractFlowElement flowElement = incoming.getSource();
			if (flowElement != null) {
				incomingFlowElements.add(flowElement);
			}
		}
		return incomingFlowElements;
	}

	protected Collection<AbstractFlowElement> getOutgoingFlowElements() {
		final Collection<AbstractFlowElement> outgoingFlowElements = new ArrayList<AbstractFlowElement>();
		for (final SequenceFlow outgoing : getOutgoing()) {
			final AbstractFlowElement flowElement = outgoing.getTarget();
			if (flowElement != null) {
				outgoingFlowElements.add(flowElement);
			}
		}
		return outgoingFlowElements;
	}

	public int getInnerBorderMargin() {
		return NO_INNER_BORDER;
	}

}
