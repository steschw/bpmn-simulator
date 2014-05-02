package com.googlecode.bpmn_simulator.animation.element.logical.ref;

public class NamedReference<E>
		implements Reference<E> {

	private final NamedElements<E> elements;

	private final String name;

	public NamedReference(final NamedElements<E> elements,
			final String name) {
		super();
		this.elements = elements;
		this.name = name;
	}

	private boolean hasName() {
		return (name != null) && !name.isEmpty();
	}

	@Override
	public boolean hasReference() {
		return hasName()
				&& elements.hasElement(name);
	}

	@Override
	public E getReferenced() {
		if (hasName()) {
			return elements.getElement(name);
		}
		return null;
	}

}
