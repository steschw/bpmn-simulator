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
package com.google.code.bpmn_simulator.bpmn.trigger;

import java.awt.Color;
import java.awt.Point;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.code.bpmn_simulator.framework.element.visual.GraphicsLayer;
import com.google.code.bpmn_simulator.framework.element.visual.geometry.Bounds;
import com.google.code.bpmn_simulator.framework.instance.Instance;



public class TriggerCollection {

	private final Map<Instance, SortedSet<Trigger>> triggers
			= new IdentityHashMap<Instance, SortedSet<Trigger>>();

	public TriggerCollection() {
		super();
	}

	private Set<Trigger> getTriggers(final Instance destinationInstance) {
		SortedSet<Trigger> instanceTriggers = null;
		if (triggers.containsKey(destinationInstance)) {
			instanceTriggers = triggers.get(destinationInstance);
		} else {
			instanceTriggers = new TreeSet<Trigger>();
			triggers.put(destinationInstance, instanceTriggers);
		}
		return instanceTriggers;
	}

	public void add(final Trigger trigger) {
		assert trigger != null;
		getTriggers(trigger.getDestinationInstance()).add(trigger);
	}

	public Trigger first(final Instance destinationInstance) {
		final SortedSet<Trigger> instanceTriggers = triggers.get(destinationInstance);
		if ((instanceTriggers != null) && !instanceTriggers.isEmpty()) {
			return instanceTriggers.first();
		} else {
			return null;
		}
	}

	public void removeInstanceTriggers(final Instance instance) {
		triggers.remove(instance);
	}

	public void removeFirst(final Instance destinationInstance) {
		final SortedSet<Trigger> instanceTriggers = triggers.get(destinationInstance);
		if ((instanceTriggers != null) && !instanceTriggers.isEmpty()) {
			instanceTriggers.remove(instanceTriggers.first());
			if (instanceTriggers.isEmpty()) {
				removeInstanceTriggers(destinationInstance);
			}
		}
	}

	public void paint(final GraphicsLayer graphics, final Point point) {
		final Point instancePosition = point;
		for (final Instance instance : triggers.keySet()) {
			final Color color = instance.getColor();
			final Bounds rect = new Bounds(instancePosition, 8);
			graphics.setPaint(color);
			graphics.fillOval(rect);
			graphics.setPaint(Trigger.HIGHLIGHT_COLOR);
			graphics.drawOval(rect);
			final Set<Trigger> instanceTriggers = triggers.get(instance);
			assert instanceTriggers != null;
			if (instanceTriggers != null) {
				graphics.setPaint(GraphicsLayer.contrastColor(color));
				graphics.drawText(rect, Integer.toString(instanceTriggers.size()));
			}
			instancePosition.translate(-5, 0);
		}
	}

}
