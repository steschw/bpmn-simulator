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
package bpmn.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import bpmn.model.core.foundation.BaseElement;

public class ElementRefCollection<T extends BaseElement> {

	private final Map<String, ElementRef<T>> data
			= new TreeMap<String, ElementRef<T>>();

	public Collection<ElementRef<T>> values() {
		return data.values();
	}

	public void set(final T element) {
		set(element.getId(), element);
	}

	protected void set(final String id, final T element) {
		if (id != null && !id.isEmpty()) {
			if (data.containsKey(id)) {
				final ElementRef<T> elementRef = data.get(id);
				if (!elementRef.hasElement()) {
					elementRef.setElement(element);
				} else {
					assert elementRef.getElement() == element;
				}
			} else {
				data.put(id, new ElementRef<T>(element));
			}
		}
	}

	public ElementRef<T> getRef(final String id) {
		if (id != null && !id.isEmpty()) {
			if (!data.containsKey(id)) {
				set(id, null);
			}
			return data.get(id);
		} else {
			return null;
		}
	}

	public T get(final String id) {
		final ElementRef<T> elementRef = getRef(id);
		return (elementRef == null) ? null : elementRef.getElement();
	}

}
