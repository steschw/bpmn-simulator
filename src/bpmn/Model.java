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
package bpmn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.text.html.StyleSheet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpmn.element.*;
import bpmn.element.Association.Direction;
import bpmn.element.activity.Activity;
import bpmn.element.activity.ExpandedProcess;
import bpmn.element.activity.task.*;
import bpmn.element.artifact.Group;
import bpmn.element.artifact.TextAnnotation;
import bpmn.element.event.*;
import bpmn.element.event.definition.LinkEventDefinition;
import bpmn.element.event.definition.MessageEventDefinition;
import bpmn.element.event.definition.SignalEventDefinition;
import bpmn.element.event.definition.TerminateEventDefinition;
import bpmn.element.event.definition.TimerEventDefinition;
import bpmn.element.gateway.*;
import bpmn.token.InstanceManager;
import bpmn.token.TokenAnimator;

public class Model implements ErrorHandler {

	protected static final String BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$
	protected static final String BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI"; //$NON-NLS-1$
	protected static final String DC = "http://www.omg.org/spec/DD/20100524/DC"; //$NON-NLS-1$
	protected static final String DI = "http://www.omg.org/spec/DD/20100524/DI"; //$NON-NLS-1$

	protected static final String EXTENSION_SIGNAVIO = "http://www.signavio.com"; //$NON-NLS-1$

	private final ElementRefCollection<VisibleElement> elements
			= new ElementRefCollection<VisibleElement>();

	private final Collection<ExpandedProcess> processes
			= new LinkedList<ExpandedProcess>();

	private final ElementRefCollection<Signal> signals
			= new ElementRefCollection<Signal>();

	private final JDesktopPane desktop;

	private final LogFrame logFrame = new LogFrame(); 

	private final InstanceManager instanceManager = new InstanceManager();

	private final TokenAnimator tokenAnimator;

	public Model(final JDesktopPane desktop) {
		super();
		tokenAnimator = new TokenAnimator(getInstanceManager());
		this.desktop = desktop;
	}

	public InstanceManager getInstanceManager() {
		return instanceManager;
	}

	public TokenAnimator getAnimator() {
		return tokenAnimator;
	}

	public Collection<ExpandedProcess> getProcesses() {
		return processes;
	}

	@SuppressWarnings("unchecked")
	protected <E> Collection<E> getElements(final Class<E> type) {
		final Collection<E> events = new LinkedList<E>();
		for (final ElementRef<VisibleElement> elementRef : elements.values()) {
			final Element element = elementRef.getElement();
			if (type.isAssignableFrom(element.getClass())) {
				events.add((E)element);
			}
		}
		return events;
	}

	public Collection<CatchEvent> getCatchEvents() {
		return getElements(CatchEvent.class);
	}

	protected void addElementToContainer(final VisibleElement element,
			final Collaboration collaboration) {
		elements.set(element);
		if (collaboration != null) {
			collaboration.addElement(element);
		}
	}

	protected void addElementToContainer(final VisibleElement element,
			final ExpandedProcess process) {
		elements.set(element);
		if (process != null) {
			process.addElement(element);
		}
	}

	protected void addElementToContainer(final Lane element,
			final LaneSet laneSet) {
		elements.set(element);
		if (laneSet != null) {
			laneSet.addLane(element);
		}
	}

	protected void addElementToContainer(final LaneSet element,
			final Lane lane) {
		elements.set(element);
		if (lane != null) {
			lane.addLaneSet(element);
		}
	}

	protected void showUnknowNode(final Node node) {
		logFrame.addError(MessageFormat.format(
				Messages.getString("Protocol.unknownElement"), //$NON-NLS-1$
				node.getNodeName()));
	}

	protected void showElementNotFound(final String id) {
		logFrame.addError(MessageFormat.format(
				Messages.getString("Protocol.elementNotFound"), //$NON-NLS-1$
				id));
	}

	protected void showUnknowAttribute(final String name, final Node node) {
		logFrame.addWarning(
				MessageFormat.format(Messages.getString("Protocol.attributeNotExist"), //$NON-NLS-1$
				name, node.getNodeName()));
	}

	protected static boolean isElementNode(final Node node,
			final String namespace, final String name) {
		return (node.getNodeType() == Node.ELEMENT_NODE)
				&& name.equals(node.getLocalName())
				&& namespace.equals(node.getNamespaceURI());
	}

	/**
	 * Liefert ein Unterelement von einem Element und prüft dabei ob dieses nur einmal vorkommt
	 * @return Liefert null zurück wenn das Unterelement nicht gefunden wird. Bei mehreren wird das erste zurück gegeben.
	 */
	protected static Node getSingleSubElement(final Node node, final String namespace, final String name) {
		Node subElement = null;
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, namespace, name)) {
				if (subElement == null) {
					subElement = childNode;
				} else {
					// Sollte bereits durch XSD geprüft werden
					//logFrame.addWarning("Element " + childNode.getNodeName() + " mehrfach gefunden, aber nur einmal erwartet");
				}
				subElement = childNode;
			}
		}
		return subElement;
	}

	protected String getAttributeString(final Node node, final String name,
			final boolean required) {
		final Node attributeNode = node.getAttributes().getNamedItem(name);
		if (attributeNode == null) {
			if (required) {
				showUnknowAttribute(name, node);
			}
			return null;
		}
		return attributeNode.getNodeValue();
	}

	protected float getAttributeFloat(final Node node, final String name) {
		try {
			return Float.parseFloat(getAttributeString(node, name, true));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected static boolean convertStringToBool(final String string,
			final boolean defaultValue) {
		if ((string == null) || string.isEmpty()) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(string);
		}
	}

	protected boolean getAttributeBoolean(final Node node, final String name,
			final boolean required, final boolean defaultValue) {
		return convertStringToBool(getAttributeString(node, name, required), defaultValue);
	}

	protected VisibleElement getAttributeElement(final Node node, final String name) {
		final String elementId = getAttributeString(node, name, true);
		VisibleElement element = null;
		if (elementId != null) {
			element = elements.get(elementId);
			if (element == null) {
				showElementNotFound(elementId);
			}
		}
		return element;
	}

	@SuppressWarnings("unchecked")
	protected <T extends Element> ElementRef<T> getElementRef(final String id) {
		return (ElementRef<T>)elements.getRef(id);
	}

	protected ElementRef<Signal> getSignalRef(final String id) {
		return signals.getRef(id);
	}

	protected <T extends Element> ElementRef<T> getAttributeElementRef(
			final Node node, final String name) {
		return getAttributeElementRef(node, name, true);
	}

	protected <T extends Element> ElementRef<T> getAttributeElementRef(
			final Node node, final String name, final boolean required) {
		return getElementRef(getAttributeString(node, name, required));
	}

	protected ElementRef<Signal> getAttributeSignalRef(
			final Node node, final String name) {
		return getSignalRef(getAttributeString(node, name, true));
	}

	protected static Color convertStringToColor(final String value) {
		final StyleSheet stylesheet = new StyleSheet();
		return stylesheet.stringToColor(value);
	}

	protected VisibleElement getBPMNElementAttribute(final Node node) {
		return getAttributeElement(node, "bpmnElement"); //$NON-NLS-1$
	}

	private static boolean isValidPlaneElement(final VisibleElement planeElement) {
		return (planeElement instanceof ExpandedProcess)
				|| (planeElement instanceof Collaboration);
	}

	protected boolean readElementBPMNShape(final Node node, final VisibleElement plane) {
		if (isElementNode(node, BPMNDI, "BPMNShape")) { //$NON-NLS-1$
			VisibleElement element = getBPMNElementAttribute(node);
			if (element != null) {
				if (element instanceof ExpandedProcess) {
					final ExpandedProcess expandedProcess = (ExpandedProcess)element;
					if (!getIsExpandedAttribute(node)) {
						element = expandedProcess.createCollapsed();
					}
				}
				if (element instanceof TitledFlowElement) {
					final TitledFlowElement titledElementContainer = (TitledFlowElement)element;
					titledElementContainer.setHorizontal(getIsHorizontalAttribute(node));
				}
				plane.add(element, 0);
				readDiagramPlaneElementBounds(node, (FlowElement)element);
				readDiagramPlaneElementLabel(node, plane, element);
				element.initSubElements();
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNEdge(final Node node, final VisibleElement plane) {
		if (isElementNode(node, BPMNDI, "BPMNEdge")) { //$NON-NLS-1$
			final VisibleElement element = getBPMNElementAttribute(node);
			if (element != null) {
				plane.add(element, 0);
				readDiagramPlaneElementWaypoints(node, (ConnectingElement)element);
				readDiagramPlaneElementLabel(node, plane, element);
				element.initSubElements();
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNPlane(final Node node, final String name) {
		if (isElementNode(node, BPMNDI, "BPMNPlane")) { //$NON-NLS-1$
			final VisibleElement planeElement = getBPMNElementAttribute(node);
			if (planeElement != null) {
				if (isValidPlaneElement(planeElement)) {
					final NodeList childNodes = node.getChildNodes();
					for (int i = 0; i < childNodes.getLength(); ++i) {
						final Node childNode = childNodes.item(i);
						if (!readElementBPMNShape(childNode, planeElement)
								&& !readElementBPMNEdge(childNode, planeElement)) {
							showUnknowNode(childNode);
						}
					}
					final DiagramFrame diagramFrame = new DiagramFrame(planeElement, name);
					desktop.add(diagramFrame);
					diagramFrame.showFrame();
				} else {
					logFrame.addWarning(MessageFormat.format(
							Messages.getString("Protocol.invalidPlaneElement"), //$NON-NLS-1$
							planeElement));
				}
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNDiagram(final Node node) {
		if (isElementNode(node, BPMNDI, "BPMNDiagram")) { //$NON-NLS-1$
			final String name = getNameAttribute(node, false);
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementBPMNPlane(childNode, name)) {
					showUnknowNode(childNode);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	protected Point getPointAttribute(final Node node) {
		return new Point(
				(int)getAttributeFloat(node, "x"), //$NON-NLS-1$
				(int)getAttributeFloat(node, "y")); //$NON-NLS-1$
	}

	protected Dimension getDimensionAttribute(final Node node) {
		final int width = (int)getAttributeFloat(node, "width"); //$NON-NLS-1$
		final int height = (int)getAttributeFloat(node, "height"); //$NON-NLS-1$
		return new Dimension(width, height);
	}

	protected Rectangle getRectangleAttribute(final Node node) {
		return new Rectangle(getPointAttribute(node), getDimensionAttribute(node));
	}

	protected boolean getIsExpandedAttribute(final Node node) {
		return getAttributeBoolean(node, "isExpanded", false, true); //$NON-NLS-1$
	}

	protected Rectangle getBoundsElement(final Node node) {
		final Node boundsNode = getSingleSubElement(node, DC, "Bounds"); //$NON-NLS-1$
		if (boundsNode != null) {
			return getRectangleAttribute(boundsNode);
		}
		return null;
	}

	protected void readDiagramPlaneElementBounds(final Node node,
			final FlowElement element) {
		element.setInnerBounds(getBoundsElement(node));
	}

	protected void readDiagramPlaneElementWaypoints(final Node node,
			final ConnectingElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, DI, "waypoint")) { //$NON-NLS-1$
				element.addWaypoint(getPointAttribute(childNode));
			}
		}
	}

	protected void readDiagramPlaneElementLabel(final Node node,
			final JComponent planeElement, final VisibleElement element) {
		final Label label = element.getElementLabel();
		if (label != null) {
			final Node labelNode = getSingleSubElement(node, BPMNDI, "BPMNLabel"); //$NON-NLS-1$
			if (labelNode != null) {
				label.setBounds(getBoundsElement(labelNode));
			}
			planeElement.add(label, 0);
		}
	}

	protected String getIdAttribute(final Node node) {
		return getAttributeString(node, "id", true); //$NON-NLS-1$
	}

	protected String getNameAttribute(final Node node) {
		return getNameAttribute(node, true);
	}

	protected String getNameAttribute(final Node node, final boolean required) {
		return getAttributeString(node, "name", required); //$NON-NLS-1$
	}

	protected ElementRef<FlowElement> getSourceRefAttribute(final Node node) {
		return getAttributeElementRef(node, "sourceRef"); //$NON-NLS-1$
	}

	protected ElementRef<FlowElement> getTargetRefAttribute(final Node node) {
		return getAttributeElementRef(node, "targetRef"); //$NON-NLS-1$
	}

	protected boolean getIsHorizontalAttribute(final Node node) {
		return getAttributeBoolean(node, "isHorizontal", true, false); //$NON-NLS-1$
	}

	protected void readDefinitions(final Node node) {
		if (isElementNode(node, BPMN, "definitions")) { //$NON-NLS-1$
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementSignal(childNode)
						&& !readElementProcess(childNode, null)
						&& !readElementCollaboration(childNode)
						&& !readElementBPMNDiagram(childNode)) {
					showUnknowNode(childNode);
				}
			}
			logFrame.toFront();
		} else {
			logFrame.addError(Messages.getString("Protocol.noDefinitions")); //$NON-NLS-1$
		}
	}

	protected boolean readElementSignal(final Node node) {
		if (isElementNode(node, BPMN, "signal")) { //$NON-NLS-1$
			final Signal signal = new Signal(getIdAttribute(node),
					getNameAttribute(node));
			readBaseElement(node, signal);
			signals.set(signal);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementParticipant(final Node node) {
		if (isElementNode(node, BPMN, "participant")) { //$NON-NLS-1$
			final ElementRef<ExpandedProcess> processRef
				= getAttributeElementRef(node, "processRef", false); //$NON-NLS-1$
			final Pool pool = new Pool(getIdAttribute(node),
					getNameAttribute(node, false), processRef);
			readBaseElement(node, pool);
			elements.set(pool);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementMessageFlow(final Node node, final Collaboration collaboration) {
		if (isElementNode(node, BPMN, "messageFlow")) { //$NON-NLS-1$
			final MessageFlow messageFlow = new MessageFlow(getIdAttribute(node),
					getSourceRefAttribute(node), getTargetRefAttribute(node));
			readBaseElement(node, messageFlow);
			addElementToContainer(messageFlow, collaboration);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementCollaboration(final Node node) {
		if (isElementNode(node, BPMN, "collaboration")) { //$NON-NLS-1$
			final Collaboration collaboration = new Collaboration(getIdAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementDocumentation(childNode, collaboration)
						&& !readElementParticipant(childNode)
						&& !readElementMessageFlow(childNode, collaboration)) {
					showUnknowNode(childNode);
				}
			}
			elements.set(collaboration);
			return true;
		} else {
			return false;
		}
	}

	protected void readExtensionElementsPropertySignavio(final Node node,
			final Element element) {
		final String keyNode = getAttributeString(node, "metaKey", true); //$NON-NLS-1$
		final String valueNode = getAttributeString(node, "metaValue", true); //$NON-NLS-1$
		if ("bgcolor".equals(keyNode) //$NON-NLS-1$
				&& ((valueNode != null) && !valueNode.isEmpty())) {
			final Color color = convertStringToColor(valueNode);
			if ((color != null) && (element instanceof VisibleElement)) {
				((VisibleElement)element).setElementBackground(color);
			}
		}
	}

	protected void readExtensionElementsProperties(final Node node,
			final Element element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, EXTENSION_SIGNAVIO, "signavioMetaData")) { //$NON-NLS-1$
				readExtensionElementsPropertySignavio(childNode, element);
			} else {
				logFrame.addWarning(MessageFormat.format(Messages.getString("Protocol.unknownExtensionProperty"), childNode.getNodeName())); //$NON-NLS-1$
			}
		}
	}

	protected boolean readElementLane(final Node node,
			final ExpandedProcess process, final LaneSet laneSet) {
		if (isElementNode(node, BPMN, "lane")) { //$NON-NLS-1$
			final Lane lane = new Lane(getIdAttribute(node), getNameAttribute(node, false));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, lane)
						&& !readElementLaneSet(childNode, process, lane)) {
					showUnknowNode(childNode);
				}
			}
			addElementToContainer(lane, process);
			addElementToContainer(lane, laneSet);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementLaneSet(final Node node,
			final ExpandedProcess process, final Lane lane) {
		final boolean isChild = isElementNode(node, BPMN, "childLaneSet");
		if (isChild || isElementNode(node, BPMN, "laneSet")) { //$NON-NLS-1$
			final LaneSet laneSet = new LaneSet(getIdAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, lane)
						&& !readElementLane(childNode, process, laneSet)) {
					showUnknowNode(childNode);
				}
			}
			addElementToContainer(laneSet, process);
			addElementToContainer(laneSet, lane);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsIncomingOutgoing(final Node node, final FlowElement element) {
		if (isElementNode(node, BPMN, "incoming")) { //$NON-NLS-1$
			final String elementId = node.getTextContent();
			final ElementRef<SequenceFlow> elementRef = getElementRef(elementId);
			element.addIncoming(elementRef);
			return true;
		} else if (isElementNode(node, BPMN, "outgoing")) { //$NON-NLS-1$
			final String elementId = node.getTextContent();
			final ElementRef<SequenceFlow> elementRef = getElementRef(elementId);
			element.addOutgoing(elementRef);
			return true;
		} else {
			return false;
		}
	}

	protected void readDefaultSequenceFlowAttribute(final Node node,
			final ElementWithDefaultSequenceFlow element) {
		final ElementRef<SequenceFlow> elementRef = getAttributeElementRef(node, "default", false);
		if (elementRef != null) {
			element.setDefaultSequenceFlowRef(elementRef);
		}
	}

	protected boolean readElementExtensionElements(final Node node, final Element element) {
		if (isElementNode(node, BPMN, "extensionElements")) { //$NON-NLS-1$
			readExtensionElementsProperties(node, element);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForBaseElement(final Node node, final Element element) {
		return readElementExtensionElements(node, element)
				|| readElementDocumentation(node, element);
	}

	protected void readBaseElement(final Node node, final Element element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (!readElementsForBaseElement(childNode, element)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementsForFlowElement(final Node node, final FlowElement element) {
		return readElementsForBaseElement(node, element)
				|| readElementsIncomingOutgoing(node, element)
				|| readElementExtensionElements(node, element);
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

	protected boolean readEventDefinitions(final Node node, final AbstractEvent event) {
		if (isElementNode(node, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new TerminateEventDefinition());
		} else if (isElementNode(node, BPMN, "timerEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new TimerEventDefinition());
		} else if (isElementNode(node, BPMN, "messageEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new MessageEventDefinition());
		} else if (isElementNode(node, BPMN, "linkEventDefinition")) { //$NON-NLS-1$
			event.setDefinition(new LinkEventDefinition(getNameAttribute(node, true)));
		} else if (isElementNode(node, BPMN, "signalEventDefinition")) { //$NON-NLS-1$
			final ElementRef<Signal> signalRef = getAttributeSignalRef(node, "signalRef");
			event.setDefinition(new SignalEventDefinition(signalRef));
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
					&& !readEventDefinitions(childNode, event)) {
				showUnknowNode(childNode);
			}
		}
	}

	protected boolean readElementEventStart(final Node node,
			final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "startEvent")) { //$NON-NLS-1$
			final StartEvent element = new StartEvent(
					getIdAttribute(node), getNameAttribute(node),
					getAnimator().getInstanceManager());
			readEvent(node, element);
			addElementToContainer(element, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementEventIntermediateThrow(final Node node,
			final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "intermediateThrowEvent")) { //$NON-NLS-1$
			final IntermediateThrowEvent element = new IntermediateThrowEvent(
					getIdAttribute(node), getNameAttribute(node));
			readEvent(node, element);
			addElementToContainer(element, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementEventIntermediateCatch(final Node node,
			final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "intermediateCatchEvent")) { //$NON-NLS-1$
			final IntermediateCatchEvent element = new IntermediateCatchEvent(
					getIdAttribute(node), getNameAttribute(node));
			readEvent(node, element);
			addElementToContainer(element, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementEventEnd(final Node node,
			final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "endEvent")) { //$NON-NLS-1$
			final EndEvent element = new EndEvent(getIdAttribute(node),
					getNameAttribute(node), getAnimator().getInstanceManager());
			readEvent(node, element);
			addElementToContainer(element, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsDataAssociations(final Node node) {
		if (isElementNode(node, BPMN, "dataInputAssociation")
				|| isElementNode(node, BPMN, "dataOutputAssociation")) {
			final Node sourceElement = getSingleSubElement(node, BPMN, "sourceRef");
			final Node targetElement = getSingleSubElement(node, BPMN, "targetRef");
			final ElementRef<FlowElement> sourceRef = getElementRef(sourceElement.getTextContent());
			final ElementRef<FlowElement> targetRef = getElementRef(targetElement.getTextContent());
			final DataAssociation dataAssociation =
					new DataAssociation(getIdAttribute(node),
							sourceRef, targetRef);
			dataAssociation.setDirection(Direction.ONE);
			elements.set(dataAssociation);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementsForActivity(final Node node, final Activity activity) {
		return readElementsForFlowElement(node, activity)
				|| readElementsDataAssociations(node);
	}

	protected void readGateway(final Node node, final Gateway gateway) {
		readDefaultSequenceFlowAttribute(node, gateway);
		readFlowElement(node, gateway);
	}

	protected Association.Direction getParameterAssociationDirection(final Node node) {
		final String value = getAttributeString(node, "associationDirection", false);
		final Association.Direction direction = Association.Direction.byValue(value);
		return (direction == null) ? Association.Direction.NONE : direction; 
	}

	protected boolean readElementDocumentation(final Node node, final Element element) {
		if (isElementNode(node, BPMN, "documentation")) { //$NON-NLS-1$
			final String text = node.getTextContent();
			if (text != null && !text.isEmpty()) {
				element.setDocumentation(new Documentation(text));
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementAssociation(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "association")) { //$NON-NLS-1$
			final Association association = new Association(getIdAttribute(node),
					getSourceRefAttribute(node),
					getTargetRefAttribute(node));
			association.setDirection(getParameterAssociationDirection(node));
			readBaseElement(node, process);
			addElementToContainer(association, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementConditionExpression(final Node node, final SequenceFlow sequenceFlow) {
		if (isElementNode(node, BPMN, "conditionExpression")) {
			final String text = node.getTextContent();
			if (text != null && !text.isEmpty()) {
				sequenceFlow.setCondition(new Expression(text));
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementSequenceflow(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "sequenceFlow")) { //$NON-NLS-1$
			final SequenceFlow sequenceFlow = new SequenceFlow(
					getIdAttribute(node), getNameAttribute(node),
					getSourceRefAttribute(node),
					getTargetRefAttribute(node));
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementsForBaseElement(childNode, sequenceFlow)
						&& !readElementConditionExpression(childNode, sequenceFlow)) {
					showUnknowNode(childNode);
				}
			}
			addElementToContainer(sequenceFlow, process);
			// Es ist möglich des der Modeller keine Incoming/Outgoing-Elemente
			// für FlowElemente exportiert (z.B. BonitaStudio).
			// Deshalb werden diese jetzt noch einmal anhand des ConnectingElement
			// hinzugefügt.
			assignFlowElementsToConnectingElement(sequenceFlow);
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

	protected boolean readElementTask(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "manualTask")) { //$NON-NLS-1$
			final ManualTask element = new ManualTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "userTask")) { //$NON-NLS-1$
			final UserTask element = new UserTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "businessRuleTask")) { //$NON-NLS-1$
			final BusinessRuleTask element = new BusinessRuleTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "scriptTask")) { //$NON-NLS-1$
			final ScriptTask element = new ScriptTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "serviceTask")) { //$NON-NLS-1$
			final ServiceTask element = new ServiceTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "sendTask")) { //$NON-NLS-1$
			final SendTask element = new SendTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "receiveTask")) { //$NON-NLS-1$
			final ReceiveTask element = new ReceiveTask(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "task")) { //$NON-NLS-1$
			final Task element = new Task(getIdAttribute(node),
					getNameAttribute(node));
			readTask(node, element);
			addElementToContainer(element, process);
		} else {
			return false;
		}
		return true;
	}

	protected static String getTextElement(final Node node) {
		String text = ""; //$NON-NLS-1$
		final Node textNode = getSingleSubElement(node, BPMN, "text"); //$NON-NLS-1$
		if (textNode != null) {
			text = textNode.getTextContent();
		}
		return text;
	}

	protected boolean readElementTextAnnotation(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "textAnnotation")) { //$NON-NLS-1$
			final String text = getTextElement(node);
			final TextAnnotation textAnnotation = new TextAnnotation(getIdAttribute(node), text);
			readElementsForBaseElement(node, textAnnotation);
			addElementToContainer(textAnnotation, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGroup(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "group")) { //$NON-NLS-1$
			final Group group = new Group(getIdAttribute(node));
			readElementsForBaseElement(node, group);
			addElementToContainer(group, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementGateway(final Node node, final ExpandedProcess process) {
		if (isElementNode(node, BPMN, "parallelGateway")) { //$NON-NLS-1$
			final ParallelGateway element = new ParallelGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "inclusiveGateway")) { //$NON-NLS-1$
			final InclusiveGateway element = new InclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "exclusiveGateway")) { //$NON-NLS-1$
			final ExclusiveGateway element = new ExclusiveGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, process);
		} else if (isElementNode(node, BPMN, "eventBasedGateway")) { //$NON-NLS-1$
			final EventBasedGateway element = new EventBasedGateway(
					getIdAttribute(node), getNameAttribute(node));
			readGateway(node, element);
			addElementToContainer(element, process);
		} else {
			return false;
		}
		return true;
	}

	protected boolean readElementDataObject(final Node node, final ExpandedProcess process) {
		final boolean isReference = isElementNode(node, BPMN, "dataObjectReference"); //$NON-NLS-1$
		if (isReference || isElementNode(node, BPMN, "dataObject")) { //$NON-NLS-1$
			final DataObject dataObject = new DataObject(
					getIdAttribute(node), getNameAttribute(node));
			if (!isReference) {
				dataObject.setCollection(getAttributeBoolean(node, "isCollection", false, false));
			}
			readFlowElement(node, dataObject);
			addElementToContainer(dataObject, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementDataStore(final Node node, final ExpandedProcess process) {
		final boolean isReference = isElementNode(node, BPMN, "dataStoreReference"); //$NON-NLS-1$
		if (isReference || isElementNode(node, BPMN, "dataStore")) { //$NON-NLS-1$
			final DataStore dataStore = new DataStore(
					getIdAttribute(node), getNameAttribute(node));
			readFlowElement(node, dataStore);
			addElementToContainer(dataStore, process);
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementProcess(final Node node, final ExpandedProcess parentProcess) {
		final boolean isSubProcess = isElementNode(node, BPMN, "subProcess"); 
		if (isSubProcess || isElementNode(node, BPMN, "process")) {
			final ExpandedProcess process = new ExpandedProcess(this,
					getIdAttribute(node), getNameAttribute(node, false));
			readDefaultSequenceFlowAttribute(node, process);
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				if (!readElementProcess(childNode, process)
						&& !readElementsForFlowElement(childNode, process)
						&& !readElementEventStart(childNode, process)
						&& !readElementEventIntermediateThrow(childNode, process)
						&& !readElementEventIntermediateCatch(childNode, process)
						&& !readElementEventEnd(childNode, process)
						&& !readElementTask(childNode, process)
						&& !readElementGateway(childNode, process)
						&& !readElementAssociation(childNode, process)
						&& !readElementSequenceflow(childNode, process)
						&& !readElementTextAnnotation(childNode, process)
						&& !readElementGroup(childNode, process)
						&& !readElementDataObject(childNode, process)
						&& !readElementDataStore(childNode, process)
						&& !readElementLaneSet(childNode, process, null)) {
					showUnknowNode(childNode);
				}
			}
			if (!isSubProcess) {
				processes.add(process);
			}
			addElementToContainer(process, parentProcess);
			return true;
		} else {
			return false;
		}
	}

	protected void assignFlowElementsToConnectingElement(final SequenceFlow connectingElement) {
		final ElementRef<SequenceFlow> connectingRef = getElementRef(connectingElement.getId());
		if (connectingRef != null) {
			final FlowElement source = connectingElement.getSource(); 
			if (source != null) {
				source.addOutgoing(connectingRef);
			}
			final FlowElement target = connectingElement.getTarget();
			if (target != null) {
				target.addIncoming(connectingRef);
			}
		}
	}

	protected Schema loadSchema() throws SAXException {
		final URL resource = getClass().getResource("xsd/BPMN20.xsd");   //$NON-NLS-1$
		final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);  
		return factory.newSchema(resource);  
	}

	public void load(final File file) {
		try {
			final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setSchema(loadSchema());
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setCoalescing(true);
			documentBuilderFactory.setValidating(false);
			final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder(); 
			documentBuilder.setErrorHandler(this);
			final Document document = documentBuilder.parse(file);
			readDefinitions(document.getDocumentElement());
		} catch (IOException e) {
			logFrame.addException(e);
		} catch (ParserConfigurationException e) {
			logFrame.addException(e);
		} catch (SAXException e) {
			logFrame.addException(e);
		}
	}

	protected void showSAXParseException(final SAXParseException exception) {
		logFrame.addError("[" + exception.getLineNumber() + ":" + exception.getColumnNumber() + "] " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				exception.getLocalizedMessage());
	}

	@Override
	public void error(final SAXParseException exception) throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void fatalError(final SAXParseException exception) throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void warning(final SAXParseException exception) throws SAXException {
		showSAXParseException(exception);
	}

	public void close() {
		tokenAnimator.end();
		desktop.removeAll();
		desktop.repaint();
	}

	public boolean hasErrorMessages() {
		return logFrame.hasErrors();
	}

	public boolean hasMessages() {
		return logFrame.hasMessages();
	}

	public void showMessages() {
		logFrame.setVisible(true);
	}

	public Collection<StartEvent> getManuallStartEvents() {
		final Collection<StartEvent> startEvents = new ArrayList<StartEvent>(); 
		for (ElementRef<VisibleElement> element : elements.values()) {
			if (element.getElement() instanceof StartEvent) {
				final StartEvent startEvent = (StartEvent)element.getElement();
				if (startEvent.canStartManuell()) {
					startEvents.add(startEvent);
				}
			}
		}
		return startEvents;
	}

}
