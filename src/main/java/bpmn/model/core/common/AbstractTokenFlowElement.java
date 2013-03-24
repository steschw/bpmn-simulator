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
package bpmn.model.core.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import bpmn.Graphics;
import bpmn.instance.Instance;
import bpmn.model.ElementRef;
import bpmn.model.core.common.gateways.EventBasedGateway;
import bpmn.model.process.activities.AbstractContainerActivity;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;
import bpmn.trigger.InstantiableNotifiyTarget;
import bpmn.trigger.Trigger;
import bpmn.trigger.TriggerCatchingElement;

@SuppressWarnings("serial")
public abstract class AbstractTokenFlowElement
		extends AbstractFlowElement
		implements TokenFlow {

	private final Collection<ElementRef<SequenceFlow>> incomingRefs
			= new ArrayList<ElementRef<SequenceFlow>>();

	private final Collection<ElementRef<SequenceFlow>> outgoingRefs
			= new ArrayList<ElementRef<SequenceFlow>>();

	private final TokenCollection innerTokens = new TokenCollection();

	public AbstractTokenFlowElement(final String id, final String name) {
		super(id, name);
	}

	private Collection<ElementRef<SequenceFlow>> getIncomingRefs() {
		return incomingRefs;
	}

	private Collection<ElementRef<SequenceFlow>> getOutgoingRefs() {
		return outgoingRefs;
	}

	protected static Collection<SequenceFlow> getElementsFromElementRefs(
					final Collection<ElementRef<SequenceFlow>> elementRefs) {
		final Collection<SequenceFlow> elements = new ArrayList<SequenceFlow>();
		for (ElementRef<SequenceFlow> elementRef : elementRefs) {
			if ((elementRef != null) && elementRef.hasElement()) {
				elements.add(elementRef.getElement());
			}
		}
		return elements;
	}

	public Collection<SequenceFlow> getIncoming() {
		return getElementsFromElementRefs(incomingRefs);
	}

	public Collection<SequenceFlow> getOutgoing() {
		return getElementsFromElementRefs(outgoingRefs);
	}

	public boolean hasIncoming() {
		return !getIncomingRefs().isEmpty();
	}

	public void addIncomingRef(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !incomingRefs.contains(element)) {
			incomingRefs.add(element);
		}
	}

	public void addOutgoingRef(final ElementRef<SequenceFlow> element) {
		assert element != null;
		if ((element != null) && !outgoingRefs.contains(element)) {
			outgoingRefs.add(element);
		}
	}

	protected Collection<AbstractFlowElement> getIncomingFlowElements() {
		final Collection<AbstractFlowElement> incomingFlowElements = new ArrayList<AbstractFlowElement>();
		for (final SequenceFlow incoming : getIncoming()) {
			final AbstractFlowElement flowElement = incoming.getSource();
			if (flowElement != null) {
				incomingFlowElements.add(flowElement);
			}
		}
		return incomingFlowElements;
	}

	protected Collection<AbstractFlowElement> getOutgoingFlowElements() {
		final Collection<AbstractFlowElement> outgoingFlowElements = new ArrayList<AbstractFlowElement>();
		for (final SequenceFlow outgoing : getOutgoing()) {
			final AbstractFlowElement flowElement = outgoing.getTarget();
			if (flowElement != null) {
				outgoingFlowElements.add(flowElement);
			}
		}
		return outgoingFlowElements;
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
		return isTokenAtEnd(token);
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
		if (passTokenToAllNextElements(token, instance)) {
			token.remove();
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
			for (final AbstractTokenConnectingElement incoming : getIncoming()) {
				if (incoming.hasIncomingPathWithActiveToken(instance)) {
					return true;
				}
			}
			return false;
		}
	}

	protected final boolean passTokenToAllNextElements(final Token token) {
		return passTokenToAllNextElements(token, token.getInstance());
	}

	protected boolean passTokenToAllNextElements(final Token token, final Instance instance) {
		if (isEndNode()) {
			notifyTokenReachedEndNode(token);
			return false;
		} else {
			return passTokenToAllOutgoingSequenceFlows(token, instance) > 0;
		}
	}

	protected void notifyTokenReachedEndNode(final Token token) {
		final AbstractContainerActivity containerActivity = getContainerActivity();
		if (containerActivity != null) {
			containerActivity.tokenReachedEndNode(token);
		}
	}

	protected boolean passTokenToFirstSequenceFlow(final Token token, final Instance instance) {
		for (final SequenceFlow outgoing : getOutgoing()) {
			if (outgoing.acceptsToken() && !outgoing.isDefault()) {
				token.passTo(outgoing, instance);
				return true;
			}
		}
		return false;
	}

	protected int passTokenToAllOutgoingSequenceFlows(final Token token, final Instance instance) {
		int forewardCount = 0;
		for (final SequenceFlow outgoing : getOutgoing()) {
			if (outgoing.acceptsToken() && !outgoing.isDefault()) {
				token.passTo(outgoing, instance);
				++forewardCount;
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
		for (final AbstractTokenConnectingElement incoming : getIncoming()) {
			if (incoming.getSource() instanceof EventBasedGateway) {
				return true;
			}
		}
		return false;
	}

	public void passAllTokenToAllNextElements() {
		final Collection<Token> tokens = new ArrayList<Token>(getTokens());
		for (final Token token : tokens) {
			passTokenToAllNextElements(token);
			token.remove();
		}
	}

	public void passFirstTokenToAllNextElements() {
		final Iterator<Token> iterator = getTokens().iterator();
		if (iterator.hasNext()) {
			final Token firstToken = iterator.next();
			passTokenToAllNextElements(firstToken);
			firstToken.remove();
		}
	}

	public void passFirstInstanceTokenToAllNextElements(final Instance instance) {
		for (final Token token : getTokens()) {
			if (token.getInstance().equals(instance)) {
				passTokenToAllNextElements(token);
				token.remove();
				break;
			}
		}
	}

	protected boolean areAllIncommingFlowElementsInstantiableNotifyTargets() {
		final Collection<AbstractFlowElement> incomingFlowElements = getIncomingFlowElements();
		if (incomingFlowElements.isEmpty()) {
			return false;
		}
		for (final AbstractFlowElement flowElement : incomingFlowElements) {
			if (!((flowElement instanceof InstantiableNotifiyTarget)
					&& ((InstantiableNotifiyTarget)flowElement).isInstantiable())) {
				return false;
			}
		}
		return true;
	}

	protected int notifyInstantiableIncomingFlowElements(
			final TriggerCatchingElement catchElement, final Trigger trigger) {
		int count = 0;
		for (final AbstractFlowElement flowElement : getIncomingFlowElements()) {
			if (flowElement instanceof InstantiableNotifiyTarget) {
				((InstantiableNotifiyTarget)flowElement).eventTriggered(catchElement, trigger);
				++count;
			}
		}
		return count;
	}

	@Override
	public boolean isEndNode() {
		return getOutgoingRefs().isEmpty();
	}

	@Override
	public boolean isTokenAtEnd(final Token token) {
		return token.getSteps() >= getStepCount();
	}

}
