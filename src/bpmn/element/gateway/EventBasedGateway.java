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
import bpmn.element.Rectangle;
import bpmn.element.SequenceFlow;
import bpmn.element.event.CatchEvent;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

@SuppressWarnings("serial")
public class EventBasedGateway extends Gateway {

	public EventBasedGateway(final String id, final String name) {
		super(id, name);
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		final Rectangle bounds = getElementInnerBounds();
		bounds.shrinkHalf();
		g.drawOval(bounds);
		bounds.shrink(2, 2, 2, 2);
		g.drawOval(bounds);
		bounds.shrink(2, 2, 2, 2);
		g.drawPentagon(bounds);
	}

	protected SequenceFlow getSequenceFlowToEvent(final CatchEvent catchEvent) {
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if (outgoingRef.hasElement()) {
				final SequenceFlow outgoing = outgoingRef.getElement();
				if (catchEvent.equals(outgoing.getTarget())) {
					return outgoing;
				}
			}
		}
		return null;
	}

	public void eventHappen(final CatchEvent catchEvent, final Instance instance) {
		final SequenceFlow sequenceFlow = getSequenceFlowToEvent(catchEvent);
		if (sequenceFlow != null) {
			final TokenCollection tokens = getInnerTokens().byInstance(instance);
			if (!tokens.isEmpty()) {
				final Token token = tokens.firstElement();
				token.passTo(sequenceFlow);
				token.remove();
			}
		}
	}

	@Override
	protected boolean canForwardTokenToNextElement(Token token) {
		return false;
	}

}
