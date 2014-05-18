package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities;

import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Transaction;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class TransactionShape
		extends AbstractSubProcessShape<Transaction> {

	private static final int PADDING = 4;

	public TransactionShape(final Transaction element) {
		super(element);
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		getPresentation().drawRoundRect(g,
				getInnerBoundsRelative().shrink(PADDING),
				Appearance.getDefault().getArcSize() - PADDING);
	}

}
