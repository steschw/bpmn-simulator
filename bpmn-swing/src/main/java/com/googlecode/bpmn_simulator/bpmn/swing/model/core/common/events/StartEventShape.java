package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events;

import java.awt.Color;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class StartEventShape
		extends AbstractEventShape<StartEvent> {

	static {
		Appearance.getDefault().getForElement(StartEventShape.class).setBackground(new Color(0xA4F0B7));
	}

	public StartEventShape(final StartEvent element) {
		super(element);
	}

}
