package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts;

import java.awt.Graphics2D;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class GroupShape
		extends AbstractBPMNShape<Group> {

	private static final Stroke STROKE = Appearance.getDefault().createStrokeDashedDotted(1);

	public GroupShape(final Group element) {
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
		g.setStroke(STROKE);
		getPresentation().drawRoundRect(g, getInnerBoundsRelative(), Appearance.getDefault().getArcSize());
	}

}
