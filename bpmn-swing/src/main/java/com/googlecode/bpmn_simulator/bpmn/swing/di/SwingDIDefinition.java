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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import com.googlecode.bpmn_simulator.bpmn.di.AbstractDIDefinition;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNEdge;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNLabel;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNPlane;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNShape;
import com.googlecode.bpmn_simulator.bpmn.model.NamedElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.SequenceFlowEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.AssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.GroupShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.TextAnnotationShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EndEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.StartEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ParallelGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.ProcessPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.TaskShape;

public class SwingDIDefinition
		extends AbstractDIDefinition<SwingBPMNDiagram> {

	private static final Map<Class<? extends BaseElement>, Class<? extends BPMNEdge>> EDGE_MAPPERS
			= new HashMap<Class<? extends BaseElement>, Class<? extends BPMNEdge>>();

	private static final Map<Class<? extends BaseElement>, Class<? extends BPMNShape>> SHAPE_MAPPERS
			= new HashMap<Class<? extends BaseElement>, Class<? extends BPMNShape>>();

	private static final Map<Class<? extends BaseElement>, Class<? extends BPMNPlane>> PLANE_MAPPERS
			= new HashMap<Class<? extends BaseElement>, Class<? extends BPMNPlane>>();

	static {
		EDGE_MAPPERS.put(Association.class, AssociationEdge.class);
		EDGE_MAPPERS.put(SequenceFlow.class, SequenceFlowEdge.class);

		// Artifacts
		SHAPE_MAPPERS.put(TextAnnotation.class, TextAnnotationShape.class);
		SHAPE_MAPPERS.put(Group.class, GroupShape.class);
		// Events
		SHAPE_MAPPERS.put(StartEvent.class, StartEventShape.class);
		SHAPE_MAPPERS.put(EndEvent.class, EndEventShape.class);
		// Gateways
		SHAPE_MAPPERS.put(ParallelGateway.class, ParallelGatewayShape.class);
		// Process
		SHAPE_MAPPERS.put(Task.class, TaskShape.class);

		PLANE_MAPPERS.put(Process.class, ProcessPlane.class);
	};

	private static <E> E createFor(
			Map<Class<? extends BaseElement>, Class<? extends E>> map,
			final SwingBPMNDiagram diagram,
			final BaseElement element) {
		final Class<? extends E> edgeClass = map.get(element.getClass());
		if (edgeClass != null) {
			try {
				final Constructor<? extends E> constructor = edgeClass.getConstructor(element.getClass());
				return constructor.newInstance(element);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected BPMNShape createShapeFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		final BPMNShape shape = createFor(SHAPE_MAPPERS, diagram, element);
		if (shape instanceof JComponent) {
			diagram.add((JComponent) shape);
			return shape;
		}
		return null;
	}

	@Override
	protected BPMNEdge createEdgeFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		final BPMNEdge edge = createFor(EDGE_MAPPERS, diagram, element);
		if (edge instanceof JComponent) {
			diagram.add((JComponent) edge);
			return edge;
		}
		return null;
	}

	@Override
	protected BPMNPlane createPlaneFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		final BPMNPlane plane = createFor(PLANE_MAPPERS, diagram, element);
		if (plane instanceof JComponent) {
			diagram.setPlane((JComponent) plane);
			return plane;
		}
		return null;
	}

	@Override
	protected BPMNLabel createLabelFor(final SwingBPMNDiagram diagram, final BaseElement element) {
		final String text;
		if (element instanceof NamedElement) {
			text = ((NamedElement) element).getName();
		} else {
			text = element.getId();
		}
		final SwingBPMNLabel label = new SwingBPMNLabel(text);
		diagram.add(label);
		return label;
	}

	@Override
	protected SwingBPMNDiagram createDiagram(final String name) {
		final SwingBPMNDiagram diagram = new SwingBPMNDiagram(name);
		diagram.setName(name);
		return diagram;
	}

}
