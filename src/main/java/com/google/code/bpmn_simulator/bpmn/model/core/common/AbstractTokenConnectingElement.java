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
package com.google.code.bpmn_simulator.bpmn.model.core.common;

import java.awt.Color;

import com.google.code.bpmn_simulator.framework.ElementRef;
import com.google.code.bpmn_simulator.framework.Graphics;
import com.google.code.bpmn_simulator.framework.instance.Instance;
import com.google.code.bpmn_simulator.framework.token.Token;
import com.google.code.bpmn_simulator.framework.token.TokenCollection;
import com.google.code.bpmn_simulator.framework.token.TokenFlow;



@SuppressWarnings("serial")
public abstract class AbstractTokenConnectingElement
		extends AbstractConnectingElement
		implements TokenFlow {

	private ElementRef<AbstractTokenFlowElement> sourceRef;
	private ElementRef<AbstractTokenFlowElement> targetRef;

	private final TokenCollection tokens = new TokenCollection();

	public AbstractTokenConnectingElement(final String id, final String name,
			final ElementRef<AbstractTokenFlowElement> source,
			final ElementRef<AbstractTokenFlowElement> target) {
		super(id, name);
		setSourceRef(source);
		setTargetRef(target);
	}

	protected void setSourceRef(final ElementRef<AbstractTokenFlowElement> elementRef) {
		assert elementRef != null;
		sourceRef = elementRef;
	}

	public ElementRef<AbstractTokenFlowElement> getSourceRef() {
		return sourceRef;
	}

	protected static AbstractTokenFlowElement getElementFromElementRef(
			final ElementRef<AbstractTokenFlowElement> elementRef) {
		if ((elementRef != null) && elementRef.hasElement()) {
			return elementRef.getElement();
		}
		return null;
	}

	public AbstractTokenFlowElement getSource() {
		return getElementFromElementRef(getSourceRef());
	}

	protected void setTargetRef(final ElementRef<AbstractTokenFlowElement> elementRef) {
		assert elementRef != null;
		targetRef = elementRef;
	}

	public ElementRef<AbstractTokenFlowElement> getTargetRef() {
		return targetRef;
	}

	public AbstractTokenFlowElement getTarget() {
		return getElementFromElementRef(getTargetRef());
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
		return isTokenAtEnd(token);
	}

	protected void addToken(final Token token) {
		getTokens().add(token);
	}

	protected void removeToken(final Token token) {
		getTokens().remove(token);
	}

	protected void forwardToken(final Token token) {
		token.passTo(getTarget());
		token.remove();
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
			final AbstractTokenFlowElement source = getSource();
			if (source != null) {
				return source.hasIncomingPathWithActiveToken(instance);
			} else {
				return false;
			}
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

	@Override
	public boolean isEndNode() {
		return getTarget() == null;
	}

	@Override
	public boolean isTokenAtEnd(final Token token) {
		return token.getSteps() >= getLength();
	}

}
