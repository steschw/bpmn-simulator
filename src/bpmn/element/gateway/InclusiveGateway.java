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

import bpmn.element.ElementRef;
import bpmn.element.Graphics;
import bpmn.element.SequenceFlow;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

public class InclusiveGateway extends Gateway {

	private static final long serialVersionUID = 1L;

	public InclusiveGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		g.drawOval(getSymbolBounds());
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(final Instance instance) {
		return false;
	}

	protected synchronized void forwardTokenParallel(final Instance instance) {
		final TokenCollection popTokens = new TokenCollection();
		for (ElementRef<SequenceFlow> incoming : getIncoming()) {
			if (incoming.hasElement()) {
				final SequenceFlow incomingSequenceFlow = (SequenceFlow)incoming.getElement();
				final Token incomingToken = getFirstTokenForIncoming(incoming.getElement(), instance);
				if (incomingToken == null) {
					// für diesen eingang ist noch kein token vorhanden
					if (incomingSequenceFlow.hasIncomingPathWithActiveToken(instance)) {
						// aber es kann noch eines ankommen
						return;
					}
				} else {
					popTokens.add(incomingToken);
				}
			}
		}
		forwardMergedTokensToAllOutgoing(popTokens);
	}

	public final boolean isForMerging() {
		return (getIncoming().size() > 1);
	}

	@Override
	protected void tokenForward(final Token token) {
		if (isForMerging()) {
			forwardTokenParallel(token.getInstance());
		} else {
			super.tokenForward(token);
		}
	}

}
