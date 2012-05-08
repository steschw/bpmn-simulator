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
package bpmn.element.event;

import java.awt.Color;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;
import bpmn.element.VisualConfig;
import bpmn.token.InstanceController;
import bpmn.token.Token;

public class EndEvent extends Event {

	private static final long serialVersionUID = 1L;

	private static final int TERMINATION_MARGIN = 4;

	public EndEvent(final String id, final String name,
			final InstanceController tockenController) {
		super(id, name, tockenController);
	}

	@Override
	protected Color getElementBackgroundColor() {
		return getVisualConfig().getBackground(VisualConfig.Element.EVENT_END);
	}

	@Override
	protected int getBorderWidth() {
		return 3;
	}

	@Override
	protected void paintIcon(Graphics g) {
		super.paintIcon(g);

		if (isTermination()) {
			final Rectangle bounds = getElementInnerBounds();
			bounds.grow(-TERMINATION_MARGIN, -TERMINATION_MARGIN);
			g.fillOval(bounds);
		}
	}

	@Override
	protected void tokenForward(final Token token) {
		if (isTermination()) {
			token.getInstance().removeAllOtherTokens(token);
		}
		super.tokenForward(token);
	}

}
