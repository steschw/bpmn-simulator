package com.googlecode.bpmn_simulator.animation.element.logical.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractReferences<E>
		implements References<E> {

	private Set<Reference<E>> references = new HashSet<Reference<E>>();

	protected void add(final Reference<E> reference) {
		references.add(reference);
	}

	@Override
	public Iterator<E> iterator() {
		Collection<E> elements = new ArrayList<E>();
		for (final Reference<E> reference : references) {
			final E element = reference.getReferenced();
			if (element != null) {
				elements.add(element);
			}
		}
		return elements.iterator();
	}

	@Override
	public boolean isEmpty() {
		return getReferencedCount() > 0;
	}

	public boolean hasInvalidReferences() {
		for (final Reference<E> reference : references) {
			if ((reference == null) || !reference.hasReference()) {
				return true;
			}
		}
		return false;
	}

	private int getReferencedCount() {
		int count = 0;
		for (final Reference<E> reference : references) {
			if (reference.hasReference()) {
				++count;
			}
		}
		return count;
	}

}
