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
import java.util.Map;
import java.util.TreeMap;

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
import bpmn.element.activity.ExpandedProcess;
import bpmn.element.activity.task.*;
import bpmn.element.event.*;
import bpmn.element.gateway.*;
import bpmn.token.TokenAnimator;

public class Model implements ErrorHandler {

	protected static final String BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$
	protected static final String BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI"; //$NON-NLS-1$
	protected static final String DC = "http://www.omg.org/spec/DD/20100524/DC"; //$NON-NLS-1$
	protected static final String DI = "http://www.omg.org/spec/DD/20100524/DI"; //$NON-NLS-1$

	protected static final String EXTENSION_SIGNAVIO = "http://www.signavio.com"; //$NON-NLS-1$

	private final Map<String, ElementRef<BaseElement>> elements
			= new TreeMap<String, ElementRef<BaseElement>>(); 

	private final JDesktopPane desktop;

	private final LogFrame logFrame = new LogFrame(); 

	private final TokenAnimator tokenAnimator = new TokenAnimator();

	private static boolean ignoreColors;

	public static final void setIgnoreColors(final boolean ignoreColors) {
		Model.ignoreColors = ignoreColors;
	}

	public static final boolean getIgnoreColors() {
		return Model.ignoreColors;
	}

	public Model(final JDesktopPane desktop) {
		super();
		this.desktop = desktop;
	}

	public TokenAnimator getAnimator() {
		return tokenAnimator;
	}

	protected void addElementToContainer(final BaseElement element) {
		registerElementRef(element.getId(), element);
	}

	protected void addElementToContainer(final BaseElement element,
			final Collaboration collaboration) {
		registerElementRef(element.getId(), element);
		if (collaboration != null) {
			collaboration.addElement(element);
		}
	}

	protected void addElementToContainer(final BaseElement element,
			final ExpandedProcess process) {
		registerElementRef(element.getId(), element);
		assert process != null;
		if (process != null) {
			process.addElement(element);
		}
	}

	protected void addElementToContainer(final LaneSet element,
			final Pool pool) {
		registerElementRef(element.getId(), element);
		if (pool != null) {
			pool.addLaneSet(element);
		}
	}

	protected void addElementToContainer(final Lane element,
			final LaneSet laneSet) {
		registerElementRef(element.getId(), element);
		if (laneSet != null) {
			laneSet.addLane(element);
		}
	}

	protected void addElementToContainer(final LaneSet element,
			final Lane lane) {
		registerElementRef(element.getId(), element);
		if (lane != null) {
			lane.addLaneSet(element);
		}
	}

	protected void registerElementRef(final String id,
			final BaseElement element) {
		if (elements.containsKey(id)) {
			final ElementRef<BaseElement> elementRef = elements.get(id);
			if (!elementRef.hasElement()) {
				elementRef.setElement(element);
			} else {
				assert elementRef.getElement() == element;
			}
		} else {
			elements.put(id, new ElementRef<BaseElement>(element));
		}
	}

	protected <T extends BaseElement> ElementRef<T> getElementRefById(final String id) {
		assert id != null;
		if (!elements.containsKey(id)) {
			registerElementRef(id, null);
		}
		return (ElementRef<T>)elements.get(id);
	}

	protected <T extends BaseElement> T getElementById(final String id) {
		final ElementRef<T> elementRef = getElementRefById(id);
		return elementRef.getElement();
	}

	private static void debugNode(final Node node) {
		/*
		if (node == null) {
			System.out.println("null"); //$NON-NLS-1$
		} else {
			final String nodeName = node.getLocalName();
			System.out.println((nodeName == null ? "null" : nodeName) + " " + node.getNamespaceURI() + " " + node.getNodeValue() + " " + node.getNodeType()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		*/
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
	protected Node getSingleSubElement(final Node node, final String namespace, final String name) {
		Node subElement = null;
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
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

	protected boolean convertStringToBool(final String string,
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

	protected BaseElement getAttributeElement(final Node node, final String name) {
		final String elementId = getAttributeString(node, name, true);
		BaseElement element = null;
		if (elementId != null) {
			element = getElementById(elementId);
			if (element == null) {
				showElementNotFound(elementId);
			}
		}
		return element;
	}

	protected <T extends BaseElement> ElementRef<T> getAttributeElementRef(
			final Node node, final String name) {
		final String elementId = getAttributeString(node, name, true);
		ElementRef<T> element = null;
		if (elementId != null) {
			element = getElementRefById(elementId);
		}
		return element;
	}

	protected static Color convertStringToColor(final String value) {
		final StyleSheet stylesheet = new StyleSheet();
		return stylesheet.stringToColor(value);
	}

	protected boolean isDocumentationNode(final Node node) {
		return isElementNode(node, BPMN, "documentation"); //$NON-NLS-1$
	}

	protected void readDefinitions(final Node node) {
		if (isElementNode(node, BPMN, "definitions")) { //$NON-NLS-1$
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				debugNode(childNode);
				if (isElementNode(childNode, BPMN, "collaboration")) { //$NON-NLS-1$
					readCollaboration(childNode); 
				} else if (isElementNode(childNode, BPMN, "process")) { //$NON-NLS-1$
					readProcess(childNode);
				} else if (isElementNode(childNode, BPMNDI, "BPMNDiagram")) { //$NON-NLS-1$
					readDiagram(childNode);
				} else {
					showUnknowNode(childNode);
				}
			}
			logFrame.toFront();
		} else {
			logFrame.addError(Messages.getString("Protocol.noDefinitions")); //$NON-NLS-1$
		}
	}

	protected BaseElement getBPMNElementAttribute(final Node node) {
		return getAttributeElement(node, "bpmnElement"); //$NON-NLS-1$
	}

	private static boolean isValidPlaneElement(final BaseElement planeElement) {
		return (planeElement instanceof ExpandedProcess)
				|| (planeElement instanceof Collaboration);
	}

	protected void readDiagram(final Node node) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMNDI, "BPMNPlane")) { //$NON-NLS-1$
				final BaseElement planeElement = getBPMNElementAttribute(childNode);
				if (planeElement != null) {
					if (isValidPlaneElement(planeElement)) {
						readDiagramPlaneElements(childNode, planeElement);
						final DiagramFrame diagramFrame = new DiagramFrame(planeElement);
						desktop.add(diagramFrame);
						diagramFrame.showFrame();
					} else {
						logFrame.addWarning(MessageFormat.format(Messages.getString("Protocol.invalidPlaneElement"), planeElement)); //$NON-NLS-1$
					}
				}
			} else {
				showUnknowNode(childNode);
			}
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
			final BaseElement element) {
		element.setInnerBounds(getBoundsElement(node));
	}

	protected void readDiagramPlaneElementWaypoints(final Node node,
			final ConnectingElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, DI, "waypoint")) { //$NON-NLS-1$
				element.addWaypoint(getPointAttribute(childNode));
			}
		}
	}

	protected void readDiagramPlaneElementLabel(final Node node,
			final JComponent planeElement, final BaseElement element) {
		final Label label = element.getElementLabel();
		if (label != null) {
			final Node labelNode = getSingleSubElement(node, BPMNDI, "BPMNLabel"); //$NON-NLS-1$
			if (labelNode != null) {
				label.setBounds(getBoundsElement(labelNode));
			}
			planeElement.add(label, 0);
		}
	}

	protected void readDiagramPlaneElements(final Node node,
			final JComponent planeElement) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
			BaseElement element = getBPMNElementAttribute(childNode);
			if (element != null) {
				if (isElementNode(childNode, BPMNDI, "BPMNShape")) { //$NON-NLS-1$
					if (element instanceof ExpandedProcess) {
						final ExpandedProcess expandedProcess = (ExpandedProcess)element;
						if (!getIsExpandedAttribute(childNode)) {
							element = expandedProcess.createCollapsed();
						}
					}
					if (element instanceof TitledFlowElement) {
						final TitledFlowElement titledElementContainer = (TitledFlowElement)element;
						titledElementContainer.setHorizontal(getIsHorizontalAttribute(childNode));
					}
					planeElement.add(element, 0);
					readDiagramPlaneElementBounds(childNode, element);
					readDiagramPlaneElementLabel(childNode, planeElement, element);
				} else if (isElementNode(childNode, BPMNDI, "BPMNEdge")) { //$NON-NLS-1$
					planeElement.add(element, 0);
					readDiagramPlaneElementWaypoints(childNode, (ConnectingElement)element);
					readDiagramPlaneElementLabel(childNode, planeElement, element);
				} else {
					showUnknowNode(childNode);
				}
				element.initSubElements();
			}
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

	protected Pool readParticipant(final Node node) {
		final String id = getIdAttribute(node);
		final String name = getNameAttribute(node);
		final ElementRef<ExpandedProcess> processRef = getAttributeElementRef(node, "processRef"); //$NON-NLS-1$
		final Pool pool = new Pool(id, name, processRef);
		readExtensionElements(node, pool);
		return pool;
	}

	protected Collaboration readCollaboration(final Node node) {
		final String id = getIdAttribute(node);
		final Collaboration collaboration = new Collaboration(id);
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "participant")) { //$NON-NLS-1$
				addElementToContainer(readParticipant(childNode), collaboration);
			} else if (isElementNode(childNode, BPMN, "messageFlow")) { //$NON-NLS-1$
				final MessageFlow messageFlow = new MessageFlow(getIdAttribute(childNode),
						getSourceRefAttribute(childNode), getTargetRefAttribute(childNode));
				addElementToContainer(messageFlow, collaboration);
			} else if (isDocumentationNode(childNode)) {
				// ignored
			} else {
				showUnknowNode(childNode);
			}
		}
		addElementToContainer(collaboration);
		return collaboration;
	}

	protected ExpandedProcess readProcess(final Node node) {
		final boolean isSubProcess = isElementNode(node, BPMN, "subProcess");  //$NON-NLS-1$
		final String name = isSubProcess ? getNameAttribute(node) : "Process"; //$NON-NLS-1$
		final String id = getIdAttribute(node);
		final ExpandedProcess process = new ExpandedProcess(id, name);
		readProcessElements(node, process);
		addElementToContainer(process);
		return process;
	}

	protected void readExtensionElementsPropertySignavio(final Node node,
			final BaseElement element) {
		final String keyNode = getAttributeString(node, "metaKey", true); //$NON-NLS-1$
		final String valueNode = getAttributeString(node, "metaValue", true); //$NON-NLS-1$
		if (!getIgnoreColors()
				&& "bgcolor".equals(keyNode) //$NON-NLS-1$
				&& ((valueNode != null) && !valueNode.isEmpty())) {
			final Color color = convertStringToColor(valueNode);
			if (color != null) {
				element.setBackground(color);
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
				logFrame.addWarning(MessageFormat.format(Messages.getString("Protocol.unknownExtensionProperty"), childNode.getNodeName())); //$NON-NLS-1$
			}
		}
	}

	protected void readExtensionElements(final Node node, final BaseElement element) {
		final Node extensionElementsNode = getSingleSubElement(node, BPMN, "extensionElements"); //$NON-NLS-1$
		if (extensionElementsNode != null) {
			readExtensionElementsProperties(extensionElementsNode, element);
		}
	}

	protected String getTextElement(final Node node) {
		String text = ""; //$NON-NLS-1$
		final Node textNode = getSingleSubElement(node, BPMN, "text"); //$NON-NLS-1$
		if (textNode != null) {
			text = textNode.getTextContent();
		}
		return text;
	}

	protected Expression getConditionExpressionElement(final Node node,
			final String name) {
		Expression expression = null;
		final Node conditionExpressionNode = getSingleSubElement(node, BPMN, name);
		if (conditionExpressionNode != null) {
			expression = new Expression(conditionExpressionNode.getTextContent());
		}
		return expression;
	}

	protected void readIncomingElements(final Node node, final FlowElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "incoming")) { //$NON-NLS-1$
				final String elementId = childNode.getTextContent();
				final ElementRef<SequenceFlow> elementRef = getElementRefById(elementId);
				element.addIncoming(elementRef);
			}
		}
	}

	protected void readOutgoingElements(final Node node, final FlowElement element) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "outgoing")) { //$NON-NLS-1$
				final String elementId = childNode.getTextContent();
				final ElementRef<SequenceFlow> elementRef = getElementRefById(elementId);
				element.addOutgoing(elementRef);
			}
		}
	}

	protected void readDefaultSequenceFlowAttribute(final Node node,
			final ElementWithDefaultSequenceFlow element) {
		final String ATTRIBUTE_NAME = "default"; //$NON-NLS-1$
		if (node.getAttributes().getNamedItem(ATTRIBUTE_NAME) != null) {
			final ElementRef<SequenceFlow> elementRef = getAttributeElementRef(node, ATTRIBUTE_NAME); 
			element.setDefaultSequenceFlowRef(elementRef);
		}
	}

	protected Lane readLane(final Node node) {
		final Lane lane = new Lane(getIdAttribute(node), getNameAttribute(node, false));
		readExtensionElements(node, lane);
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "childLaneSet")) { //$NON-NLS-1$
				addElementToContainer(readLaneSet(childNode), lane);
			}
		}
		return lane;
	}

	protected LaneSet readLaneSet(final Node node) {
		final LaneSet laneSet = new LaneSet(getIdAttribute(node));
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "lane")) { //$NON-NLS-1$
				addElementToContainer(readLane(childNode), laneSet);
			}
		}
		return laneSet;
	}

	protected void readEndEventDefinitions(final Node node, final EndEvent event) {
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
				event.setTermination(true);
			}
		}
	}

	protected void readFlowElement(final Node node, final FlowElement element) {
		readIncomingElements(node, element);
		readOutgoingElements(node, element);
		if (element instanceof ElementWithDefaultSequenceFlow) {
			readDefaultSequenceFlowAttribute(node, (ElementWithDefaultSequenceFlow)element);
		}
		readExtensionElements(node, element);
	}

	protected void readEventStart(final Node node, final StartEvent event) {
		readFlowElement(node, event);
	}

	protected void readEventEnd(final Node node, final EndEvent event) {
		readFlowElement(node, event);
		readEndEventDefinitions(node, event);
	}

	protected void readTask(final Node node, final Task task) {
		readFlowElement(node, task);
	}

	protected void readGateway(final Node node, final Gateway gateway) {
		readFlowElement(node, gateway);
	}

	protected void readProcessElements(final Node node, final ExpandedProcess process) {
		readIncomingElements(node, process);
		readOutgoingElements(node, process);
		readDefaultSequenceFlowAttribute(node, process);
		final NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			final Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMN, "subProcess")) { //$NON-NLS-1$
				addElementToContainer(readProcess(childNode), process);
			} else {
				if (isDocumentationNode(childNode)) {
					// ignored
				} else if (isElementNode(childNode, BPMN, "incoming") //$NON-NLS-1$
						|| isElementNode(childNode, BPMN, "outgoing")) { //$NON-NLS-1$
					// elemente werden ignoriert, da diese bereits zu anfang eingelesen werden
				} else if (isElementNode(childNode, BPMN, "extensionElements")) { //$NON-NLS-1$
					readExtensionElements(node, process);
				} else {
					final String id = getIdAttribute(childNode);
					if (isElementNode(childNode, BPMN, "textAnnotation")) { //$NON-NLS-1$
						addElementToContainer(new TextAnnotation(id, getTextElement(childNode)), process);
					} else if (isElementNode(childNode, BPMN, "group")) { //$NON-NLS-1$
						addElementToContainer(new Group(id), process);
					} else if (isElementNode(childNode, BPMN, "laneSet")) { //$NON-NLS-1$
						addElementToContainer(readLaneSet(childNode), process);
					} else {
						final String name = getNameAttribute(childNode);
						if (isElementNode(childNode, BPMN, "startEvent")) { //$NON-NLS-1$
							final StartEvent element = new StartEvent(id, name, getAnimator().getInstanceController());
							readEventStart(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "endEvent")) { //$NON-NLS-1$
							final EndEvent element = new EndEvent(id, name, getAnimator().getInstanceController());
							readEventEnd(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "manualTask")) { //$NON-NLS-1$
							final ManualTask element = new ManualTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "userTask")) { //$NON-NLS-1$
							final UserTask element = new UserTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "businessRuleTask")) { //$NON-NLS-1$
							final BusinessRuleTask element = new BusinessRuleTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "scriptTask")) { //$NON-NLS-1$
							final ScriptTask element = new ScriptTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "serviceTask")) { //$NON-NLS-1$
							final ServiceTask element = new ServiceTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "sendTask")) { //$NON-NLS-1$
							final SendTask element = new SendTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "receiveTask")) { //$NON-NLS-1$
							final ReceiveTask element = new ReceiveTask(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "task")) { //$NON-NLS-1$
							final Task element = new Task(id, name);
							readTask(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "parallelGateway")) { //$NON-NLS-1$
							final ParallelGateway element = new ParallelGateway(id, name);
							readGateway(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "inclusiveGateway")) { //$NON-NLS-1$
							final InclusiveGateway element = new InclusiveGateway(id, name);
							readGateway(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "exclusiveGateway")) { //$NON-NLS-1$
							final ExclusiveGateway element = new ExclusiveGateway(id, name);
							readGateway(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "sequenceFlow")) { //$NON-NLS-1$
							final SequenceFlow sequenceFlow = new SequenceFlow(id, name,
									getSourceRefAttribute(childNode),
									getTargetRefAttribute(childNode));
							readExtensionElements(childNode, sequenceFlow);
							addElementToContainer(sequenceFlow, process);
							sequenceFlow.setCondition(getConditionExpressionElement(childNode, "conditionExpression")); //$NON-NLS-1$
							// Es ist möglich des der Modeller keine Incoming/Outgoing-Elemente
							// für FlowElemente exportiert (z.B. BonitaStudio).
							// Deshalb werden diese jetzt noch einmal anhand des ConnectingElement
							// hinzugefügt.
							assignFlowElementsToConnectingElement(sequenceFlow);
//							process.setComponentZOrder(sequenceFlow, 0);
						} else if (isElementNode(childNode, BPMN, "association")) { //$NON-NLS-1$
							final Association association = new Association(id, name,
									getSourceRefAttribute(childNode),
									getTargetRefAttribute(childNode));
							readExtensionElements(childNode, association);
							addElementToContainer(association, process);
						} else {
							showUnknowNode(childNode);
						}
					}
				}
			}
		}
	}

	protected void assignFlowElementsToConnectingElement(final SequenceFlow connectingElement) {
		final ElementRef<SequenceFlow> connectingRef = getElementRefById(connectingElement.getId());
		final FlowElement source = connectingElement.getSource(); 
		if (source != null) {
			source.addOutgoing(connectingRef);
		}
		final FlowElement target = connectingElement.getTarget();
		if (target != null) {
			target.addIncoming(connectingRef);
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
		for (ElementRef<BaseElement> element : elements.values()) {
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
