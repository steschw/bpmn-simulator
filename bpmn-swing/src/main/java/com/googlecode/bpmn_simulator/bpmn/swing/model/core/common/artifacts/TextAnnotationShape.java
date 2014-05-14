package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoint;
import com.googlecode.bpmn_simulator.animation.element.visual.Waypoints;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public class TextAnnotationShape
		extends AbstractBPMNShape<TextAnnotation> {

	private static int HOR_LINE_LENGTH = 20;

	private static float STROKE_WIDTH = 2.f;

	private static final Stroke STROKE = new BasicStroke(STROKE_WIDTH);

	static {
		Appearance.getDefault().getForElement(TextAnnotationShape.class).setBackground(new Color(0xECF4FF));
	}

	public TextAnnotationShape(final TextAnnotation element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillRect(g, getInnerBoundsRelative());
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		g.setStroke(STROKE);
		final Bounds bounds = getInnerBoundsRelative();
		final Waypoints waypoints = new Waypoints();
		waypoints.add(new Waypoint(bounds.getMinX() + HOR_LINE_LENGTH, bounds.getMinY()));
		waypoints.add(new Waypoint(bounds.getMinX(), bounds.getMinY()));
		waypoints.add(new Waypoint(bounds.getMinX(), bounds.getMaxY()));
		waypoints.add(new Waypoint(bounds.getMinX() + HOR_LINE_LENGTH, bounds.getMaxY()));
		getPresentation().drawLine(g, waypoints);
	}

}
