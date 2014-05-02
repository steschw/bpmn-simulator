package com.googlecode.bpmn_simulator.test.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.bpmn_simulator.test.logic.StateSequence;

public class StateSequenceTestcase {

	private static final String TEST_VALUE = "test";

	@Test
	public void testAddFromLast() {
		final StateSequence<String> sequence = new StateSequence<String>(TEST_VALUE);
		String s = sequence.addFromLast();
		assertEquals(s, "test");
		assertNotSame(s, TEST_VALUE);
	}

	@Test
	public void test() {
		final StateSequence<AttributedElementsState<String, Integer>> sequence
				= new StateSequence<AttributedElementsState<String, Integer>>(new AttributedElementsState<String, Integer>("a", "b", "c"));
		sequence.addFromLast().addAttribute("a", 1);
		sequence.addFromLast().moveAttribute("a", 1, "b");
		sequence.addFromLast().moveAttribute("b", 1, "c");
		sequence.addFromLast().removeAttribute("c", 1);
		System.out.println(sequence);
	}

}
