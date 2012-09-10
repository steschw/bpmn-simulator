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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Scrollable;

import bpmn.Graphics;
import bpmn.Model;
import bpmn.element.AbstractTokenFlowElement;
import bpmn.element.Element;
import bpmn.element.Label;
import bpmn.element.VisibleElement;
import bpmn.element.Visualization;
import bpmn.element.event.AbstractEvent;
import bpmn.element.event.StartEvent;
import bpmn.element.gateway.AbstractGateway;
import bpmn.instance.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;
import bpmn.token.TokenFlow;

@SuppressWarnings("serial")
public abstract class AbstractContainerActivity
		extends AbstractActivity
		implements Scrollable {

	protected static final int ARC_LENGTH = 20;

	private final Collection<Element> elements = new ArrayList<Element>();

	private final boolean triggeredByEvent;

	private final Model model;

	public AbstractContainerActivity(final Model model, final String id,
			final String name, final boolean triggeredByEvent) {
		super(id, name);
		this.model = model;
		this.triggeredByEvent = triggeredByEvent;
		setAutoscrolls(true);
	}

	@Override
	public Model getModel() {
		return (model == null) ? super.getModel() : model;
	}

	public boolean isTriggeredByEvent() {
		return triggeredByEvent;
	}

	public void addElement(final VisibleElement element) {
		assert !elements.contains(element);
		elements.add(element);
		element.setContainerActivity(this);
	}

	public Collection<Element> getElements() {
		return elements;
	}

	public TokenCollection getAllInnerTokens() {
		final TokenCollection innerTokens = new TokenCollection(); 
		final Collection<Element> elements = getElements();
		if (elements != null) {
			for (final Element innerElement : elements) {
				if (innerElement instanceof TokenFlow) {
					final TokenFlow innerTokenFlow = (TokenFlow)innerElement;
					innerTokens.addAll(innerTokenFlow.getTokens());
				}
			}
		}
		return innerTokens;
	}

	@Override
	public TokenCollection getTokens() {
		final TokenCollection innerTokens = new TokenCollection(getIncomingTokens());
		innerTokens.addAll(getAllInnerTokens());
		innerTokens.addAll(getOutgoingTokens());
		return innerTokens;
	}

	protected boolean containsTokenFlow(final TokenFlow tokenFlow) {
		for (Element element : getElements()) {
			if (element instanceof TokenFlow) {
				if ((TokenFlow)element == tokenFlow) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Stroke getStroke() {
		if (isTriggeredByEvent()) {
			return new BasicStroke(getBorderWidth(),
					BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f,
					new float[] { 2.f, 3.f }, 0); 
		} else {
			return super.getStroke();
		}
	}

	@Override
	protected void forwardTokenFromIncoming(final Token token) {
		passTokenToInner(token);
		repaint();
	}

	protected void passTokenToInner(final Token token) {
		final AbstractEvent startEvent = getPlainStartEvent();
		if (startEvent == null) {
			final Collection<AbstractTokenFlowElement> startElements = getStartElements();
			if (startElements.isEmpty()) {
				super.forwardTokenFromIncoming(token);
			} else {
				final Instance subInstance = token.getInstance().newChildInstance(this);
				for (final AbstractTokenFlowElement startElement : startElements) {
					token.passTo(startElement, subInstance);
				}
				token.remove();
			}
		} else {
			token.passTo(startEvent, token.getInstance().newChildInstance(this)); 
			token.remove();
		}
	}

	protected boolean isTokenFromInnerElement(final Token token) {
		final TokenFlow from = token.getPreviousFlow();
		return this.equals(from) || containsTokenFlow(from);
	}

	@Override
	public void tokenEnter(final Token token) {
		assert !isTokenFromInnerElement(token);
		super.tokenEnter(token);
	}

	protected boolean areAllTokenAtEnd(final Instance instance) {
		final int exitTokenCount = getOutgoingTokens().byInstance(instance).getCount();
		return exitTokenCount == instance.getTokenCount();
	}

	@Override
	protected boolean canForwardTokenToNextElement(final Token token) {
		return super.canForwardTokenToNextElement(token) && areAllTokenAtEnd(token.getInstance());
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByInnerComponents();
	}

	public Collection<AbstractTokenFlowElement> getStartElements() {
		final Collection<AbstractTokenFlowElement> startElements = new ArrayList<AbstractTokenFlowElement>();
		for (Element element : getElements()) {
			if (element instanceof AbstractTokenFlowElement) {
				final AbstractTokenFlowElement tokenFlowElement = (AbstractTokenFlowElement)element; 
				if (!tokenFlowElement.hasIncoming()) {
					if (tokenFlowElement instanceof AbstractActivity
							|| tokenFlowElement instanceof AbstractGateway) {
						startElements.add(tokenFlowElement);
					}
				}
			}
		}
		return startElements;
	}

	public StartEvent getPlainStartEvent() {
		StartEvent start = null;
		for (Element element : getElements()) {
			if (element instanceof StartEvent) {
				final StartEvent event = (StartEvent)element;
				if (event.isPlain()) {
					assert start == null;
					start = event;
				}
			}
		}
		return start;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(final Rectangle arg0,
			final int arg1, final int arg2) {
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(final Rectangle arg0,
			final int arg1, final int arg2) {
		return 0;
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.PROCESS);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
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
		final Point position = getInnerBounds().getLeftTop();
		position.translate(4, 4);
		getElementLabel().setLeftTopPosition(position);
	}

	public void tokenReachedEndNode(final Token token) {
		final Instance instance = token.getInstance();
		if (instance.isEnded()) {
			final Instance parentInstance = instance.getParent();
			if (parentInstance != null) {
				getOutgoingTokens().add(parentInstance.assignNewToken(this));
			}
			instance.remove();
		}
	}

}
