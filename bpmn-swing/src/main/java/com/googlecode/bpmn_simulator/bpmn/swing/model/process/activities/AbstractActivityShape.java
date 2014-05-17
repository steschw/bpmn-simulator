package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities;

import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Activity;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNTokenShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public abstract class AbstractActivityShape<E extends Activity>
		extends AbstractBPMNTokenShape<E> {

	public AbstractActivityShape(final E element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillRoundRect(g, getInnerBoundsRelative(), Appearance.getDefault().getArcSize());
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		getPresentation().drawRoundRect(g, getInnerBoundsRelative(), Appearance.getDefault().getArcSize());
	}

	@Override
	protected void paintTokens(final Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
