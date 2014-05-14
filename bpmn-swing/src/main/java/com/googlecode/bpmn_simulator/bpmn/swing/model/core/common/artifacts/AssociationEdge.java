package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts;

import java.awt.Graphics2D;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class AssociationEdge
		extends AbstractBPMNEdge<Association> {

	private static final Stroke STROKE = Appearance.getDefault().createStrokeDashed(1);

	public AssociationEdge(final Association element) {
		super(element);
	}

	@Override
	protected void paintElementLine(final Graphics2D g) {
		g.setStroke(STROKE);
		super.paintElementLine(g);
	}

}
