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
package com.google.code.bpmn_simulator.framework.element;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

public class ClickThroughMouseListener
		implements MouseListener {

	private static boolean isComponentBetween(final Container parent,
			final Component component,
			final Component higher, final Component lower) {
		final int componentZIndex = parent.getComponentZOrder(component);
		final int higherZIndex = parent.getComponentZOrder(higher);
		final int lowerZIndex = parent.getComponentZOrder(lower);
		assert (higherZIndex < lowerZIndex) || (lowerZIndex == -1);
		return (componentZIndex > higherZIndex)
				&& ((componentZIndex < lowerZIndex) || (lowerZIndex == -1));
	}

	private static void dispatchEventToUnderlyingComponent(final MouseEvent event) {
		final Component sourceComponent = event.getComponent();
		Component targetComponent = null;
		final Container parent = sourceComponent.getParent();
		final Point point = SwingUtilities.convertPoint(sourceComponent, event.getPoint(), parent);
		//final int sourceComponentZOrder = parent.getComponentZOrder(sourceComponent); 
		for (final Component component : parent.getComponents()) {
			if (!component.equals(sourceComponent)
					&& component.getBounds().contains(point)) {
				/*
				final int componentZOrder = parent.getComponentZOrder(component); 
				final int targetComponentZOrder = parent.getComponentZOrder(targetComponent); 
				if ((targetComponent == null)
						|| ((componentZOrder > sourceComponentZOrder)
						&& (componentZOrder < targetComponentZOrder))) {
					targetComponent = component;
				}
				*/
				if (isComponentBetween(parent, component, sourceComponent, targetComponent)) {
					targetComponent = component;
				}
			}
		}
		if (targetComponent != null) {
			targetComponent.dispatchEvent(
					SwingUtilities.convertMouseEvent(event.getComponent(), event, targetComponent));
		}
	}

	@Override
	public void mouseReleased(final MouseEvent event) {
		dispatchEventToUnderlyingComponent(event);
	}

	@Override
	public void mousePressed(final MouseEvent event) {
		dispatchEventToUnderlyingComponent(event);
	}

	@Override
	public void mouseExited(final MouseEvent event) {
	}

	@Override
	public void mouseEntered(final MouseEvent event) {
	}

	@Override
	public void mouseClicked(final MouseEvent event) {
		dispatchEventToUnderlyingComponent(event);
	}

}
