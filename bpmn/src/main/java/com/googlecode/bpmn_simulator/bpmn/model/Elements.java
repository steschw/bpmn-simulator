package com.googlecode.bpmn_simulator.bpmn.model;

import java.util.Collection;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;

public final class Elements {

	private Elements() {
	}

	public static <T extends LogicalFlowElement> T findById(final Collection<T> elements, final String id) {
		if (id != null) {
			for (final T element : elements) {
				if (element instanceof BaseElement) {
					if (id.equals(((BaseElement) element).getId())) {
						return element;
					}
				}
			}
		}
		return null;
	}

}
