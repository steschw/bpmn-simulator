package com.googlecode.bpmn_simulator.test.logic;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents a state as a set of different elements with attributes
 */
public class AttributedElementsState<ELEMENT, ATTRIBUTE>
		implements Serializable {

	private static final long serialVersionUID = -9033432433330245560L;

	private final Map<ELEMENT, Set<ATTRIBUTE>> elementAttributes
			= new HashMap<ELEMENT, Set<ATTRIBUTE>>();

	@SafeVarargs
	public AttributedElementsState(final ELEMENT... elements) {
		this(Arrays.<ELEMENT>asList(elements));
	}

	public AttributedElementsState(final Collection<ELEMENT> elements) {
		for (final ELEMENT element : elements) {
			addElement(element);
		}
	}

	private void addElement(final ELEMENT element) {
		elementAttributes.put(element, new HashSet<ATTRIBUTE>());
	}

	public Set<ATTRIBUTE> getAttributes(final ELEMENT element) {
		if (element != null) {
			final Set<ATTRIBUTE> attributes = elementAttributes.get(element);
			if (attributes != null) {
				return attributes;
			}
		}
		throw new IllegalArgumentException(MessageFormat.format(
				"element {0} doesn''t exist",
				element));
	}

	public void addAttribute(final ELEMENT element, final ATTRIBUTE attribute) {
		if (!getAttributes(element).add(attribute)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"attribute {0} already exist in {1}",
					attribute, element));
		}
	}

	public void removeAttribute(final ELEMENT element, final ATTRIBUTE attribute) {
		if (!getAttributes(element).remove(attribute)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"attribute {0} doesn''t exist in {1}",
					attribute, element));
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AttributedElementsState) {
			final AttributedElementsState<?, ?> state = (AttributedElementsState<?, ?>) obj;
			return elementAttributes.equals(state.elementAttributes);
		}
		return false;
	}

	public AttributedElementsState<ELEMENT, ATTRIBUTE> moveAttribute(
			final ELEMENT fromElement, final ATTRIBUTE attribute, final ELEMENT... toElements) {
		removeAttribute(fromElement, attribute);
		assert toElements.length > 0;
		for (final ELEMENT toElement : toElements) {
			addAttribute(toElement, attribute);
		}
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append('{');
		final Iterator<ELEMENT> elementIterator = elementAttributes.keySet().iterator();
		while (elementIterator.hasNext()) {
			final ELEMENT element = elementIterator.next();
			builder.append(element);
			final Iterator<ATTRIBUTE> attributeIterator = getAttributes(element).iterator();
			if (attributeIterator.hasNext()) {
				builder.append(':');
				builder.append('<');
				while (attributeIterator.hasNext()) {
					final ATTRIBUTE attribute = attributeIterator.next();
					builder.append(attribute);
					if (attributeIterator.hasNext()) {
						builder.append(',');
					}
				}
				builder.append('>');
			} else {
			}
			if (elementIterator.hasNext()) {
				builder.append(',');
			}
		}
		builder.append('}');
		return builder.toString();
	}

}
