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

import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Scrollable;

import bpmn.element.BaseElement;
import bpmn.element.Graphics;
import bpmn.element.Label;
import bpmn.element.event.Event;
import bpmn.element.event.StartEvent;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenFlow;

public class ExpandedProcess extends Activity implements Scrollable {

	private static final long serialVersionUID = 1L;

	private static final int ARC_LENGTH = 20;

	private final Collection<BaseElement> elements = new ArrayList<BaseElement>();

	private CollapsedProcess collapsedProcess; 

	public ExpandedProcess(final String id, final String name) {
		super(id, name);
		setAutoscrolls(true);
	}

	public void addElement(final BaseElement element) {
		assert !elements.contains(element);
		elements.add(element);
		element.setParentProcess(this);
	}

	protected boolean containsTokenFlow(final TokenFlow tokenFlow) {
		for (BaseElement element : elements) {
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
		collapsedProcess.setBackground(getBackground());
		collapsedProcess.setForeground(getForeground());
		final ExpandedProcess parentProcess = getParentProcess();
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
		final Event startEvent = getStartEvent();
		if (startEvent == null) {
			final Collection<Activity> activities = getStartActivities();
			if (activities.isEmpty()) {
				token.passTo(this, subInstance);
			} else {
				for (Activity startActivity : activities) {
					token.passTo(startActivity, subInstance);
				}
			}
		} else {
			token.passTo(startEvent, subInstance);
		}
	}

	public void updateSizeByComponents() {
		setSize(getPreferredSize());
	}

	@Override
	public Dimension getPreferredSize() {
		return calcSizeByInnerComponents();
	}

	public Collection<Activity> getStartActivities() {
		final Collection<Activity> activities = new ArrayList<Activity>();
		for (BaseElement element : elements) {
			if (element instanceof Activity) {
				final Activity activity = (Activity)element;
				if (!activity.hasIncoming()) {
					activities.add(activity);
				}
			}
		}
		return activities;
	}

	public StartEvent getStartEvent() {
		StartEvent start = null;
		for (BaseElement element : elements) {
			if (element instanceof StartEvent) {
				final StartEvent event = (StartEvent)element;
				assert start == null;
				start = event;
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
	protected void paintBackground(final Graphics g) {
		super.paintBackground(g);

		final Paint paint = getBackgroundPaint();
		if (paint != null) {
			g.setPaint(paint);
			g.fillRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
		}
	}

	@Override
	protected void paintElement(final Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), ARC_LENGTH, ARC_LENGTH);
	}

	@Override
	public void createElementLabel() {
		super.createElementLabel();
		final Label label = getElementLabel(); 
		if (label != null) {
			label.setAlignCenter(false);
		}
	}

	@Override
	public void setElementLabelDefaultPosition() {
		final Point position = getElementLeftTop();
		position.translate(4, 4);
		getElementLabel().setLeftTopPosition(position);
	}

}
