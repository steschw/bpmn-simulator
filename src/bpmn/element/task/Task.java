/*
 * Copyright (C) 2012 Stefan Schweitzer
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
package bpmn.element.task;

import java.awt.Point;
import java.net.URL;

import javax.swing.ImageIcon;

import bpmn.element.Activity;
import bpmn.element.Graphics;
import bpmn.element.Rectangle;

public class Task extends Activity {

	private static final long serialVersionUID = 1L;

	private ImageIcon typeIcon = null;

	protected static ImageIcon loadTypeIcon(final String filename) {
		final URL url = Task.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	public Task(final String id, final String name) {
		super(id, name);
	}

	protected Task(final String id, final String name, final ImageIcon icon) {
		this(id, name);
		this.typeIcon = icon;
	}

	@Override
	protected int getStepCount() {
		return 50;
	}

	@Override
	protected void paintBackground(Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), 10, 10);
	}

	public void paintTypeIcon(Graphics g) {
		if (typeIcon != null) {
			final Rectangle innerBounds = getElementInnerBounds();
			final Point position = innerBounds.getLeftTop();
			position.translate(6, 6);
			g.drawIcon(typeIcon, position);
		}
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), 10, 10);
		paintTypeIcon(g);
	}

}
