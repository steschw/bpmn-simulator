/*
 * Copyright (C) 2014 Stefan Schweitzer
 *
 * This software was created by Stefan Schweitzer as a student's project at
 * Fachhochschule Kaiserslautern (University of Applied Sciences).
 * Supervisor: Professor Dr. Thomas Allweyer. For more information please see
 * http://www.fh-kl.de/~allweyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this Software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	private static final Stroke STROKE = new BasicStroke(1);

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
