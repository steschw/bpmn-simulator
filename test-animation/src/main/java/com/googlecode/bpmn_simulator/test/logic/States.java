package com.googlecode.bpmn_simulator.test.logic;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.TreeSet;

public final class States {

	private States() {
	}

	public static <ELEMENT, ATTRIBUTE> String toString(final AttributedElementsState<ELEMENT, ATTRIBUTE> state) {
		final StringBuilder builder = new StringBuilder();
		builder.append('{');
		final Iterator<ELEMENT> elementIterator = new TreeSet<>(state.getElements()).iterator();
		while (elementIterator.hasNext()) {
			final ELEMENT element = elementIterator.next();
			builder.append(element);
			final Iterator<ATTRIBUTE> attributeIterator = new TreeSet<>(state.getElementAttributes(element)).iterator();
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

	public static <STATE> void print(final StateSequence<STATE> stateSequence,
			final boolean combine, final PrintStream out) {
		final Iterator<STATE> iter = stateSequence.iterator();
		STATE currentState = null;
		STATE previousState = null;
		while (iter.hasNext()) {
			previousState = currentState;
			currentState = iter.next();
			if (!combine || ((currentState != null) && !currentState.equals(previousState))) {
				out.println(currentState);
			}
		}
	}

}
