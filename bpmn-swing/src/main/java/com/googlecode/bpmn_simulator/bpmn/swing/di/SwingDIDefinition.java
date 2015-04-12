/*
 * Copyright (C) 2015 Stefan Schweitzer
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
import com.googlecode.bpmn_simulator.bpmn.model.choreography.Choreography;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.MessageFlow;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Participant;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.conversations.Conversation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.BoundaryEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateCatchEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateThrowEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.EventBasedGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.InclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.process.Lane;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.CallActivity;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.SubProcess;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Transaction;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.BusinessRuleTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ManualTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ReceiveTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ScriptTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.SendTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ServiceTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.UserTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataAssociation;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataInput;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObjectReference;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataOutput;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStoreReference;
import com.googlecode.bpmn_simulator.bpmn.swing.model.choreography.ChoreographyPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.CollaborationPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.MessageFlowEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.ParticipantShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.conversations.ConversationShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.SequenceFlowEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.AssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.GroupShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.TextAnnotationShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.BoundaryEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EndEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateCatchEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateThrowEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.StartEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.EventBasedGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ExclusiveGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.InclusiveGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ParallelGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.LaneShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.CallActivityShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.ProcessPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.SubProcessPlane;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.SubProcessShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.TransactionShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.BusinessRuleTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ManualTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ReceiveTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ScriptTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.SendTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ServiceTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.TaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.UserTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataAssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataInputShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataObjectReferenceShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataOutputShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataStoreReferenceShape;

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
		EDGE_MAPPERS.put(DataAssociation.class, DataAssociationEdge.class);
		EDGE_MAPPERS.put(MessageFlow.class, MessageFlowEdge.class);

		// Artifacts
		SHAPE_MAPPERS.put(TextAnnotation.class, TextAnnotationShape.class);
		SHAPE_MAPPERS.put(Group.class, GroupShape.class);
		// Events
		SHAPE_MAPPERS.put(StartEvent.class, StartEventShape.class);
		SHAPE_MAPPERS.put(EndEvent.class, EndEventShape.class);
		SHAPE_MAPPERS.put(IntermediateThrowEvent.class, IntermediateThrowEventShape.class);
		SHAPE_MAPPERS.put(IntermediateCatchEvent.class, IntermediateCatchEventShape.class);
		SHAPE_MAPPERS.put(BoundaryEvent.class, BoundaryEventShape.class);
		// Gateways
		SHAPE_MAPPERS.put(ParallelGateway.class, ParallelGatewayShape.class);
		SHAPE_MAPPERS.put(ExclusiveGateway.class, ExclusiveGatewayShape.class);
		SHAPE_MAPPERS.put(InclusiveGateway.class, InclusiveGatewayShape.class);
		SHAPE_MAPPERS.put(EventBasedGateway.class, EventBasedGatewayShape.class);
		// Activity
		SHAPE_MAPPERS.put(BusinessRuleTask.class, BusinessRuleTaskShape.class);
		SHAPE_MAPPERS.put(ManualTask.class, ManualTaskShape.class);
		SHAPE_MAPPERS.put(ReceiveTask.class, ReceiveTaskShape.class);
		SHAPE_MAPPERS.put(ScriptTask.class, ScriptTaskShape.class);
		SHAPE_MAPPERS.put(ServiceTask.class, ServiceTaskShape.class);
		SHAPE_MAPPERS.put(SendTask.class, SendTaskShape.class);
		SHAPE_MAPPERS.put(Task.class, TaskShape.class);
		SHAPE_MAPPERS.put(UserTask.class, UserTaskShape.class);
		SHAPE_MAPPERS.put(SubProcess.class, SubProcessShape.class);
		SHAPE_MAPPERS.put(Transaction.class, TransactionShape.class);
		SHAPE_MAPPERS.put(CallActivity.class, CallActivityShape.class);
		// Data
		SHAPE_MAPPERS.put(DataObjectReference.class, DataObjectReferenceShape.class);
		SHAPE_MAPPERS.put(DataStoreReference.class, DataStoreReferenceShape.class);
		SHAPE_MAPPERS.put(DataInput.class, DataInputShape.class);
		SHAPE_MAPPERS.put(DataOutput.class, DataOutputShape.class);
		// Collaboration
		SHAPE_MAPPERS.put(Participant.class, ParticipantShape.class);
		// Conversations
		SHAPE_MAPPERS.put(Conversation.class, ConversationShape.class);
		// Process
		SHAPE_MAPPERS.put(Lane.class, LaneShape.class);

		PLANE_MAPPERS.put(Process.class, ProcessPlane.class);
		PLANE_MAPPERS.put(SubProcess.class, SubProcessPlane.class);
		PLANE_MAPPERS.put(Collaboration.class, CollaborationPlane.class);
		PLANE_MAPPERS.put(Choreography.class, ChoreographyPlane.class);
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
		final SwingBPMNLabel label = new SwingBPMNLabel();
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
