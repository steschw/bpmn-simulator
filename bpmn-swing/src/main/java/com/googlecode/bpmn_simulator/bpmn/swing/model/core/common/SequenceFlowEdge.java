package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common;

import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNTokenEdge;

@SuppressWarnings("serial")
public class SequenceFlowEdge
		extends AbstractBPMNTokenEdge<SequenceFlow> {

	public SequenceFlowEdge(final SequenceFlow element) {
		super(element);
	}

	@Override
	protected void paintTokens(Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
