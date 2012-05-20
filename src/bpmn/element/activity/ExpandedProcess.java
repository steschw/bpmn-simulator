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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Scrollable;

import bpmn.Model;
import bpmn.element.VisibleElement;
import bpmn.element.Graphics;
import bpmn.element.Label;
import bpmn.element.TokenFlowElement;
import bpmn.element.Visualization;
import bpmn.element.event.AbstractEvent;
import bpmn.element.event.StartEvent;
import bpmn.element.gateway.Gateway;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenFlow;

@SuppressWarnings("serial")
public class ExpandedProcess extends Activity implements Scrollable {

	private static final int ARC_LENGTH = 20;

	private final Collection<VisibleElement> elements = new ArrayList<VisibleElement>();

	private CollapsedProcess collapsedProcess; 

	private final Model model;

	public ExpandedProcess(final Model model, final String id, final String name) {
		super(id, name);
		this.model = model;
		setAutoscrolls(true);
	}

	public ExpandedProcess(final String id, final String name) {
		this(null, id, name);
	}

	public Model getModel() {
		if (model == null) {
			final ExpandedProcess parentProcess = getProcess();
			return (parentProcess == null) ? null : parentProcess.getModel();
		} else {
			return model;
		}
	}

	public void addElement(final VisibleElement element) {
		assert !elements.contains(element);
		elements.add(element);
		element.setProcess(this);
	}

	public Collection<VisibleElement> getElements() {
		return elements;
	}

	protected boolean containsTokenFlow(final TokenFlow tokenFlow) {
		for (VisibleElement element : elements) {
			if (element instanceof TokenFlow) {
				if ((TokenFlow)element == tokenFlow) {
					return true;
				}
			}
		}
		return false;
	}

	public CollapsedProcess createCollapsed() {
		assert collapsedProcess == null;
		collapsedProcess = new CollapsedProcess(this);
		collapsedProcess.setForeground(getForeground());
		final ExpandedProcess parentProcess = getProcess();
		if (parentProcess != null) {
			parentProcess.addElement(collapsedProcess);
		}
		return collapsedProcess;
	}

	@Override
	protected void forwardTokenFromIncoming(final Token token) {
		passTokenToInner(token);
		token.remove();
		repaint();
	}

	@Override
	protected void forwardTokenFromInner(final Token token) {
		getOutgoingTokens().add(token);
	}

	@Override
	protected void forwardTokenFromOutgoing(final Token token) {
		final Instance tokenInstance = token.getInstance();
		super.forwardTokenFromOutgoing(token);
		if (tokenInstance != null) {
			tokenInstance.removeIfHasNoTokens();
		}
	}

	protected boolean isTokenFromInnerElement(final Token token) {
		final TokenFlow from = token.getPreviousFlow();
		return from.equals(this) || containsTokenFlow(from);
	}

	@Override
	public void tokenEnter(final Token token) {
		if (isTokenFromInnerElement(token)) {
			forwardTokenFromInner(token);		
			repaint();
		} else {
			super.tokenEnter(token);
		}
	}

	protected boolean areAllTokenAtEnd(final Instance instance) {
		final int exitTokenCount = getOutgoingTokens().byInstance(instance).getCount();
		return exitTokenCount == instance.getTokenCount();
	}

	@Override
	protected boolean canForwardToken(final Token token) {
		return super.canForwardToken(token) && areAllTokenAtEnd(token.getInstance());
	}

	@Override
	protected boolean passTokenToAllOutgoing(final Token token) {
		final Instance subInstance = token.getInstance(); 
		final Instance parentInstance = subInstance.getParentInstance();
		boolean forwarded = true; // der hauptprozess hat keine ausgehenden sequence flows
		if (parentInstance != null) {
			forwarded = super.passTokenToAllOutgoing(token, parentInstance);
		}
		if (collapsedProcess != null) {
			collapsedProcess.removeInstance(subInstance);
		}
		return forwarded;
	}

	protected void passTokenToInner(final Token token) {
		final Instance subInstance = token.getInstance().newChildInstance();
		if (collapsedProcess != null) {
			collapsedProcess.addInstance(subInstance);
		}
		final AbstractEvent startEvent = getStartEvent();
		if (startEvent == null) {
			final Collection<TokenFlowElement> startElements = getStartElements();
			if (startElements.isEmpty()) {
				token.passTo(this, subInstance);
			} else {
				for (TokenFlowElement startElement : startElements) {
					token.passTo(startElement, subInstance);
				}
			}
		} else {
			token.passTo(startEvent, subInstance);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByInnerComponents();
	}

	public Collection<TokenFlowElement> getStartElements() {
		final Collection<TokenFlowElement> startElements = new ArrayList<TokenFlowElement>();
		for (VisibleElement element : elements) {
			if (element instanceof TokenFlowElement) {
				final TokenFlowElement tokenFlowElement = (TokenFlowElement)element; 
				if (!tokenFlowElement.hasIncoming()) {
					if (tokenFlowElement instanceof Activity
							|| tokenFlowElement instanceof Gateway) {
						startElements.add(tokenFlowElement);
					}
				}
			}
		}
		return startElements;
	}

	public StartEvent getStartEvent() {
		StartEvent start = null;
		for (VisibleElement element : elements) {
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

}
