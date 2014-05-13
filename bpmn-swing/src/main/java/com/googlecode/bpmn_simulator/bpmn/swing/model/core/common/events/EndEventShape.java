package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;

@SuppressWarnings("serial")
public class EndEventShape
		extends EventShape<EndEvent> {

	public EndEventShape(final EndEvent element) {
		super(element);
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		g.setStroke(new BasicStroke(4));
		super.paintElementForeground(g);
	}

}
