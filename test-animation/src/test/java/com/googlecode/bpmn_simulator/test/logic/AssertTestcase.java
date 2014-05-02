package com.googlecode.bpmn_simulator.test.logic;

import static com.googlecode.bpmn_simulator.test.logic.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.googlecode.bpmn_simulator.test.logic.StateSequence;

public class AssertTestcase {

	@Test
	public void testAssert1() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		assertStates(expectedStates, states.iterator());
	}

	@Test
	public void testAssert2() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		states.add(1);
		states.add(1);
		assertStates(expectedStates, states.iterator());
	}

	@Test
	public void testAssert3() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		states.add(2);
		assertStates(expectedStates, states.iterator());
	}

	@Test
	public void testAssert4() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		states.add(1);
		states.add(1);
		states.add(2);
		assertStates(expectedStates, states.iterator());
	}

	@Test
	public void testAssert5() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		states.add(2);
		states.add(2);
		states.add(2);
		assertStates(expectedStates, states.iterator());
	}

	@Test
	public void testAssert6() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		states.add(1);
		states.add(1);
		states.add(2);
		states.add(2);
		states.add(2);
		assertStates(expectedStates, states.iterator());
	}

	@Test(expected=AssertionError.class)
	public void testException1() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		assertStates(expectedStates, states.iterator());
	}

	@Test(expected=AssertionError.class)
	public void testException2() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 1);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		assertStates(expectedStates, states.iterator());
	}

	@Test(expected=AssertionError.class)
	public void testException3() {
		final StateSequence<Integer> expectedStates = new StateSequence<Integer>(1, 2);
		final List<Integer> states = new ArrayList<Integer>();
		states.add(1);
		assertStates(expectedStates, states.iterator());
	}

}
