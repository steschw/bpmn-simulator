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
package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;

import java.awt.Graphics2D;
import java.awt.Image;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.swing.di.AbstractBPMNTokenShape;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;

@SuppressWarnings("serial")
abstract class AbstractTaskShape<E extends Task>
		extends AbstractBPMNTokenShape<E> {

	private static final int IMAGE_WIDTH = 16;
	private static final int IMAGE_HEIGHT = 16;

	public AbstractTaskShape(final E element) {
		super(element);
	}

	@Override
	protected void paintElementBackground(final Graphics2D g) {
		super.paintElementBackground(g);
		getPresentation().fillRoundRect(g, getInnerBoundsRelative(), Appearance.getDefault().getArcSize());
	}

	protected Image getTaskImage() {
		return null;
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		final int arcSize = Appearance.getDefault().getArcSize();
		getPresentation().drawRoundRect(g, getInnerBoundsRelative(), arcSize);
		final Image image = getTaskImage();
		if (image != null) {
			final Bounds bounds = getInnerBoundsRelative();
			final int imageMargin = arcSize / 2;
			final Bounds imageBounds = new Bounds(
					bounds.getMinX() + imageMargin,
					bounds.getMinY() + imageMargin,
					IMAGE_WIDTH, IMAGE_HEIGHT);
			getPresentation().drawImage(g, image, imageBounds);
		}
	}

	@Override
	protected void paintTokens(final Graphics2D g) {
		// TODO Auto-generated method stub
	}

}
