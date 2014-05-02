package com.googlecode.bpmn_simulator.animation.element.logical.ref;

public class NamedReferences<E>
		extends AbstractReferences<E> {

	private final NamedElements<E> elements;

	private NamedReferences(final NamedElements<E> elements) {
		super();
		this.elements = elements;
	}

	public NamedReference<E> add(final String referencedElementName) {
		final NamedReference<E> reference = new NamedReference<E>(elements, referencedElementName);
		super.add(reference);
		return reference;
	}

}
