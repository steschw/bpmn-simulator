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
package com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task;

import java.awt.Graphics2D;
import java.awt.Image;

import com.googlecode.bpmn_simulator.animation.element.visual.Bounds;
import com.googlecode.bpmn_simulator.animation.element.visual.Label;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.AbstractActivityShape;

@SuppressWarnings("serial")
abstract class AbstractTaskShape<E extends Task>
		extends AbstractActivityShape<E> {

	private static final int IMAGE_WIDTH = 16;
	private static final int IMAGE_HEIGHT = 16;

	public AbstractTaskShape(final E element) {
		super(element);
	}

	@Override
	public void alignLabel(final Label label) {
		final Bounds innerBounds = getInnerBounds();
		if (getTaskImage() == null) {
			label.setBounds(innerBounds);
		} else {
			label.setBounds(innerBounds.shrinkLeft(getPadding() * 2 + IMAGE_WIDTH));
		}
	}

	protected Image getTaskImage() {
		return null;
	}

	@Override
	protected void paintElementForeground(final Graphics2D g) {
		super.paintElementForeground(g);
		final Image image = getTaskImage();
		if (image != null) {
			final Bounds bounds = getInnerBoundsRelative();
			final int padding = getPadding();
			final Bounds imageBounds = new Bounds(
					bounds.getMinX() + padding,
					bounds.getMinY() + padding,
					IMAGE_WIDTH, IMAGE_HEIGHT);
			getPresentation().drawImage(g, image, imageBounds);
		}
	}

}
