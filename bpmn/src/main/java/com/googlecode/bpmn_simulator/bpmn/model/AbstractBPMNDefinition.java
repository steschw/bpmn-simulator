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
package com.googlecode.bpmn_simulator.bpmn.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeType;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.NamedElements;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.NamedReference;
import com.googlecode.bpmn_simulator.animation.element.logical.ref.Reference;
import com.googlecode.bpmn_simulator.animation.input.AbstractXmlDefinition;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.di.BPMNDiagram;
import com.googlecode.bpmn_simulator.bpmn.model.choreography.Choreography;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Collaboration;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.MessageFlow;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.Participant;
import com.googlecode.bpmn_simulator.bpmn.model.collaboration.conversations.Conversation;
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
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.BoundaryEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ConditionalEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ErrorEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.Event;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateCatchEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.IntermediateThrowEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.LinkEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.MessageEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TerminateEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TimerEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.EventBasedGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.Gateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.InclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.Documentation;
import com.googlecode.bpmn_simulator.bpmn.model.core.infrastructure.Definitions;
import com.googlecode.bpmn_simulator.bpmn.model.process.Lane;
import com.googlecode.bpmn_simulator.bpmn.model.process.LaneSet;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Activity;
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
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObject;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObjectReference;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataOutput;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStore;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStoreReference;

public abstract class AbstractBPMNDefinition<E extends BPMNDiagram<?>>
		extends AbstractXmlDefinition<E>
		implements Definitions<E> {

	public static final String ELEMENT_NAME = Messages.getString("definitions"); //$NON-NLS-1$

	public static final String FILE_DESCRIPTION = "BPMN 2.0 XML"; //$NON-NLS-1$
	public static final String[] FILE_EXTENSIONS = {"bpmn", "xml"}; //$NON-NLS-1$ //$NON-NLS-2$

	private static final URI DEFAULT_EXPRESSION_LANGUAGE = uri("http://www.w3.org/1999/XPath");
	private static final URI DEFAULT_TYPE_LANGUAGE = uri("http://www.w3.org/2001/XMLSchema");

	private static final String SCHEMA_FILENAME =
			"com/googlecode/bpmn_simulator/bpmn/xsd/BPMN20.xsd"; //$NON-NLS-1$

	protected static final String BPMN =
			"http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$

	private static URI uri(final String uri) {
		try {
			return new URI(uri);
		} catch (NullPointerException e) {
		} catch (URISyntaxException e) {
		}
		return null;
	}

	private final NamedElements<BaseElement> elements
			= new NamedElements<BaseElement>();

	private String id;
	private String name;

	private URI expressionLanguage;
	private URI typeLanguage;

	private String exporter;
	private String exporterVersion;

	private List<Documentation> documentations = new ArrayList<Documentation>();

	private Collection<LogicalElement> startElements = new ArrayList<LogicalElement>();

	@Override
	public Collection<LogicalElement> getStartElements() {
		return startElements;
	}

	public AbstractBPMNDefinition() {
		super(SCHEMA_FILENAME);
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getExporter() {
		return exporter;
	}

	@Override
	public String getExporterVersion() {
		return exporterVersion;
	}

	@Override
	public URI getExpressionLanguage() {
		return expressionLanguage;
	}

	@Override
	public URI getTypeLanguage() {
		return typeLanguage;
	}

	@Override
	public void addDocumentation(final Documentation documentation) {
		documentations.add(documentation);
	}

	@Override
	public Collection<Documentation> getDocumentation() {
		return documentations;
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

	protected boolean readArtifactElements(final Node node) {
		return readElementAssociation(node)
				|| readElementGroup(node)
				|| readElementTextAnnotation(node);
	}

	protected boolean readRootElements(final Node node) {
		return readElementMessage(node)
				|| readElementDataStore(node)
				|| readElementProcess(node)
				|| readElementCollaboration(node)
				|| readElementChoreography(node);
	}

	protected String getExporterAttribute(final Node node) {
		return getAttributeString(node, "exporter"); //$NON-NLS-1$
	}

	protected String getExporterVersionAttribute(final Node node) {
		return getAttributeString(node, "exporterVersion"); //$NON-NLS-1$
	}

	protected URI getExpressionLanguageAttribute(final Node node) {
		return getAttributeURI(node, "expressionLanguage", DEFAULT_EXPRESSION_LANGUAGE);
	}

	protected URI getTypeLanguageAttribute(final Node node) {
		return getAttributeURI(node, "typeLanguage", DEFAULT_TYPE_LANGUAGE);
	}

	protected void readDefinitions(final Node node) {
		if (isElementNode(node, BPMN, "definitions")) { //$NON-NLS-1$
			exporter = getExporterAttribute(node);
			exporterVersion = getExporterVersionAttribute(node);
			expressionLanguage = getExpressionLanguageAttribute(node);
			typeLanguage = getTypeLanguageAttribute(node);
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readRootElements(childNode)) {
					showUnknowNode(childNode);
				}
			}
		} else {
			notifyWarning("schema doesn''t contains definitions");
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
		if (isElementNode(node, BPMN, "conditionalEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new ConditionalEventDefinition(getIdAttribute(node)));
		} else if (isElementNode(node, BPMN, "errorEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new ErrorEventDefinition(getIdAttribute(node)));
		} else if (isElementNode(node, BPMN, "linkEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new LinkEventDefinition(getIdAttribute(node), getNameAttribute(node)));
		} else if (isElementNode(node, BPMN, "messageEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new MessageEventDefinition(getIdAttribute(node)));
		} else if (isElementNode(node, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
				event.setEventDefinition(new TerminateEventDefinition(getIdAttribute(node)));
		} else if (isElementNode(node, BPMN, "timerEventDefinition")) { //$NON-NLS-1$
			event.setEventDefinition(new TimerEventDefinition(getIdAttribute(node)));
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
					&& !readElementDataAssociations(childNode)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean getIsInterruptingAttribute(final Node node) {
		return getAttributeBoolean(node, "isInterrupting", true); //$NON-NLS-1$
	}

	protected boolean readElementStartEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "startEvent")) { //$NON-NLS-1$
			final StartEvent event = new StartEvent(
					getIdAttribute(node), getNameAttribute(node),
					getIsInterruptingAttribute(node));
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

	protected boolean readElementIntermediateThrowEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "intermediateThrowEvent")) { //$NON-NLS-1$
			final IntermediateThrowEvent event = new IntermediateThrowEvent(getIdAttribute(node),
					getNameAttribute(node));
			readEvent(node, event);
			registerElement(event);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementIntermediateCatchEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "intermediateCatchEvent")) { //$NON-NLS-1$
			final IntermediateCatchEvent event = new IntermediateCatchEvent(getIdAttribute(node),
					getNameAttribute(node));
			readEvent(node, event);
			registerElement(event);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBoundaryEvent(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "boundaryEvent")) { //$NON-NLS-1$
			final BoundaryEvent event = new BoundaryEvent(getIdAttribute(node),
					getNameAttribute(node));
			readEvent(node, event);
			registerElement(event);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataAssociations(final Node node) {
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
				|| readElementDataAssociations(node);
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

	protected boolean readElementMessageFlow(final Node node) {
		if (isElementNode(node, BPMN, "messageFlow")) { //$NON-NLS-1$
			final MessageFlow messageFlow = new MessageFlow(
					getIdAttribute(node), getNameAttribute(node));
			readElementsForBaseElement(node, messageFlow);
			registerElement(messageFlow);
			return true;
		} else {
			return false;
		}
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

	protected void readActivity(final Node node, final Activity activity) {
		readDefaultSequenceFlowAttribute(node, activity);
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForActivity(childNode, activity)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean getInstantiateAttribute(final Node node) {
		return getAttributeBoolean(node, "instantiate", false); //$NON-NLS-1$
	}

	protected boolean readElementCallActivity(final Node node,
			final FlowElementsContainer activity) {
		if (isElementNode(node, BPMN, "callActivity")) { //$NON-NLS-1$
			final CallActivity callActivity = new CallActivity(getIdAttribute(node),
					getNameAttribute(node));
			readActivity(node, callActivity);
			registerElement(callActivity);
			return true;
		}
		return false;
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
					getNameAttribute(node), getInstantiateAttribute(node));
		} else if (isElementNode(node, BPMN, "task")) { //$NON-NLS-1$
			task = new Task(getIdAttribute(node),
					getNameAttribute(node));
		} else {
			return false;
		}
		readActivity(node, task);
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
		} else if (isElementNode(node, BPMN, "eventBasedGateway")) { //$NON-NLS-1$
			gateway = new EventBasedGateway(
					getIdAttribute(node), getNameAttribute(node), getInstantiateAttribute(node));
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
					getIdAttribute(node), getNameAttribute(node));
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

	protected boolean readElementDataInput(final Node node) {
		if (isElementNode(node, BPMN, "dataInput")) { //$NON-NLS-1$
			final DataInput dataInput = new DataInput(
					getIdAttribute(node), getNameAttribute(node));
			readBaseElement(node, dataInput);
			registerElement(dataInput);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataOutput(final Node node) {
		if (isElementNode(node, BPMN, "dataOutput")) { //$NON-NLS-1$
			final DataOutput dataOutput = new DataOutput(
					getIdAttribute(node), getNameAttribute(node));
			readBaseElement(node, dataOutput);
			registerElement(dataOutput);
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

	protected boolean readActivitiyElements(final Node node,
			final FlowElementsContainer container) {
		return readElementSubprocess(node, container)
				|| readElementTransaction(node, container)
				|| readElementTask(node, container)
				|| readElementCallActivity(node, container);
	}

	protected boolean readFlowElements(final Node node,
			final FlowElementsContainer container) {
		return readActivitiyElements(node, container)
				|| readElementStartEvent(node, container)
				|| readElementEndEvent(node, container)
				|| readElementIntermediateThrowEvent(node, container)
				|| readElementIntermediateCatchEvent(node, container)
				|| readElementBoundaryEvent(node, container)
				|| readElementGateway(node, container)
				|| readElementSequenceflow(node, container)
				|| readArtifactElements(node)
				|| readElementDataAssociations(node)
				|| readElementDataObject(node, container)
				|| readElementDataObjectReference(node, container)
				|| readElementDataStoreReference(node, container)
				|| readElementLaneSet(node, container);
	}

	protected void readFlowElementsContainer(final Node node,
			final FlowElementsContainer container) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, container)
					&& !readFlowElements(childNode, container)) {
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

	protected boolean readElementConversation(final Node node, final Collaboration collaboration) {
		if (isElementNode(node, BPMN, "conversation")) { //$NON-NLS-1$
			final Conversation conversation = new Conversation(
					getIdAttribute(node), getNameAttribute(node));
			registerElement(conversation);
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
					&& !readElementParticipant(childNode, collaboration)
					&& !readElementConversation(childNode, collaboration)
					&& !readElementMessageFlow(childNode)
					&& !readArtifactElements(childNode)) {
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

	protected void readChoreographyElements(final Node node, final Choreography choreography) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, choreography)
					&& !readFlowElements(childNode, choreography)
					&& !readElementParticipant(childNode, choreography)
					&& !readElementMessageFlow(childNode)
					&& !readArtifactElements(childNode)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementChoreography(final Node node) {
		if (isElementNode(node, BPMN, "choreography")) { //$NON-NLS-1$
			final Choreography choreography = new Choreography(
					getIdAttribute(node), getNameAttribute(node),
					getIsClosedAttribute(node));
			readChoreographyElements(node, choreography);
			registerElement(choreography);
			return true;
		} else {
			return false;
		}
	}

	protected void showElementLoading(BaseElement element) {
		notifyInfo(MessageFormat.format("Loading {0} ''{1}''",
				element.getElementName(), element.getId()));
	}

	protected boolean readElementProcess(final Node node) {
		if (isElementNode(node, BPMN, "process")) { //$NON-NLS-1$
			final Process process = new Process(
					getIdAttribute(node), getNameAttribute(node));
			showElementLoading(process);
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
			final SubProcess subprocess = new SubProcess(
					getIdAttribute(node), getNameAttribute(node),
					getTriggeredByEventAttribute(node));
			readDefaultSequenceFlowAttribute(node, subprocess);
			readFlowElementsContainer(node, subprocess);
			registerElement(subprocess);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementTransaction(final Node node,
			final FlowElementsContainer parentActivity) {
		if (isElementNode(node, BPMN, "transaction")) { //$NON-NLS-1$
			final Transaction transaction = new Transaction(
					getIdAttribute(node), getNameAttribute(node),
					getTriggeredByEventAttribute(node));
			readDefaultSequenceFlowAttribute(node, transaction);
			readFlowElementsContainer(node, transaction);
			registerElement(transaction);
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
