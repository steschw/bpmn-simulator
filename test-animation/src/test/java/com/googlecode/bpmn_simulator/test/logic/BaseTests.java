package com.googlecode.bpmn_simulator.test.logic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AssertTestcase.class, AttributedElementsTestcase.class,
		StateSequenceTestcase.class })
public class BaseTests {

}
