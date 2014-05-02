package com.googlecode.bpmn_simulator.animation.element.logical.ref;

import java.util.ArrayList;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;

public final class ReferenceUtils<E> {

	private ReferenceUtils() {
		super();
	}

	public static <E extends LogicalElement> References<E> emptyReferences() {
		return new EmptyReferences<E>();
	}

	private static class EmptyReferences<E>
			extends ArrayList<E>
			implements References<E> {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean add(final E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final E element) {
			throw new UnsupportedOperationException();
		}

		
	}

}
