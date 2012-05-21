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
package bpmn.element.event;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;

import bpmn.element.ElementRef;
import bpmn.element.Graphics;
import bpmn.element.Visualization;
import bpmn.element.activity.Activity;
import bpmn.token.Instance;
import bpmn.token.Token;
import bpmn.token.TokenCollection;

@SuppressWarnings("serial")
public final class BoundaryEvent
		extends AbstractEvent
		implements CatchEvent, MouseListener {

	private final boolean cancelActivity;
	private final ElementRef<Activity> attachedToRef;

	public BoundaryEvent(final String id, final String name,
			final boolean cancelActivity,
			ElementRef<Activity> attachedToRef) {
		super(id, name, null);
		this.cancelActivity = cancelActivity;
		this.attachedToRef = attachedToRef;
		addMouseListener(this);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public boolean isInterrupting() {
		return cancelActivity;
	}

	protected ElementRef<Activity> getAttachedToRef() {
		return attachedToRef;
	}

	public Activity getAttachedTo() {
		final ElementRef<Activity> activityRef = getAttachedToRef();
		return ((activityRef == null) || !activityRef.hasElement())
				? null
				: activityRef.getElement();
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualization().getBackground(Visualization.Element.EVENT_BOUNDARY);
	}

	@Override
	public void happen(final Instance instance) {
		final Activity activity = getAttachedTo();
		if (activity != null) {
			final TokenCollection activityTokens = activity.getAllTokens();
			if (!activityTokens.isEmpty()) {
				final Token token = activityTokens.firstElement();
				tokenForwardToNextElement(token);
				if (isInterrupting()) {
					for (final Token activityToken : activityTokens) {
						activityToken.remove();						
					}
				}
			}
		}
	}

	@Override
	protected Stroke getStroke() {
		return new BasicStroke(getBorderWidth(),
				BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.f, new float[] { 4.f, 6.f }, 0); 
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		if (!isInterrupting()) {
			paintInnerCircle(g);
		}
	}

	@Override
	protected Icon getTypeIcon() {
		return getDefinition().getIcon(getVisualization(), false);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		happen(null);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

}
