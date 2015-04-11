package com.googlecode.bpmn_simulator.animation.input;

public interface DefinitionListener {

	void info(String message);

	void warning(String message);

	void error(String message, Throwable throwable);

}
