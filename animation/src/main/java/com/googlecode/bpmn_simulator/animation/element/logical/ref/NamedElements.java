package com.googlecode.bpmn_simulator.animation.element.logical.ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NamedElements<E> {

	private Map<String, E> elements = new HashMap<String, E>();

	public void setElement(final String name, final E element) {
		assert !elements.containsKey(name);
		elements.put(name, element);
	}

	public E getElement(final String name) {
		final E element = elements.get(name);
		assert element != null;
		return element;
	}

	public boolean hasElement(final String name) {
		return elements.containsKey(name);
	}

	@Override
	public String toString() {
		final List<String> names = new ArrayList<String>(elements.keySet());
		Collections.sort(names);
		final StringBuilder builder = new StringBuilder();
		final Iterator<String> i = names.iterator();
		while (i.hasNext()) {
			final String name = i.next();
			builder.append('\'');
			builder.append(name);
			builder.append('\'');
			builder.append('=');
			builder.append(elements.get(name));
			if (i.hasNext()) {
				builder.append(',');
			}
		}
		return builder.toString();
	}

}
