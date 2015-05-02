/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.Point;
import com.googlecode.bpmn_simulator.animation.element.visual.swing.Colors;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
public final class ExclusiveGatewayShape
		extends AbstractGatewayShape<ExclusiveGateway> {

	private static final Stroke SYMBOL_STROKE = new BasicStroke(3.f);

	static {
		Appearance.setDefaultColor(ExclusiveGatewayShape.class, Colors.ORANGE);
	}

	public ExclusiveGatewayShape(final ExclusiveGateway element) {
		super(element);
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		if (isMarkerVisible()) {
			g.setStroke(SYMBOL_STROKE);
			final Bounds symbolBounds = getInnerBoundsRelative().scaleSize(0.3f);
			getPresentation().drawLine(g,
					new Point(symbolBounds.getMinX(), symbolBounds.getMinY()),
					new Point(symbolBounds.getMaxX(), symbolBounds.getMaxY()));
			getPresentation().drawLine(g,
					new Point(symbolBounds.getMinX(), symbolBounds.getMaxY()),
					new Point(symbolBounds.getMaxX(), symbolBounds.getMinY()));
		}
	}

}
