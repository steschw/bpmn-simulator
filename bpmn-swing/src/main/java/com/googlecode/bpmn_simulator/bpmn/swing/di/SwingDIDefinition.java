/*
 * Copyright (C) 2014 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.bpmn.swing.di;

import com.googlecode.bpmn_simulator.bpmn.di.AbstractDIDefinition;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNEdge;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNLabel;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNPlane;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNShape;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.Event;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EndEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.StartEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.ProcessPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.TaskShape;

public class SwingDIDefinition
		extends AbstractDIDefinition<SwingBPMNDiagram> {

	@Override
	protected BPMNShape createShapeFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		if (element instanceof Task) {
			final TaskShape task = new TaskShape((Task) element);
			diagram.add(task);
			return task;
		} else if (element instanceof Event) {
			final EventShape<?> event;
			if (element instanceof StartEvent) {
				event = new StartEventShape((StartEvent) element);
			} else if (element instanceof EndEvent) {
				event = new EndEventShape((EndEvent) element);
			} else {
				event = new EventShape<Event>((Event) element);
			}
			diagram.add(event);
			return event;
		}
		return null;
	}

	@Override
	protected BPMNEdge createEdgeFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		return null;
	}

	@Override
	protected BPMNPlane createPlaneFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		if (element instanceof Process) {
			final ProcessPlane plane = new ProcessPlane((Process) element);
			diagram.setPlane(plane);
			return plane;
		}
		return null;
	}

	@Override
	protected BPMNLabel createLabelFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		final SwingBPMNLabel label = new SwingBPMNLabel(element.getId());
		return label;
	}

	@Override
	protected SwingBPMNDiagram createDiagram(final String name) {
		final SwingBPMNDiagram diagram = new SwingBPMNDiagram(name);
		diagram.setName(name);
		return diagram;
	}

}
