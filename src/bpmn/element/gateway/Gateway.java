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
package bpmn.element.gateway;

import java.awt.Color;
import java.awt.Point;

import bpmn.Graphics;
import bpmn.element.Label;
import bpmn.element.Rectangle;
import bpmn.element.SequenceFlow;
import bpmn.element.TokenFlowElementWithDefault;
import bpmn.element.Visualization;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

@SuppressWarnings("serial")
public abstract class Gateway extends TokenFlowElementWithDefault {

	private static final int SYMBOL_MARGIN = 14;

	public Gateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected int getStepCount() {
		return 10;
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.GATEWAY);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fillDiamond(getElementInnerBounds());
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawDiamond(getElementInnerBounds());
	}

	protected Rectangle getSymbolBounds() {
		final Rectangle bounds = getElementInnerBounds();
		bounds.grow(-SYMBOL_MARGIN, -SYMBOL_MARGIN);
		return bounds;
	}

	protected final void forwardMergedTokensToAllOutgoing(final TokenCollection tokens) {
		final Token mergedToken = tokens.merge();
		if (mergedToken != null) {
			passTokenToAllNextElements(mergedToken);
			mergedToken.remove();
		}
	}

	protected Token getFirstTokenForIncoming(final SequenceFlow sequenceFlow,
			final Instance instance) {
		for (Token token : getInnerTokens().byInstance(instance)) {
			if (sequenceFlow.equals(token.getPreviousFlow())) {
				return token;
			}
		}
		return null;
	}

	@Override
	public Label createElementLabel() {
		final Label label = super.createElementLabel();
		if (label != null) {
			label.setAlignCenter(false);
		}
		return label;
	}

	@Override
	public void updateElementLabelPosition() {
		final Rectangle innerBounds = getInnerBounds(); 
		final Point position = innerBounds.getRightBottom();
		position.translate(
				-(int)(innerBounds.getWidth() / 4),
				-(int)(innerBounds.getHeight() / 4));
		getElementLabel().setLeftTopPosition(position);
	}

}
