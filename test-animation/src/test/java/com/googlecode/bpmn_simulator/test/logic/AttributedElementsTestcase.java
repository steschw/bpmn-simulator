package com.googlecode.bpmn_simulator.test.logic;

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.bpmn_simulator.test.logic.AttributedElementsState;

public class AttributedElementsTestcase {

	@Test
	public void testEquals() {
		final AttributedElementsState<String, Integer> state1 = new AttributedElementsState<String, Integer>("a", "b");
		final AttributedElementsState<String, Integer> state2 = new AttributedElementsState<String, Integer>("a", "b");
		assertEquals(state1, state2);
		state1.addAttribute("a", 1);
		state2.addAttribute("a", 1);
		assertEquals(state1, state2);
		state1.addAttribute("b", 2);
		state2.addAttribute("b", 2);
		assertEquals(state1, state2);
		state1.removeAttribute("a", 1);
		state2.removeAttribute("a", 1);
		assertEquals(state1, state2);
		state1.removeAttribute("b", 2);
		state2.removeAttribute("b", 2);
		assertEquals(state1, state2);
	}

	@Test
	public void testNotEquals() {
		final AttributedElementsState<String, Integer> state1 = new AttributedElementsState<String, Integer>("a", "b");
		final AttributedElementsState<String, Integer> state2 = new AttributedElementsState<String, Integer>("x", "y");
		assertNotEquals(state1, state2);
		final AttributedElementsState<String, Integer> state3 = new AttributedElementsState<String, Integer>("x", "y");
		state2.addAttribute("x", 1);
		assertNotEquals(state1, state2);
		state3.addAttribute("x", 2);
		assertNotEquals(state1, state2);
	}

	@Test
	public void testMove1() {
		final AttributedElementsState<String, Integer> state1 = new AttributedElementsState<String, Integer>("a", "b");
		state1.addAttribute("a", 1);
		state1.moveAttribute("a", 1, "b");
		final AttributedElementsState<String, Integer> state2 = new AttributedElementsState<String, Integer>("a", "b");
		state2.addAttribute("b", 1);
		assertEquals(state2, state1);
	}

	@Test
	public void testMove2() {
		final AttributedElementsState<String, Integer> state1 = new AttributedElementsState<String, Integer>("a", "b", "c");
		state1.addAttribute("a", 1);
		state1.moveAttribute("a", 1, "b", "c");
		final AttributedElementsState<String, Integer> state2 = new AttributedElementsState<String, Integer>("a", "b", "c");
		state2.addAttribute("b", 1);
		state2.addAttribute("c", 1);
		assertEquals(state2, state1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testException1() {
		final AttributedElementsState<String, Integer> state = new AttributedElementsState<String, Integer>("a", "b");
		state.addAttribute("a", 1);
		state.addAttribute("a", 1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testException2() {
		final AttributedElementsState<String, Integer> state = new AttributedElementsState<String, Integer>("a", "b");
		state.removeAttribute("a", 1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testException3() {
		final AttributedElementsState<String, Integer> state = new AttributedElementsState<String, Integer>("a", "b");
		state.moveAttribute("a", 1, "b");
	}

}
