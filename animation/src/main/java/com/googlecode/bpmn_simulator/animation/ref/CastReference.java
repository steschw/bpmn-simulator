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

public class CastReference<E, T extends E>
		implements Reference<T> {

	private final Reference<E> reference;

	private final Class<T> clazz;

	public CastReference(final Reference<E> reference, final Class<T> clazz) {
		super();
		this.reference = reference;
		this.clazz = clazz;
	}

	@Override
	public boolean hasReference() {
		return (reference != null)
				&& reference.hasReference();
	}

	private boolean hasType(final E referenced) {
		return clazz.isAssignableFrom(referenced.getClass());
	}

	@Override
	public T getReferenced() {
		if (hasReference()) {
			final E referenced = getReferenced();
			if ((referenced != null) && hasType(referenced)) {
				return clazz.cast(referenced);
			}
		}
		return null;
	}

}
