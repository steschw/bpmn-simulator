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
package com.googlecode.bpmn_simulator.test.logic;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a state as a set of different elements with attributes
 */
public class StaticAttributedElementsState<ELEMENT, ATTRIBUTE>
		implements AttributedElementsState<ELEMENT, ATTRIBUTE> {

	private static final long serialVersionUID = -9033432433330245560L;

	private final Map<ELEMENT, Set<ATTRIBUTE>> elementAttributes = new HashMap<>();

	@SafeVarargs
	public StaticAttributedElementsState(final ELEMENT... elements) {
		this(Arrays.<ELEMENT>asList(elements));
	}

	public StaticAttributedElementsState(final Collection<ELEMENT> elements) {
		for (final ELEMENT element : elements) {
			addElement(element);
		}
	}

	private void addElement(final ELEMENT element) {
		elementAttributes.put(element, new HashSet<ATTRIBUTE>());
	}

	@Override
	public Set<ELEMENT> getElements() {
		return elementAttributes.keySet();
	}

	@Override
	public Set<ATTRIBUTE> getElementAttributes(final Object element) {
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
		if (!getElementAttributes(element).add(attribute)) {
			throw new IllegalArgumentException(MessageFormat.format(
					"attribute {0} already exist in {1}",
					attribute, element));
		}
	}

	public void removeAttribute(final ELEMENT element, final ATTRIBUTE attribute) {
		if (!getElementAttributes(element).remove(attribute)) {
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
			final Set<ELEMENT> elements = getElements();
			if (Objects.equals(elements, state.getElements())) {
				for (final ELEMENT element : elements) {
					if (!Objects.equals(getElementAttributes(element), state.getElementAttributes(element))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return elementAttributes.hashCode();
	}

	public StaticAttributedElementsState<ELEMENT, ATTRIBUTE> moveAttribute(
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
		final Iterator<ELEMENT> elementIterator = new TreeSet<>(elementAttributes.keySet()).iterator();
		while (elementIterator.hasNext()) {
			final ELEMENT element = elementIterator.next();
			builder.append(element);
			final Iterator<ATTRIBUTE> attributeIterator = new TreeSet<>(getElementAttributes(element)).iterator();
			if (attributeIterator.hasNext()) {
				builder.append(":<");
				while (attributeIterator.hasNext()) {
					final ATTRIBUTE attribute = attributeIterator.next();
					builder.append(attribute);
					if (attributeIterator.hasNext()) {
						builder.append(',');
					}
				}
				builder.append('>');
			}
			if (elementIterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append('}');
		return builder.toString();
	}

}
