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
package bpmn.element;

public class ElementRef<E extends BaseElement> {

	private E element;

	public ElementRef() {
		super();
	}

	public ElementRef(final E element) {
		this();
		setElement(element);
	}

	public void setElement(final E element) {
		this.element = element;
	}

	public E getElement() {
		return element;
	}

	public boolean hasElement() {
		return element != null;
	}

	public boolean equalsElement(final BaseElement element) {
		final BaseElement e = getElement();
		if ((e == null) || (element == null)) {
			return false;
		}
		return e.equals(element);
	}

	@Override
	public boolean equals(final Object object) {
		if (object instanceof ElementRef<?>) {
			return equalsElement(((ElementRef<?>)object).getElement());
		} else {
			assert false;
		}
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return hasElement() ? getElement().hashCode() : super.hashCode();
	}

}
