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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class StaticStateSequence<STATE extends Serializable>
		extends AbstractStateSequence<STATE>
		implements StateSequence<STATE> {

	private final Queue<STATE> sequence = new LinkedList<>();

	public StaticStateSequence(final STATE beginState) {
		super();
		sequence.add(beginState);
	}

	public StaticStateSequence(final STATE... states) {
		super();
		Collections.addAll(sequence, states);
	}

	@Override
	public Iterator<STATE> iterator() {
		return sequence.iterator();
	}

	private STATE copyState(final STATE state) {
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try (final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
				oos.writeObject(state);
				oos.flush();
				final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				try (final ObjectInputStream ois = new ObjectInputStream(bis)) {
					return (STATE) ois.readObject();
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	public StaticStateSequence<STATE> add(final STATE state) {
		sequence.add(state);
		return this;
	}

	public STATE getLast() {
		return sequence.element();
	}

	public STATE addLast() {
		final STATE lastState = copyState(getLast());
		add(lastState);
		return lastState;
	}

}
