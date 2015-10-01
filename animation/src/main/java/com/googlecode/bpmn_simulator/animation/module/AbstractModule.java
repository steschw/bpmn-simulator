package com.googlecode.bpmn_simulator.animation.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualElement;

public abstract class AbstractModule
		implements Module {

	private final Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> elements = new HashMap<>();

	protected AbstractModule() {
		super();
	}

	protected void addElement(final Class<? extends LogicalElement> logical, Class<? extends VisualElement> visual) {
		final Set<Class<? extends VisualElement>> visuals = new HashSet<>();
		Collections.addAll(visuals, visual);
		elements.put(logical, visuals);
	}

	@Override
	public Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> getElements() {
		return Collections.unmodifiableMap(elements);
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
