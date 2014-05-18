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
package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.swing.Presentation;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.SubProcess;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
abstract class AbstractSubProcessShape<E extends SubProcess>
		extends AbstractActivityShape<E> {

	private static final Stroke EVENT_STROKE = Appearance.getDefault().createStrokeDotted(1);

	public AbstractSubProcessShape(final E element) {
		super(element);
	}

	@Override
	protected Stroke getStroke() {
		return getLogicalElement().isTriggeredByEvent()
				? EVENT_STROKE : super.getStroke();
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		final Presentation presentation = getPresentation();
		final Appearance appearance = Appearance.getDefault();
		final Bounds bounds = getInnerBoundsRelative();
		if (!isExpanded()) {
			final Image collapsedImage = appearance.getImage(Appearance.IMAGE_COLLAPSED);
			final Bounds collapsedBounds = bounds.centerBottom(collapsedImage.getWidth(null), collapsedImage.getHeight(null));
			presentation.drawImage(g, collapsedImage, collapsedBounds);
		}
	}

}
