package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways;

import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.Gateway;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNTokenShape;

@SuppressWarnings("serial")
abstract class AbstractGatewayShape<E extends Gateway>
		extends AbstractBPMNTokenShape<E> {

	public AbstractGatewayShape(final E element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillDiamond(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		getPresentation().drawDiamond(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintTokens(final Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
