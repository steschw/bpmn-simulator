package com.googlecode.bpmn_simulator.test.logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StateSequence<STATE extends Serializable>
		implements Iterable<STATE> {

	private final List<STATE> sequence = new ArrayList<STATE>();

	public StateSequence(final STATE beginState) {
		super();
		sequence.add(beginState);
	}

	@SafeVarargs
	public StateSequence(final STATE... states) {
		super();
		for (int i = 0; i < states.length; ++i) {
			sequence.add(states[i]);
		}
	}

	private static void closeSilent(final AutoCloseable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("unchecked")
	private STATE copyState(final STATE state) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(state);
			oos.flush();

			final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			ois = new ObjectInputStream(bis);
			return (STATE) ois.readObject();
		} catch (Exception e) {
		} finally {
			closeSilent(oos);
			closeSilent(ois);
		}
		return null;
	}

	public STATE addFromLast() {
		final STATE lastState = copyState(getLastState());
		addState(lastState);
		return lastState;
	}

	public StateSequence<STATE> addState(final STATE state) {
		sequence.add(state);
		return this;
	}

	public Iterator<STATE> iterator() {
		return sequence.iterator();
	}

	public STATE getLastState() {
		return sequence.get(sequence.size() - 1);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder('{');
		final Iterator<STATE> stateIterator = iterator();
		int index = 0;
		while (stateIterator.hasNext()) {
			final STATE state = stateIterator.next();
			builder.append(index);
			builder.append(':');
			builder.append(state);
			++index;
			if (stateIterator.hasNext()) {
				builder.append(',');
				builder.append(' ');
			}
		}
		builder.append('}');
		return builder.toString();
	}

}
