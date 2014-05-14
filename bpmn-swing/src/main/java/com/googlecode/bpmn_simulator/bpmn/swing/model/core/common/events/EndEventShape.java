package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class EndEventShape
		extends AbstractEventShape<EndEvent> {

	static {
		Appearance.getDefault().getForElement(EndEventShape.class).setBackground(new Color(0xFFA4A4));
	}

	public EndEventShape(final EndEvent element) {
		super(element);
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		g.setStroke(new BasicStroke(4));
		super.paintElementForeground(g);
	}

}
