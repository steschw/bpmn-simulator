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

import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

@SuppressWarnings("serial")
public abstract class TokenConnectingElement extends ConnectingElement
		implements TokenFlow {

	private final TokenCollection tokens = new TokenCollection();

	public TokenConnectingElement(final String id, final String name,
			final ElementRef<FlowElement> source, final ElementRef<FlowElement> target) {
		super(id, name, source, target);
	}

	@Override
	public TokenCollection getTokens() {
		return tokens;
	}

	@Override
	public void tokenEnter(final Token token) {
		addToken(token);
		repaint();
		tokenDispatch(token);
	}

	@Override
	public void tokenDispatch(final Token token) {
		assert getTokens().contains(token);
		if (canForwardToken(token)) {
			forwardToken(token);
		}
		repaint();
	}

	@Override
	public void tokenExit(final Token token) {
		removeToken(token);
		repaint();
	}

	protected boolean canForwardToken(final Token token) {
		return token.getSteps() >= getLength();
	}

	protected void addToken(final Token token) {
		getTokens().add(token);
	}

	protected void removeToken(final Token token) {
		getTokens().remove(token);
	}

	protected void forwardToken(final Token token) {
		final FlowElement flowElement = getTarget();
		if (flowElement instanceof TokenFlow) {
			token.passTo((TokenFlow)flowElement);
			token.remove();
		}
	}

	protected boolean hasElementActiveToken(final Instance instance) {
		return !getTokens().byInstance(instance).isEmpty(); 
	}

	@Override
	public boolean hasIncomingPathWithActiveToken(final Instance instance) {
		if (hasElementActiveToken(instance)) {
			return true;
		} else {
			// Oder eines der eingehenden Elemente hat noch Token dieser Instanz
			final FlowElement flowElement = getSource();
			if (flowElement instanceof TokenFlow) {
				return ((TokenFlow)flowElement).hasIncomingPathWithActiveToken(instance);
			}
			return false;
		}
	}

	@Override
	public Color getForeground() {
		final TokenCollection tokens = getTokens();
		if ((tokens != null) && !tokens.isEmpty()) {
			return Token.HIGHLIGHT_COLOR;
		}
		return super.getForeground();
	}

	@Override
	protected void paintTokens(final Graphics g) {
		final TokenCollection tokens = getTokens();
		synchronized (tokens) {
			for (Token token : tokens) {
				token.getInstance().paint(g, waypointToRelative(getPosition(token.getSteps())));
			}
		}
	}

}
