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
package com.googlecode.bpmn_simulator.animation.element.logical.ref;

public class NamedReference<E extends F, F>
		implements Reference<E> {

	private final NamedElements<F> elements;

	private final Class<E> clazz;

	private final String name;

	public NamedReference(final NamedElements<F> elements,
			final String name,
			final Class<E> clazz) {
		super();
		this.elements = elements;
		this.name = name;
		this.clazz = clazz;
	}

	private boolean hasName() {
		return (name != null) && !name.isEmpty();
	}

	@Override
	public boolean hasReference() {
		return hasName()
				&& elements.hasElement(name);
	}

	private boolean hasType(final F referenced) {
		return clazz.isAssignableFrom(referenced.getClass());
	}

	@Override
	public E getReferenced() {
		if (hasName()) {
			final F referenced = elements.getElement(name);
			if (referenced != null) {
				if (hasType(referenced)) {
					return clazz.cast(referenced);
				}
			}
		}
		return null;
	}

}
