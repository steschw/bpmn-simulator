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
package com.googlecode.bpmn_simulator.bpmn.model;

import javax.activation.MimeType;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.NamedElements;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.NamedReference;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.Reference;
import com.googlecode.bpmn_simulator.animation.element.visual.Diagram;
import com.googlecode.bpmn_simulator.animation.input.AbstractXmlDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Participant;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.DefaultSequenceFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Expression;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowElementsContainer;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.FlowNode;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Message;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.AssociationDirection;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ConditionalEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.Event;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TerminateEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.Gateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.InclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.Documentation;
import com.googlecode.bpmn_simulator.bpmn.model.process.Lane;
import com.googlecode.bpmn_simulator.bpmn.model.process.LaneSet;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Activity;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Subprocess;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.BusinessRuleTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ManualTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ReceiveTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ScriptTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.SendTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ServiceTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.Task;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.UserTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataAssociation;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObject;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObjectReference;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStore;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStoreReference;

/**
 * Diagram Interchange (DI) Definition
 */
public abstract class AbstractBPMNDefinition<E extends Diagram<?>>
		extends AbstractXmlDefinition<E> {

	private static final String SCHEMA_FILENAME =
			"com/googlecode/bpmn_simulator/bpmn/xsd/BPMN20.xsd"; //$NON-NLS-1$

	protected static final String BPMN =
			"http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$

	private final NamedElements<BaseElement> elements
			= new NamedElements<BaseElement>();

	private String exporter;
	private String exporterVersion;

	public AbstractBPMNDefinition() {
		super(SCHEMA_FILENAME);
	}

	protected void setExporter(final String exporter) {
		this.exporter = exporter;
	}

	public String getExporter() {
		return exporter;
	}

	protected void setExporterVersion(final String exporterVersion) {
		this.exporterVersion = exporterVersion;
	}

	public String getExporterVersion() {
		return exporterVersion;
	}

	protected void registerElement(final BaseElement element) {
		elements.setElement(element.getId(), element);
	}

	protected BaseElement getElement(final String id) {
		return elements.getElement(id);
	}

	protected String getIdAttribute(final Node node) {
		return getAttributeString(node, "id"); //$NON-NLS-1$
	}

	protected String getNameAttribute(final Node node) {
		return getAttributeString(node, "name"); //$NON-NLS-1$
	}

	protected boolean readArtifacts(final Node node) {
		return readElementAssociation(node)
				|| readElementGroup(node)
				|| readElementTextAnnotation(node);
	}

	protected boolean readRootElements(final Node node) {
		return readElementMessage(node)
				|| readElementDataStore(node)
				|| readElementProcess(node)
				|| readElementCollaboration(node);
	}

	protected String getExporterAttribute(final Node node) {
		return getAttributeString(node, "exporter"); //$NON-NLS-1$
	}

	protected String getExporterVersionAttribute(final Node node) {
		return getAttributeString(node, "exporterVersion"); //$NON-NLS-1$
	}

	protected void readDefinitions(final Node node) {
		if (isElementNode(node, BPMN, "definitions")) { //$NON-NLS-1$
			setExporter(getExporterAttribute(node));
			setExporterVersion(getExporterVersionAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readRootElements(childNode)) {
					showUnknowNode(childNode);
				}
			}
		} else {
			notifyWarning("there a no definitions");
		}
	}

	protected boolean readElementMessage(final Node node) {
		if (isElementNode(node, BPMN, "message")) { //$NON-NLS-1$
			final String name = getNameAttribute(node);
			final Message message = new Message(getIdAttribute(node),
					name);
			readBaseElement(node, message);
			elements.setElement(name, message);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForBaseElement(final Node node, final BaseElement element) {
		return readElementDocumentation(node, element)
				|| readElementExtensionElements(node, element);
	}

	protected void readBaseElement(final Node node, final BaseElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, element)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementsForFlowElement(final Node node, final FlowElement element) {
		return readElementsForBaseElement(node, element);
	}

	protected void readFlowElement(final Node node, final FlowElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForFlowElement(childNode, element)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readEventDefinitions(final Node node, final Event event) {
		if (isElementNode(node, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new TerminateEventDefinition());
		} else if (isElementNode(node, BPMN, "conditionalEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new ConditionalEventDefinition());
		} else {
			showUnknowNode(node);
			return false;
		}
		return true;
	}

	protected void readEvent(final Node node, final Event event) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForFlowElement(childNode, event)
					&& !readEventDefinitions(childNode, event)
					&& !readElementsDataAssociations(childNode)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementStartEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "startEvent")) { //$NON-NLS-1$
			final StartEvent event = new StartEvent(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(event);
			readEvent(node, event);
			return true;
		} else {
			return false;
		}
	}

	protected boolean getCancelActivityAttribute(final Node node) {
		return getAttributeBoolean(node, "cancelActivity", true); //$NON-NLS-1$
	}

	protected boolean readElementEndEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "endEvent")) { //$NON-NLS-1$
			final EndEvent element = new EndEvent(getIdAttribute(node),
					getNameAttribute(node));
			readEvent(node, element);
			registerElement(element);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsDataAssociations(final Node node) {
		if (isElementNode(node, BPMN, "dataInputAssociation") //$NON-NLS-1$
				|| isElementNode(node, BPMN, "dataOutputAssociation")) { //$NON-NLS-1$
			final DataAssociation dataAssociation
					= new DataAssociation(getIdAttribute(node));
			registerElement(dataAssociation);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForActivity(final Node node, final Activity activity) {
		return readElementsForFlowElement(node, activity)
				|| readElementsDataAssociations(node);
	}

	protected Reference<SequenceFlow> getDefaultAttribute(final Node node) {
		return new NamedReference<SequenceFlow, BaseElement>(
				elements, getAttributeString(node, "default"), SequenceFlow.class);
	}

	protected void readDefaultSequenceFlowAttribute(
			final Node node, final DefaultSequenceFlowElement element) {
		element.setDefaultSequenceFlow(getDefaultAttribute(node));
	}

	protected void readGateway(final Node node, final Gateway gateway) {
		if (gateway instanceof DefaultSequenceFlowElement) {
			readDefaultSequenceFlowAttribute(node, (DefaultSequenceFlowElement) gateway);
		}
		readFlowElement(node, gateway);
	}

	protected AssociationDirection getParameterAssociationDirection(final Node node) {
		final String value = getAttributeString(node, "associationDirection"); //$NON-NLS-1$
		final AssociationDirection direction = AssociationDirection.byValue(value);
		if (direction == null) {
			return AssociationDirection.NONE;
		} else {
			return direction;
		}
	}

	protected boolean readElementExtensionElements(final Node node,
			final BaseElement element) {
		if (isElementNode(node, BPMN, "extensionElements")) { //$NON-NLS-1$
			//TODO
			return true;
		} else {
			return false;
		}
	}

	protected MimeType getTextFormatAttribute(final Node node) {
		return getAttributeMimeType(node, "textFormat"); //$NON-NLS-1$
	}

	protected boolean readElementDocumentation(final Node node,
			final BaseElement element) {
		if (isElementNode(node, BPMN, "documentation")) { //$NON-NLS-1$
			final String text = node.getTextContent();
			if ((text != null) && !text.isEmpty()) {
				element.addDocumentation(new Documentation(
						getIdAttribute(node),
						text, getTextFormatAttribute(node)));
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementConditionExpression(final Node node,
			final SequenceFlow sequenceFlow) {
		if (isElementNode(node, BPMN, "conditionExpression")) { //$NON-NLS-1$
			final String text = node.getTextContent();
			if (text != null && !text.isEmpty()) { ///XXX:
				sequenceFlow.setConditionExpression(new Expression(getIdAttribute(node)));
			}
			return true;
		} else {
			return false;
		}
	}

	protected <T extends BaseElement> Reference<T> getAttributeIDREF(
			final Node node, final String name, final Class<T> clazz) {
		return new NamedReference<T, BaseElement>(elements, getAttributeString(node, name), clazz);
	}

	protected Reference<FlowNode> getSourceRefAttribute(final Node node) {
		return getAttributeIDREF(node, "sourceRef", FlowNode.class);
	}

	protected Reference<FlowNode> getTargetRefAttribute(final Node node) {
		return getAttributeIDREF(node, "targetRef", FlowNode.class);
	}

	protected boolean readElementSequenceflow(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "sequenceFlow")) { //$NON-NLS-1$
			final SequenceFlow sequenceFlow = new SequenceFlow(
					getIdAttribute(node), getNameAttribute(node),
					getSourceRefAttribute(node), getTargetRefAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, sequenceFlow)
						&& !readElementConditionExpression(childNode, sequenceFlow)) {
					showUnknowNode(childNode);
				}
			}
			registerElement(sequenceFlow);
			return true;
		} else {
			return false;
		}
	}

	protected void readTask(final Node node, final Task task) {
		readDefaultSequenceFlowAttribute(node, task);
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForActivity(childNode, task)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean getInstantiateAttribute(final Node node) {
		return getAttributeBoolean(node, "instantiate", false); //$NON-NLS-1$
	}

	protected boolean readElementTask(final Node node,
			final FlowElementsContainer activity) {
		final Task task;
		if (isElementNode(node, BPMN, "manualTask")) { //$NON-NLS-1$
			task = new ManualTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "userTask")) { //$NON-NLS-1$
			task = new UserTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "businessRuleTask")) { //$NON-NLS-1$
			task = new BusinessRuleTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "scriptTask")) { //$NON-NLS-1$
			task = new ScriptTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "serviceTask")) { //$NON-NLS-1$
			task = new ServiceTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "sendTask")) { //$NON-NLS-1$
			task = new SendTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "receiveTask")) { //$NON-NLS-1$
			task = new ReceiveTask(getIdAttribute(node),
					getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "task")) { //$NON-NLS-1$
			task = new Task(getIdAttribute(node),
					getNameAttribute(node));
		} else {
			return false;
		}
		readTask(node, task);
		registerElement(task);
		return true;
	}

	protected static boolean readElementText(final Node node,
			final TextAnnotation textAnnotation) {
		if (isElementNode(node, BPMN, "text")) { //$NON-NLS-1$
			textAnnotation.setText(getNodeText(node));
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementTextAnnotation(final Node node) {
		if (isElementNode(node, BPMN, "textAnnotation")) { //$NON-NLS-1$
			final TextAnnotation textAnnotation = new TextAnnotation(getIdAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, textAnnotation)
						&& !readElementText(childNode, textAnnotation)) {
					showUnknowNode(childNode);
				}
			}
			registerElement(textAnnotation);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementAssociation(final Node node) {
		if (isElementNode(node, BPMN, "association")) { //$NON-NLS-1$
			final Association association = new Association(getIdAttribute(node));
			association.setDirection(getParameterAssociationDirection(node));
			readBaseElement(node, association);
			registerElement(association);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGroup(final Node node) {
		if (isElementNode(node, BPMN, "group")) { //$NON-NLS-1$
			final Group group = new Group(getIdAttribute(node));
			readBaseElement(node, group);
			registerElement(group);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGateway(final Node node,
			final FlowElementsContainer activity) {
		final Gateway gateway;
		if (isElementNode(node, BPMN, "parallelGateway")) { //$NON-NLS-1$
			gateway = new ParallelGateway(
					getIdAttribute(node), getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "inclusiveGateway")) { //$NON-NLS-1$
			gateway = new InclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
		} else if (isElementNode(node, BPMN, "exclusiveGateway")) { //$NON-NLS-1$
			gateway = new ExclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
		} else {
			return false;
		}
		readGateway(node, gateway);
		registerElement(gateway);
		return true;
	}

	protected boolean readElementDataObject(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "dataObject")) { //$NON-NLS-1$
			final DataObject dataObject = new DataObject(
					getIdAttribute(node), getNameAttribute(node));
			dataObject.setIsCollection(getAttributeBoolean(node, "isCollection", false)); //$NON-NLS-1$
			readBaseElement(node, dataObject);
			registerElement(dataObject);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataObjectReference(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "dataObjectReference")) { //$NON-NLS-1$
			final DataObjectReference dataObjectReference = new DataObjectReference(
					getIdAttribute(node), getNameAttribute(node),
					getAttributeIDREF(node, "dataObjectRef", DataObject.class)); //$NON-NLS-1$
			readBaseElement(node, dataObjectReference);
			registerElement(dataObjectReference);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataStore(final Node node) {
		if (isElementNode(node, BPMN, "dataStore")) { //$NON-NLS-1$
			final DataStore dataStore = new DataStore(
					getIdAttribute(node));
			readBaseElement(node, dataStore);
			registerElement(dataStore);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataStoreReference(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "dataStoreReference")) { //$NON-NLS-1$
			final DataStoreReference dataStoreReference = new DataStoreReference(
					getIdAttribute(node), getNameAttribute(node),
					getAttributeIDREF(node, "dataObjectRef", DataStore.class)); //$NON-NLS-1$
			readBaseElement(node, dataStoreReference);
			registerElement(dataStoreReference);
			return true;
		} else {
			return false;
		}
	}

	protected void readLaneElements(final Node node, final Lane lane) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, lane)
					&& !readElementChildLaneSet(childNode)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementLane(final Node node) {
		if (isElementNode(node, BPMN, "lane")) { //$NON-NLS-1$
			final Lane lane = new Lane(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(lane);
			readLaneElements(node, lane);
			return true;
		} else {
			return false;
		}
	}

	protected void readLaneSetElements(final Node node, final LaneSet laneSet) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, laneSet)
					&& !readElementLane(childNode)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementChildLaneSet(final Node node) {
		if (isElementNode(node, BPMN, "childLaneSet")) { //$NON-NLS-1$
			final LaneSet laneSet = new LaneSet(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(laneSet);
			readLaneSetElements(node, laneSet);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementLaneSet(final Node node,
			final FlowElementsContainer container) {
		if (isElementNode(node, BPMN, "laneSet")) { //$NON-NLS-1$
			final LaneSet laneSet = new LaneSet(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(laneSet);
			readLaneSetElements(node, laneSet);
			return true;
		} else {
			return false;
		}
	}

	protected void readFlowElementsContainer(final Node node,
			final FlowElementsContainer container) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, container)
					&& !readElementSubprocess(childNode, container)
					&& !readElementStartEvent(childNode, container)
					&& !readElementEndEvent(childNode, container)
					&& !readElementTask(childNode, container)
					&& !readElementGateway(childNode, container)
					&& !readElementSequenceflow(childNode, container)
					&& !readArtifacts(childNode)
					&& !readElementsDataAssociations(childNode)
					&& !readElementDataObject(childNode, container)
					&& !readElementDataObjectReference(childNode, container)
					&& !readElementDataStoreReference(childNode, container)
					&& !readElementLaneSet(childNode, container)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean getTriggeredByEventAttribute(final Node node) {
		return getAttributeBoolean(node, "triggeredByEvent", false); //$NON-NLS-1$
	}

	protected boolean getIsClosedAttribute(final Node node) {
		return getAttributeBoolean(node, "isClosed", false); //$NON-NLS-1$
	}

	protected boolean readElementParticipant(final Node node, final Collaboration collaboration) {
		if (isElementNode(node, BPMN, "participant")) { //$NON-NLS-1$
			final Participant participant = new Participant(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(participant);
			collaboration.addParticipant(participant);
			return true;
		} else {
			return false;
		}
	}

	protected void readCollaborationElements(final Node node, final Collaboration collaboration) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, collaboration)
					&& !readElementParticipant(childNode, collaboration)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementCollaboration(final Node node) {
		if (isElementNode(node, BPMN, "collaboration")) { //$NON-NLS-1$
			final Collaboration collaboration = new Collaboration(
					getIdAttribute(node), getNameAttribute(node),
					getIsClosedAttribute(node));
			readCollaborationElements(node, collaboration);
			registerElement(collaboration);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementProcess(final Node node) {
		if (isElementNode(node, BPMN, "process")) { //$NON-NLS-1$
			final Process process = new Process(
					getIdAttribute(node), getNameAttribute(node));
			readFlowElementsContainer(node, process);
			registerElement(process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementSubprocess(final Node node,
			final FlowElementsContainer parentActivity) {
		if (isElementNode(node, BPMN, "subProcess")) { //$NON-NLS-1$
			final Subprocess subprocess = new Subprocess(
					getIdAttribute(node), getNameAttribute(node),
					getTriggeredByEventAttribute(node));
			readDefaultSequenceFlowAttribute(node, subprocess);
			readFlowElementsContainer(node, subprocess);
//			readElementsForFlowElement(node, subprocess);
			registerElement(subprocess);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void loadData(final Node node) {
		readDefinitions(node);
	}

}
