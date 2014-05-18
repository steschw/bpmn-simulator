package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities;

import java.awt.Graphics2D;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.SubProcess;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class SubProcessPlane
		extends AbstractBPMNPlane<SubProcess> {

	private static final Stroke EVENT_STROKE = Appearance.getDefault().createStrokeDotted(1);

	public SubProcessPlane(final SubProcess element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		final Bounds bounds = new Bounds(0, 0, getWidth(), getHeight());
		getPresentation().fillRoundRect(g, bounds.shrink(MARGIN), Appearance.getDefault().getArcSize());
	}

	@Override
	protected Stroke getStroke() {
		return getLogicalElement().isTriggeredByEvent()
				? EVENT_STROKE : super.getStroke();
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		final Bounds bounds = new Bounds(0, 0, getWidth(), getHeight());
		getPresentation().drawRoundRect(g, bounds.shrink(MARGIN), Appearance.getDefault().getArcSize());
	}

}
