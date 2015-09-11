package com.googlecode.bpmn_simulator.animation.module;

import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.input.Definition;

public interface Module {

	String getName();

	String getDescription();

	String getFileDescription();

	Collection<String> getFileExtensions();

	Definition<?> createEmptyDefinition();

}
