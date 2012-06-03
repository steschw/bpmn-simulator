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
package bpmn.element.activity;

import bpmn.Graphics;
import bpmn.element.TokenFlowElementWithDefault;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

@SuppressWarnings("serial")
public abstract class AbstractActivity
		extends TokenFlowElementWithDefault
		implements Activity{

	private final TokenCollection incomingTokens = new TokenCollection();
	private final TokenCollection outgoingTokens = new TokenCollection();

	public AbstractActivity(final String id, final String name) {
		super(id, name);
	}

	protected final TokenCollection getIncomingTokens() {
		return incomingTokens;
	}

	protected final TokenCollection getOutgoingTokens() {
		return outgoingTokens;
	}

	@Override
	public TokenCollection getTokens() {
		final TokenCollection tokens = super.getTokens();
		tokens.addAll(getIncomingTokens());
		tokens.addAll(getOutgoingTokens());
		return tokens;
	}

	@Override
	protected void addToken(final Token token) {
		getIncomingTokens().add(token);
	}

	@Override
	protected void removeToken(final Token token) {
		if (getOutgoingTokens().contains(token)) {
			getOutgoingTokens().remove(token);
		} else if (getInnerTokens().contains(token)) {
			getInnerTokens().remove(token);
		} else if (getIncomingTokens().contains(token)) {
			getIncomingTokens().remove(token);
		} else {
			//assert false;
		}
	}

	protected boolean canForwardTokenToInner(final Token token) {
		return true;
	}

	protected void forwardTokenFromIncoming(final Token token) {
		getIncomingTokens().moveTo(getInnerTokens(), token);
	}

	protected boolean canForwardTokenToOutgoing(final Token token) {
		return token.getSteps() >= getStepCount();
	}

	protected void forwardTokenFromInner(final Token token) {
		getInnerTokens().moveTo(getOutgoingTokens(), token);
	}

	protected void forwardTokenFromOutgoing(final Token token) {
		tokenForwardToNextElement(token);
	}

	@Override
	public void tokenDispatch(final Token token) {
		if (getInnerTokens().contains(token)) {
			if (canForwardTokenToOutgoing(token)) {
				forwardTokenFromInner(token);
			}
		} else if (getIncomingTokens().contains(token)) {
			if (canForwardTokenToInner(token)) {
				forwardTokenFromIncoming(token);
			}
		} else if (getOutgoingTokens().contains(token)) {
			if (canForwardTokenToNextElement(token)) {
				forwardTokenFromOutgoing(token);
			}
		}
		repaint();
	}

	public boolean hasIncomingTokens() {
		final TokenCollection incomingTokens = getIncomingTokens();
		return (incomingTokens != null) && !incomingTokens.isEmpty();
	}

	public boolean hasOutgoingTokens() {
		final TokenCollection outgoingTokens = getOutgoingTokens();
		return (outgoingTokens != null) && !outgoingTokens.isEmpty();
	}

	@Override
	public boolean hasToken() {
		return super.hasToken() || hasIncomingTokens() || hasOutgoingTokens();
	}

	@Override
	public boolean hasElementActiveToken(final Instance instance) {
		return super.hasElementActiveToken(instance)
				|| !getIncomingTokens().byInstance(instance).isEmpty()
				|| !getOutgoingTokens().byInstance(instance).isEmpty();
	}

	protected void paintIncomingTokens(final Graphics g) {
		getIncomingTokens().paintVertical(g, getElementInnerBounds().getLeftCenter());
	}

	protected void paintInnerTokens(final Graphics g) {
		getInnerTokens().paintHorizontal(g, getElementInnerBounds().getCenterTop());
	}

	protected void paintOutgoingTokens(final Graphics g) {
		getOutgoingTokens().paintVertical(g, getElementInnerBounds().getRightCenter());
	}

	@Override
	protected void paintTokens(final Graphics g) {
		paintIncomingTokens(g);
		paintInnerTokens(g);
		paintOutgoingTokens(g);
	}

}
