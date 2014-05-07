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

import java.awt.Color;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.activation.MimeType;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.googlecode.bpmn_simulator.animation.element.logical.ref.NamedElements;
import com.googlecode.bpmn_simulator.animation.element.visual.Diagram;
import com.googlecode.bpmn_simulator.animation.input.AbstractXmlDefinition;
import com.googlecode.bpmn_simulator.animation.token.Instance;
import com.googlecode.bpmn_simulator.animation.token.TokenFlow;
import com.googlecode.bpmn_simulator.bpmn.Messages;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.AbstractFlowElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Error;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Expression;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.Message;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.SequenceFlow;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Association;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.Group;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.artifacts.TextAnnotation;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.ConditionalEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.EndEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.StartEvent;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.events.TerminateEventDefinition;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.AbstractGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ExclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.InclusiveGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.common.gateways.ParallelGateway;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.BaseElement;
import com.googlecode.bpmn_simulator.bpmn.model.core.foundation.Documentation;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.AbstractActivity;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Process;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.Subprocess;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.BusinessRuleTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ManualTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ScriptTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.ServiceTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.activities.tasks.UserTask;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataAssociation;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataObject;
import com.googlecode.bpmn_simulator.bpmn.model.process.data.DataStore;

public abstract class AbstractBPMNDefinition<E extends Diagram>
		extends AbstractXmlDefinition<E> {

	private static final String SCHEMA_FILENAME =
			"com/googlecode/bpmn_simulator/bpmn/xsd/BPMN20.xsd"; //$NON-NLS-1$

	protected static final String BPMN =
			"http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$

	protected static final String EXTENSION_SIGNAVIO =
			"http://www.signavio.com"; //$NON-NLS-1$

	private final NamedElements<TokenFlow> elements
			= new NamedElements<TokenFlow>();

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

	protected <E extends BaseElement> ElementRef<E> getNodeElementRef(
			final Node node, final String namespace, final String name) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, namespace, name)) {
				final String elementId = getNodeText(childNode);
				return getElementRef(elementId);
			}
		}
		return null;
	}

	protected <E extends BaseElement> E getAttributeElement(final Node node,
			final String name, final Class<E> type)
			throws StructureException {
		return getElement(getAttributeString(node, name), type);
	}

	@SuppressWarnings("unchecked")
	protected <T extends BaseElement> ElementRef<T> getElementRef(final String id) {
		return (ElementRef<T>)elements.getRef(id);
	}

	protected <T extends AbstractFlowElement> ElementRef<T> getAttributeElementRef(
			final Node node, final String name) {
		return getElementRef(getAttributeString(node, name));
	}

	protected String getIdAttribute(final Node node) {
		return getAttributeString(node, "id"); //$NON-NLS-1$
	}

	protected String getNameAttribute(final Node node) {
		return getAttributeString(node, "name"); //$NON-NLS-1$
	}

	protected <E extends AbstractTokenFlowElement> ElementRef<E> getSourceRefAttribute(final Node node) {
		return getAttributeElementRef(node, "sourceRef"); //$NON-NLS-1$
	}

	protected <E extends AbstractTokenFlowElement> ElementRef<E> getTargetRefAttribute(final Node node) {
		return getAttributeElementRef(node, "targetRef"); //$NON-NLS-1$
	}

	protected boolean readArtifacts(final Node node) {
		return readElementAssociation(node)
				|| readElementGroup(node)
				|| readElementTextAnnotation(node);
	}

	protected boolean readElementsForDefinitionsElement(final Node node) {
		return readElementMessage(node)
				|| readElementSignal(node)
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
				if (!readElementsForDefinitionsElement(childNode)) {
					showUnknowNode(childNode);
				}
			}
		} else {
			notifyWarning("there a no definitions")
		}
	}

	protected boolean readElementMessage(final Node node) {
		if (isElementNode(node, BPMN, "message")) { //$NON-NLS-1$
			final Message message = new Message(getIdAttribute(node),
					getNameAttribute(node));
			readBaseElement(node, message);
			elements.set(message);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementParticipant(final Node node,
			final Collaboration collaboration) {
		if (isElementNode(node, BPMN, "participant")) { //$NON-NLS-1$
			final ElementRef<Subprocess> processRef
				= getAttributeElementRef(node, "processRef"); //$NON-NLS-1$
			final Participant participant = new Participant(getIdAttribute(node),
					getNameAttribute(node), processRef);
			readBaseElement(node, participant);
			elements.set(participant);
			collaboration.addParticipant(participant);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementMessageFlow(final Node node,
			final Collaboration collaboration) {
		if (isElementNode(node, BPMN, "messageFlow")) { //$NON-NLS-1$
			final MessageFlow messageFlow = new MessageFlow(
					getIdAttribute(node), getNameAttribute(node),
					getSourceRefAttribute(node), getTargetRefAttribute(node));
			readBaseElement(node, messageFlow);
			elements.set(messageFlow);
			collaboration.addMessageFlow(messageFlow);
			return true;
		} else {
			return false;
		}
	}

	protected void readExtensionElementsPropertySignavio(final Node node,
			final BaseElement element) {
		final String keyNode = getAttributeString(node, "metaKey"); //$NON-NLS-1$
		final String valueNode = getAttributeString(node, "metaValue"); //$NON-NLS-1$
		if ("bgcolor".equals(keyNode) //$NON-NLS-1$
				&& ((valueNode != null) && !valueNode.isEmpty())) {
			final Color color = convertStringToColor(valueNode);
			if ((color != null) && (element instanceof AbstractFlowElement)) {
				((AbstractFlowElement)element).setElementBackground(color);
			}
		}
	}

	protected void readExtensionElementsProperties(final Node node,
			final BaseElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, EXTENSION_SIGNAVIO, "signavioMetaData")) { //$NON-NLS-1$
				readExtensionElementsPropertySignavio(childNode, element);
			} else {
				notifyWarning("unknow extension property");
			}
		}
	}

	protected boolean readElementFlowNodeRef(final Node node, final Lane lane) {
		if (isElementNode(node, BPMN, "flowNodeRef")) { //$NON-NLS-1$
			final String elementId = node.getTextContent();
			final ElementRef<AbstractFlowElement> elementRef = getElementRef(elementId);
			lane.addFlowNodeRef(elementRef);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementLane(final Node node,
			final AbstractContainerActivity activity, final LaneSet laneSet) {
		if (isElementNode(node, BPMN, "lane")) { //$NON-NLS-1$
			final Lane lane = new Lane(getIdAttribute(node), getNameAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, lane)
						&& !readElementLaneSet(childNode, activity, lane)
						&& !readElementFlowNodeRef(childNode, lane)) {
					showUnknowNode(childNode);
				}
			}
			addElementToContainer(lane, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementLaneSet(final Node node,
			final AbstractContainerActivity activity, final Lane lane) {
		final boolean isChild = isElementNode(node, BPMN, "childLaneSet"); //$NON-NLS-1$
		if (isChild || isElementNode(node, BPMN, "laneSet")) { //$NON-NLS-1$
			final LaneSet laneSet = new LaneSet(getIdAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, lane)
						&& !readElementLane(childNode, activity, laneSet)) {
					showUnknowNode(childNode);
				}
			}
			addElementToContainer(laneSet, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsIncomingOutgoing(final Node node,
			final AbstractTokenFlowElement element) {
		if (isElementNode(node, BPMN, "incoming")) { //$NON-NLS-1$
			final String elementId = node.getTextContent();
			final ElementRef<SequenceFlow> elementRef = getElementRef(elementId);
			element.addIncomingRef(elementRef);
			return true;
		} else if (isElementNode(node, BPMN, "outgoing")) { //$NON-NLS-1$
			final String elementId = node.getTextContent();
			final ElementRef<SequenceFlow> elementRef = getElementRef(elementId);
			element.addOutgoingRef(elementRef);
			return true;
		} else {
			return false;
		}
	}

	protected void readDefaultSequenceFlowAttribute(final Node node,
			final ElementWithDefaultSequenceFlow element) {
		final ElementRef<SequenceFlow> elementRef = getAttributeElementRef(node, "default"); //$NON-NLS-1$
		if (elementRef != null) {
			element.setDefaultSequenceFlowRef(elementRef);
		}
	}

	protected boolean readElementExtensionElements(final Node node, final BaseElement element) {
		if (isElementNode(node, BPMN, "extensionElements")) { //$NON-NLS-1$
			readExtensionElementsProperties(node, element);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForBaseElement(final Node node, final BaseElement element) {
		return readElementExtensionElements(node, element)
				|| readElementDocumentation(node, element);
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

	protected boolean readElementsForFlowElement(final Node node, final AbstractTokenFlowElement element) {
		return readElementsForBaseElement(node, element)
				|| readElementsIncomingOutgoing(node, element)
				|| readElementExtensionElements(node, element);
	}

	protected void readFlowElement(final Node node, final AbstractTokenFlowElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForFlowElement(childNode, element)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readEventDefinitions(final Node node, final AbstractEvent event) {
		if (isElementNode(node, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new TerminateEventDefinition(event));
		} else if (isElementNode(node, BPMN, "errorEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new ErrorEventDefinition(event,
					getAttributeErrorRef(node, "errorRef"))); //$NON-NLS-1$
		} else if (isElementNode(node, BPMN, "conditionalEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new ConditionalEventDefinition(event));
		} else if (isElementNode(node, BPMN, "timerEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new TimerEventDefinition(event));
		} else if (isElementNode(node, BPMN, "messageEventDefinition")) { //$NON-NLS-1$
			final MessageEventDefinition definition =
					new MessageEventDefinition(event, getElementRefAttribute(node));
			event.setDefinition(definition);
		} else if (isElementNode(node, BPMN, "linkEventDefinition")) { //$NON-NLS-1$
			final LinkEventDefinition definition =
					new LinkEventDefinition(event, getNameAttribute(node));
			event.setDefinition(definition);
		} else if (isElementNode(node, BPMN, "signalEventDefinition")) { //$NON-NLS-1$
			final ElementRef<Signal> signalRef = getAttributeSignalRef(node, "signalRef"); //$NON-NLS-1$
			event.setDefinition(new SignalEventDefinition(event, signalRef));
		} else {
			return false;
		}
		return true;
	}

	protected void readEvent(final Node node, final AbstractEvent event) {
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
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "startEvent")) { //$NON-NLS-1$
			final StartEvent event = new StartEvent(
					getIdAttribute(node), getNameAttribute(node),
					getAnimator().getInstanceManager());
			addElementToContainer(event, activity);
			readEvent(node, event);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementIntermediateThrowEvent(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "intermediateThrowEvent")) { //$NON-NLS-1$
			final IntermediateThrowEvent event = new IntermediateThrowEvent(
					getIdAttribute(node), getNameAttribute(node));
			readEvent(node, event);
			addElementToContainer(event, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementIntermediateCatchEvent(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "intermediateCatchEvent")) { //$NON-NLS-1$
			final IntermediateCatchEvent event = new IntermediateCatchEvent(
					getIdAttribute(node), getNameAttribute(node));
			readEvent(node, event);
			addElementToContainer(event, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean getCancelActivityAttribute(final Node node) {
		return getAttributeBoolean(node, "cancelActivity", true); //$NON-NLS-1$
	}

	protected ElementRef<AbstractActivity> getAttachedToRefAttribute(final Node node) {
		return getAttributeElementRef(node, "attachedToRef");		 //$NON-NLS-1$
	}

	protected boolean readElementBoundaryEvent(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "boundaryEvent")) { //$NON-NLS-1$
			final BoundaryEvent event = new BoundaryEvent(getIdAttribute(node),
					getNameAttribute(node),
					getCancelActivityAttribute(node),
					getAttachedToRefAttribute(node));
			readEvent(node, event);
			addElementToContainer(event, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementEndEvent(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "endEvent")) { //$NON-NLS-1$
			final EndEvent element = new EndEvent(getIdAttribute(node),
					getNameAttribute(node), getAnimator().getInstanceManager());
			readEvent(node, element);
			addElementToContainer(element, activity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsDataAssociations(final Node node) {
		if (isElementNode(node, BPMN, "dataInputAssociation") //$NON-NLS-1$
				|| isElementNode(node, BPMN, "dataOutputAssociation")) { //$NON-NLS-1$
			final ElementRef<AbstractTokenFlowElement> sourceRef
					= getNodeElementRef(node, BPMN, "sourceRef"); //$NON-NLS-1$
			final ElementRef<AbstractTokenFlowElement> targetRef
					= getNodeElementRef(node, BPMN, "targetRef"); //$NON-NLS-1$
			final DataAssociation dataAssociation
					= new DataAssociation(getIdAttribute(node),
							sourceRef, targetRef);
			elements.set(dataAssociation);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForActivity(final Node node, final AbstractActivity activity) {
		return readElementsForFlowElement(node, activity)
				|| readElementsDataAssociations(node);
	}

	protected void readGateway(final Node node, final AbstractGateway gateway) {
		readDefaultSequenceFlowAttribute(node, gateway);
		readFlowElement(node, gateway);
	}

	protected Direction getParameterAssociationDirection(final Node node) {
		final String value = getAttributeString(node, "associationDirection"); //$NON-NLS-1$
		final Direction direction = Direction.byValue(value);
		return (direction == null) ? Direction.NONE : direction;
	}

	protected MimeType getTextFormatAttribute(final Node node) {
		return getAttributeMimeType(node, "textFormat"); //$NON-NLS-1$
	}

	protected boolean readElementDocumentation(final Node node,
			final BaseElement element) {
		if (isElementNode(node, BPMN, "documentation")) { //$NON-NLS-1$
			final String text = node.getTextContent();
			if ((text != null) && !text.isEmpty()) {
				element.setDocumentation(new Documentation(text,
						getTextFormatAttribute(node)));
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
			if (text != null && !text.isEmpty()) {
				sequenceFlow.setCondition(new Expression(text));
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementSequenceflow(final Node node,
			final AbstractContainerActivity activity) {
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
			addElementToContainer(sequenceFlow, activity);
			// Es ist möglich des der Modeller keine Incoming/Outgoing-Elemente
			// für FlowElemente exportiert (z.B. BonitaStudio).
			// Deshalb werden diese jetzt noch einmal anhand des ConnectingElement
			// hinzugefügt.
			try {
				assignFlowElementsToSequenceFlow(sequenceFlow);
			} catch (StructureException e) {
				notifyStructureExceptionListeners(e);
			}
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

	protected ElementRef<Message> getElementRefAttribute(final Node node) {
		return getAttributeElementRef(node, "elementRef"); //$NON-NLS-1$
	}

	protected boolean getInstantiateAttribute(final Node node) {
		return getAttributeBoolean(node, "instantiate", false); //$NON-NLS-1$
	}

	protected boolean readElementTask(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "manualTask")) { //$NON-NLS-1$
			final ManualTask task = new ManualTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "userTask")) { //$NON-NLS-1$
			final UserTask task = new UserTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "businessRuleTask")) { //$NON-NLS-1$
			final BusinessRuleTask task = new BusinessRuleTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "scriptTask")) { //$NON-NLS-1$
			final ScriptTask task = new ScriptTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "serviceTask")) { //$NON-NLS-1$
			final ServiceTask task = new ServiceTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "sendTask")) { //$NON-NLS-1$
			final SendTask task = new SendTask(getIdAttribute(node),
					getNameAttribute(node), getElementRefAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "receiveTask")) { //$NON-NLS-1$
			final ReceiveTask task = new ReceiveTask(getIdAttribute(node),
					getNameAttribute(node), getInstantiateAttribute(node),
					getElementRefAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else if (isElementNode(node, BPMN, "task")) { //$NON-NLS-1$
			final Task task = new Task(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, task);
			addElementToContainer(task, activity);
		} else {
			return false;
		}
		return true;
	}

	public static String getNodeText(final Node node) {
		final String text = node.getTextContent();
		assert text != null && !text.isEmpty();
		return text;
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
			elements.set(textAnnotation);
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
			elements.set(association);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGroup(final Node node) {
		if (isElementNode(node, BPMN, "group")) { //$NON-NLS-1$
			final Group group = new Group(getIdAttribute(node));
			readBaseElement(node, group);
			elements.set(group);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGateway(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "parallelGateway")) { //$NON-NLS-1$
			final ParallelGateway element = new ParallelGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, activity);
		} else if (isElementNode(node, BPMN, "inclusiveGateway")) { //$NON-NLS-1$
			final InclusiveGateway element = new InclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, activity);
		} else if (isElementNode(node, BPMN, "exclusiveGateway")) { //$NON-NLS-1$
			final ExclusiveGateway element = new ExclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, activity);
		} else if (isElementNode(node, BPMN, "eventBasedGateway")) { //$NON-NLS-1$
			final EventBasedGateway element = new EventBasedGateway(
					getIdAttribute(node), getNameAttribute(node),
					getInstantiateAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, activity);
		} else {
			return false;
		}
		return true;
	}

	protected boolean readElementDataObject(final Node node,
			final AbstractContainerActivity activity) {
		final boolean isReference = isElementNode(node, BPMN, "dataObjectReference"); //$NON-NLS-1$
		if (isReference || isElementNode(node, BPMN, "dataObject")) { //$NON-NLS-1$
			final DataObject dataObject = new DataObject(
					getIdAttribute(node), getNameAttribute(node));
			if (!isReference) {
				dataObject.setCollection(getAttributeBoolean(node, "isCollection", false)); //$NON-NLS-1$
			}
			readBaseElement(node, dataObject);
			addElementToContainer(dataObject, activity);
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
			elements.set(dataStore);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataStoreReference(final Node node,
			final AbstractContainerActivity activity) {
		if (isElementNode(node, BPMN, "dataStoreReference")) { //$NON-NLS-1$
			final DataStore dataStore = new DataStore(
					getIdAttribute(node), getNameAttribute(node));
			readBaseElement(node, dataStore);
			addElementToContainer(dataStore, activity);
			return true;
		} else {
			return false;
		}
	}

	protected void readFlowElementsContainer(final Node node,
			final AbstractContainerActivity container) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementSubprocess(childNode, container)
					&& !readElementTransaction(childNode, container)
					&& !readElementsForFlowElement(childNode, container)
					&& !readElementStartEvent(childNode, container)
					&& !readElementIntermediateThrowEvent(childNode, container)
					&& !readElementIntermediateCatchEvent(childNode, container)
					&& !readElementBoundaryEvent(childNode, container)
					&& !readElementEndEvent(childNode, container)
					&& !readElementTask(childNode, container)
					&& !readElementGateway(childNode, container)
					&& !readElementSequenceflow(childNode, container)
					&& !readArtifacts(childNode)
					&& !readElementDataObject(childNode, container)
					&& !readElementDataStoreReference(childNode, container)
					&& !readElementsDataAssociations(childNode)
					&& !readElementLaneSet(childNode, container, null)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean getTriggeredByEventAttribute(final Node node) {
		return getAttributeBoolean(node, "triggeredByEvent", false); //$NON-NLS-1$
	}

	protected boolean readElementTransaction(final Node node,
			final AbstractContainerActivity parentActivity) {
		if (isElementNode(node, BPMN, "transaction")) { //$NON-NLS-1$
			final Transaction transaction = new Transaction(this,
					getIdAttribute(node), getNameAttribute(node),
					getTriggeredByEventAttribute(node));
			readDefaultSequenceFlowAttribute(node, transaction);
			readFlowElementsContainer(node, transaction);
			addElementToContainer(transaction, parentActivity);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementProcess(final Node node) {
		if (isElementNode(node, BPMN, "process")) { //$NON-NLS-1$
			final Process process = new Process(this,
					getIdAttribute(node), getNameAttribute(node));
			readDefaultSequenceFlowAttribute(node, process);
			readFlowElementsContainer(node, process);
			processes.add(process);
			addElementToContainer(process, null);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementSubprocess(final Node node,
			final AbstractContainerActivity parentActivity) {
		if (isElementNode(node, BPMN, "subProcess")) { //$NON-NLS-1$
			final Subprocess process = new Subprocess(this,
					getIdAttribute(node), getNameAttribute(node),
					getTriggeredByEventAttribute(node));
			readDefaultSequenceFlowAttribute(node, process);
			readFlowElementsContainer(node, process);
			addElementToContainer(process, parentActivity);
			return true;
		} else {
			return false;
		}
	}

	protected void assignFlowElementsToSequenceFlow(final SequenceFlow sequenceFlow)
			throws StructureException {
		final ElementRef<SequenceFlow> sequenceFlowRef = getElementRef(sequenceFlow.getId());
		if (sequenceFlowRef != null) {
			AbstractTokenFlowElement source = null;
			try {
				source = sequenceFlow.getSource();
			} catch (ClassCastException exception) {
				throwInvalidElementType(null, AbstractFlowElement.class);
			}
			if (source != null) {
				source.addOutgoingRef(sequenceFlowRef);
			}
			AbstractTokenFlowElement target = null;
			try {
				target = sequenceFlow.getTarget();
			} catch (ClassCastException exception) {
				throwInvalidElementType(null, AbstractFlowElement.class);
			}
			if (target != null) {
				target.addIncomingRef(sequenceFlowRef);
			}
		}
	}

	@Override
	protected void loadData(final Node node) {
		readDefinitions(node);
	}

	public Collection<TriggerCatchingElement> getManuallStartEvents() {
		final Collection<TriggerCatchingElement> events = new ArrayList<TriggerCatchingElement>();
		for (final TriggerCatchingElement event : getElements(TriggerCatchingElement.class)) {
			if (event.canTriggerManual()) {
				events.add(event);
			}
		}
		return events;
	}

}
