package com.googlecode.bpmn_simulator.animation.module;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualElement;
import com.googlecode.bpmn_simulator.animation.input.Definition;

public interface Module {

	String getName();

	String getDescription();

	String getFileDescription();

	Collection<String> getFileExtensions();

	Definition<?> createEmptyDefinition();

	Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> getElements();

}
