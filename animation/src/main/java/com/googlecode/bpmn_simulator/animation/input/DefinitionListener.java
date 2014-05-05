package com.googlecode.bpmn_simulator.animation.input;

public interface DefinitionListener {

	void warning(String message);

	void error(String message, Throwable throwable);

}
