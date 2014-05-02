package com.googlecode.bpmn_simulator.animation.element.visual.swing;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalNodeElement;
import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualNodeElement;

@SuppressWarnings("serial")
public abstract class AbstractVisualNodeElement<E extends LogicalNodeElement>
		extends AbstractVisualElement<E>
		implements VisualNodeElement {

	private Bounds bounds;

	public AbstractVisualNodeElement(final E element) {
		super(element);
	}

	public void setElementBounds(final Bounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public Bounds getElementBounds() {
		return bounds;
	}

}
