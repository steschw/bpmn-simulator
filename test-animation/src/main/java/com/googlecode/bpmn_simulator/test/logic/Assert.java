package com.googlecode.bpmn_simulator.test.logic;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Iterator;

public final class Assert {

	private Assert() {
	}

	public static <STATE extends Serializable> void assertStates(
			final StateSequence<STATE> expectedStates, final Iterator<STATE> states) {
		assertStates(expectedStates.iterator(), states);
	}

	public static <STATE> void assertStates(
			final Iterator<STATE> expectedStates, final Iterator<STATE> states) {
		STATE expectedState = expectedStates.next();
		while (states.hasNext()) {
			final STATE state = states.next();
			if (!state.equals(expectedState)) {
				assertTrue(MessageFormat.format("unexpected state {0}, no more states expected", state),
						expectedStates.hasNext());
				expectedState = expectedStates.next();
				assertTrue(MessageFormat.format("unexpected state {0}, expected {1}", state, expectedState),
						state.equals(expectedState));
			}
		}
		assertFalse("next state expected", expectedStates.hasNext());
	}

}
