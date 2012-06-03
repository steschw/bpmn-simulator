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
package bpmn.element;

import java.awt.Color;
import java.util.Iterator;

import bpmn.Graphics;
import bpmn.element.activity.ExpandedProcess;
import bpmn.element.gateway.EventBasedGateway;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCatchElement;
import bpmn.trigger.TriggerNotifyElement;

@SuppressWarnings("serial")
public abstract class TokenFlowElement extends FlowElement implements TokenFlow {

	private final TokenCollection innerTokens = new TokenCollection();

	public TokenFlowElement(final String id, final String name) {
		super(id, name);
	}

	public TokenCollection getInnerTokens() {
		return innerTokens;
	}

	@Override
	public TokenCollection getTokens() {
		return getInnerTokens();
	}

	@Override
	public void tokenEnter(final Token token) {
		addToken(token);
		repaint();
		//tokenDispatch(token);
	}

	@Override
	public void tokenDispatch(final Token token) {
//		assert getInnerTokens().contains(token);
		if (canForwardTokenToNextElement(token)) {
			tokenForwardToNextElement(token);
		}
		repaint();
	}

	@Override
	public void tokenExit(final Token token) {
		removeToken(token);
		repaint();
	}

	protected int getStepCount() {
		return 0;
	}

	protected boolean canForwardTokenToNextElement(final Token token) {
		return token.getSteps() >= getStepCount();
	}

	protected void addToken(final Token token) {
		getInnerTokens().add(token);
	}

	protected void removeToken(final Token token) {
		getInnerTokens().remove(token);
	}

	protected final void tokenForwardToNextElement(final Token token) {
		tokenForwardToNextElement(token, token.getInstance());
	}

	protected void tokenForwardToNextElement(final Token token,
			final Instance instance) {
		if (passTokenToAllOutgoing(token, instance)) {
			setException(false);
			token.remove();
		} else {
			setException(true);
		}
	}

	protected boolean hasToken() {
		final TokenCollection token = getInnerTokens();
		return (token != null) && !token.isEmpty(); 
	}

	protected boolean hasElementActiveToken(final Instance instance) {
		return !getInnerTokens().byInstance(instance).isEmpty(); 
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(final Instance instance) {
		if (hasElementActiveToken(instance)) {
			return true;
		} else {
			// Oder eines der eingehenden Elemente hat noch Token dieser Instanz
			for (ElementRef<SequenceFlow> incoming : getIncoming()) {
				if (incoming.hasElement()
						&& incoming.getElement().hasIncomingPathWithActiveToken(instance)) {
					return true;
				}
			}
			return false;
		}
	}

	protected boolean passTokenToAllOutgoing(final Token token) {
		return passTokenToAllOutgoing(token, token.getInstance());
	}

	protected boolean passTokenToAllOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			return passTokenToAllOutgoingSequenceFlows(token, instance) > 0;
		} else {
			return passTokenToParent(token, instance);
		}
	}

	protected boolean passTokenToParent(final Token token, final Instance instance) {
		final ExpandedProcess parentProcess = getProcess();
		if (parentProcess != null) {
			token.passTo(parentProcess, instance);
			return true;
		}
		return false;
	}

	protected boolean passTokenToFirstSequenceFlow(final Token token, final Instance instance) {
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if (outgoingRef.hasElement()) {
				final SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
					token.passTo(sequenceFlow, instance);
					return true;
				}
			}
		}
		return false;
	}

	protected int passTokenToAllOutgoingSequenceFlows(final Token token, final Instance instance) {
		int forewardCount = 0;
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if (outgoingRef.hasElement()) {
				final SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
					token.passTo(sequenceFlow, instance);
					++forewardCount;
				}
			}
		}
		return forewardCount;
	}

	@Override
	public Color getForeground() {
		return hasToken() ? Token.HIGHLIGHT_COLOR : Color.BLACK;
	}

	@Override
	protected void paintTokens(final Graphics g) {
		getInnerTokens().paintHorizontalRight(g, getElementInnerBounds().getRightTop());
	}

	protected boolean isGatewayCondition() {
		for (final ElementRef<SequenceFlow> incomingRef : getIncoming()) {
			if (incomingRef.hasElement()) {
				final SequenceFlow incoming = incomingRef.getElement();
				final FlowElement flowElement = incoming.getSource();
				if (flowElement instanceof EventBasedGateway) {
					return true;
				}
			}
		}
		return false;
	}

	protected void passFirstTokenToAllOutgoing() {
		final Iterator<Token> iterator = getTokens().iterator();
		if (iterator.hasNext()) {
			final Token firstToken = iterator.next();
			passTokenToAllOutgoing(firstToken);
			firstToken.remove();
		}
	}

	protected void passFirstInstanceTokenToAllNextElements(final Instance instance) {
		for (final Token token : getTokens()) {
			if (token.getInstance().equals(instance)) {
				passTokenToAllOutgoing(token);
				token.remove();
				break;
			}
		}
	}

	protected int notifyTriggerNotifyEvents(
			final TriggerCatchElement catchElement, final Trigger trigger) {
		int count = 0;
		for (final ElementRef<SequenceFlow> incomingRef : getIncoming()) {
			if (incomingRef.hasElement()) {
				final SequenceFlow incoming = incomingRef.getElement();
				final FlowElement flowElement = incoming.getSource();
				if (flowElement instanceof TriggerNotifyElement) {
					((TriggerNotifyElement)flowElement).eventTriggered(catchElement, trigger);
					++count;
				}
			}
		}
		return count;
	}

}
