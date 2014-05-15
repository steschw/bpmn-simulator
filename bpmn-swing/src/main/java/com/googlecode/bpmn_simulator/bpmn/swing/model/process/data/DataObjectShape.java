package com.googlecode.bpmn_simulator.bpmn.swing.model.process.data;

import java.awt.Color;
import java.awt.Graphics2D;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.Point;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObject;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class DataObjectShape
		extends AbstractBPMNShape<DataObject> {

	static {
		Appearance.getDefault().getForElement(DataObjectShape.class).setBackground(new Color(0xEEEEEE));
	}

	public DataObjectShape(final DataObject element) {
		super(element);
	}

	private int getN(final Bounds bounds) {
		return (int) Math.round(Math.min(bounds.getWidth(), bounds.getHeight()) * 0.25);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		final Bounds bounds = getInnerBoundsRelative();
		getPresentation().fillDocument(g, bounds, getN(bounds));
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		final Bounds bounds = getInnerBoundsRelative();
		final int n = getN(bounds);
		getPresentation().drawDocument(g, bounds, n);
		final Point p = new Point(bounds.getMaxX() - n, bounds.getMinY() + n);
		getPresentation().drawLine(g,
				new Point(bounds.getMaxX() - n, bounds.getMinY()), p);
		getPresentation().drawLine(g,
				new Point(bounds.getMaxX(), bounds.getMinY() + n), p);
	}

}
