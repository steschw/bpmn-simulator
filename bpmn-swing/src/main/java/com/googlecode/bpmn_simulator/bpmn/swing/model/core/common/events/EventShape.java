package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events;

import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.Event;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNShape;

@SuppressWarnings("serial")
public class EventShape<E extends Event>
		extends AbstractBPMNShape<E> {

	public EventShape(final E element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillOval(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		getPresentation().drawOval(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintTokens(Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
