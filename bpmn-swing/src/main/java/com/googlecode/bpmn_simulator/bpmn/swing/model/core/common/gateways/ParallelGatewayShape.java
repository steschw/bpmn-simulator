package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways;

import java.awt.Color;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class ParallelGatewayShape
		extends AbstractGatewayShape<ParallelGateway> {

	static {
		Appearance.getDefault().getForElement(ParallelGatewayShape.class).setBackground(new Color(0xFFCEA2));
	}

	public ParallelGatewayShape(final ParallelGateway element) {
		super(element);
	}

}
