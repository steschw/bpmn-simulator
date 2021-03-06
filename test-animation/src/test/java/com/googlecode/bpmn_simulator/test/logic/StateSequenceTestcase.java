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

import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.bpmn_simulator.test.logic.StaticStateSequence;

public class StateSequenceTestcase {

	private static final String TEST_VALUE = "test";

	@Test
	public void testAddFromLast() {
		final StaticStateSequence<String> sequence = new StaticStateSequence<>(TEST_VALUE);
		String s = sequence.addLast();
		assertEquals(s, "test");
		assertNotSame(s, TEST_VALUE);
	}

	@Test
	public void test() {
		final StaticStateSequence<StaticAttributedElementsState<String, Integer>> sequence
				= new StaticStateSequence<>(new StaticAttributedElementsState<String, Integer>("a", "b", "c"));
		sequence.addLast().addAttribute("a", 1);
		sequence.addLast().moveAttribute("a", 1, "b");
		sequence.addLast().moveAttribute("b", 1, "c");
		sequence.addLast().removeAttribute("c", 1);
	}

}
