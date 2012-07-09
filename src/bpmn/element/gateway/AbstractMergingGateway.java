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

import java.awt.Point;

import bpmn.Graphics;
import bpmn.element.Rectangle;
import bpmn.element.SequenceFlow;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

@SuppressWarnings("serial")
public abstract class AbstractMergingGateway extends AbstractGateway {

	protected static final int TOKEN_MARGIN = 5;

	public AbstractMergingGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void tokenForwardToNextElement(final Token token, final Instance instance) {
		forwardTokenParallel(instance);
	}

	protected abstract void forwardTokenParallel(final Instance instance);

	protected final void forwardMergedTokensToAllOutgoing(final TokenCollection tokens) {
		final Token mergedToken = tokens.merge();
		if (mergedToken != null) {
			passTokenToAllNextElements(mergedToken);
			mergedToken.remove();
		}
	}

	@Override
	protected void paintTokens(final Graphics g) {
		final Rectangle bounds = getElementInnerBounds();
		int y = bounds.y;
		for (Instance tokenInstance : getInnerTokens().getInstances()) {
			int x = bounds.x + (int)bounds.getWidth();
			final TokenCollection instanceTokens = getInnerTokens().byInstance(tokenInstance);
			for (final SequenceFlow incoming : getIncoming()) {
				final int count = instanceTokens.byPreviousFlow(incoming).getCount();
				if (count > 0) {
					tokenInstance.paint(g, new Point(x, y), count);
				}
				x -= TOKEN_MARGIN;
			}
			y += TOKEN_MARGIN;
		}
	}

}
