package com.googlecode.bpmn_simulator.animation.element.visual.swing;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalEdgeElement;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualEdgeElement;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoints;

@SuppressWarnings("serial")
public abstract class AbstractVisualEdgeElement<E extends LogicalEdgeElement>
		extends AbstractVisualElement<E>
		implements VisualEdgeElement {

	private Waypoints waypoints;

	public AbstractVisualEdgeElement(final E element) {
		super(element);
	}

	public void setElementWaypoints(final Waypoints waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public Waypoints getElementWaypoints() {
		return waypoints;
	}

}
