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
package com.googlecode.bpmn_simulator.animation.ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NamedElements<E> {

	private final Map<String, E> elements = new HashMap<>();

	public void setElement(final String name, final E element) {
		assert !elements.containsKey(name);
		elements.put(name, element);
	}

	public E getElement(final String name) {
		final E element = elements.get(name);
		assert element != null;
		return element;
	}

	public <F extends E> F getElement(final String name, final Class<F> clazz) {
		final E element = getElement(name);
		if (element != null) {
			if (clazz.isAssignableFrom(element.getClass())) {
				return clazz.cast(element);
			}
		}
		return null;
	}

	public boolean hasElement(final String name) {
		return elements.containsKey(name);
	}

	@Override
	public String toString() {
		final List<String> names = new ArrayList<>(elements.keySet());
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
