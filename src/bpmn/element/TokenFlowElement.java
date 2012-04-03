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
import java.awt.Point;

import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

public abstract class TokenFlowElement extends FlowElement implements TokenFlow {

	private static final long serialVersionUID = 1L;

	private TokenCollection tokens = new TokenCollection();

	public TokenFlowElement(final String id, final String name) {
		super(id, name);
	}

	protected TokenCollection getTokens() {
		return tokens;
	}

	@Override
	public void tokenEnter(Token token) {
		addToken(token);
		tokenDispatch(token);
	}

	@Override
	public void tokenDispatch(Token token) {
		assert(getTokens().contains(token));
		if (canForwardToken(token)) {
			tokenForward(token);
		}
		repaint();
	}

	@Override
	public void tokenExit(Token token) {
		removeToken(token);
	}

	protected int getStepCount() {
		return 0;
	}

	protected boolean canForwardToken(Token token) {
		return (token.getSteps() >= getStepCount());
	}

	protected void addToken(Token token) {
		getTokens().add(token);
		repaint();
	}

	protected void removeToken(Token token) {
		getTokens().remove(token);
		repaint();
	}

	protected void tokenForward(Token token) {
		if (forwardTokenToAllOutgoing(token)) {
			setException(false);
			token.remove();
		} else {
			setException(true);
		}
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(Instance instance) {
		if (getTokens().byInstance(instance).getCount() > 0) {
			// Entweder das Element selbst hat noch Token dieser Instanz
			return true;
		} else {
			// oder eines der eingehenden
			for (ElementRef<SequenceFlow> incoming : getIncoming()) {
				if ((incoming != null) && incoming.hasElement()) {
					if (incoming.getElement().hasIncomingPathWithActiveToken(instance)) {
						return true;
					}
				}
			}
			return false;
		}
	}

	protected final boolean forwardTokenToFirstOutgoing(final Token token) {
		return forwardTokenToFirstOutgoing(token, token.getInstance());
	}

	protected final boolean forwardTokenToFirstOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (forwardTokenToFirstSequenceFlow(token, instance)) {
				return true;
			} else {
				return forwardTokenToDefaultSequenceFlow(token, instance);
			}
		} else {
			return forwardTokenToParent(token, instance);
		}
	}

	protected boolean forwardTokenToAllOutgoing(final Token token) {
		return forwardTokenToAllOutgoing(token, token.getInstance());
	}

	protected boolean forwardTokenToAllOutgoing(final Token token, final Instance instance) {
		if (hasOutgoing()) {
			if (forwardTokenToAllSequenceFlows(token, instance) == 0) {
				return forwardTokenToDefaultSequenceFlow(token, instance);
			} else {
				return true;
			}
		} else {
			return forwardTokenToParent(token, instance);
		}
	}

	private final boolean forwardTokenToParent(final Token token, final Instance instance) {
		final ExpandedProcess parentProcess = getParentProcess();
		if (parentProcess != null) {
			token.passTo(parentProcess, instance);
			return true;
		}
		return false;
	}

	private final boolean forwardTokenToFirstSequenceFlow(final Token token, final Instance instance) {
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if ((outgoingRef != null) && outgoingRef.hasElement()) {
				SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow != null) {
					if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
						token.passTo(sequenceFlow, instance);
						return true;
					}
				}
			}
		}
		return false;
	}

	private final int forwardTokenToAllSequenceFlows(final Token token, final Instance instance) {
		int forewardCount = 0;
		for (ElementRef<SequenceFlow> outgoingRef : getOutgoing()) {
			if ((outgoingRef != null) && outgoingRef.hasElement()) {
				final SequenceFlow sequenceFlow = outgoingRef.getElement();
				if (sequenceFlow.acceptsToken() && !sequenceFlow.isDefault()) {
					token.passTo(sequenceFlow, instance);
					++forewardCount;
				}
			}
		}
		return forewardCount;
	}

	private final boolean forwardTokenToDefaultSequenceFlow(final Token token, final Instance instance) {
		if (this instanceof ElementWithDefaultSequenceFlow) {
			final ElementRef<SequenceFlow> defaultSequenceFlowRef = ((ElementWithDefaultSequenceFlow)this).getDefaultElementFlowRef();
			if ((defaultSequenceFlowRef != null) && defaultSequenceFlowRef.hasElement()) {
				token.passTo(defaultSequenceFlowRef.getElement(), instance);
				return true;
			}
		}
		return false;
	}

	@Override
	public Color getForeground() {
		final TokenCollection tokens = getTokens();
		if ((tokens != null) && (tokens.getCount() > 0)) {
			return Token.HIGHLIGHT_COLOR;
		}
		return super.getForeground();
	}

	@Override
	protected void paintTokens(Graphics g) {
		Rectangle bounds = getElementInnerBounds();
		Point point = new Point((int)bounds.getMaxX(), (int)bounds.getMinY());
		for (Instance instance : getTokens().getInstances()) {
			instance.paint(g, point, getTokens().byInstance(instance).byCurrentFlow(this).getCount());
			point.translate(-5, 0);
		}
	}

}
