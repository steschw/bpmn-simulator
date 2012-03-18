package bpmn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Vector;

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
import bpmn.element.event.EndEvent;
import bpmn.element.event.StartEvent;
import bpmn.element.gateway.ExclusiveGateway;
import bpmn.element.gateway.InclusiveGateway;
import bpmn.element.gateway.ParallelGateway;
import bpmn.element.task.BusinessRuleTask;
import bpmn.element.task.ManuallTask;
import bpmn.element.task.ReceiveTask;
import bpmn.element.task.ScriptTask;
import bpmn.element.task.SendTask;
import bpmn.element.task.ServiceTask;
import bpmn.element.task.Task;
import bpmn.element.task.UserTask;
import bpmn.token.TokenAnimator;

/*
 * Ids von Elementen sind nur innerhalb eines Prozesses eindeutig
 * Darstellungsinformationen im bpmndi-Namensraum beziehen sich immer
 * auf Elemente des angegebenen Prozesses (BPMNPlane)
 */
public class Model implements ErrorHandler {

	protected static final String BPMN = "http://www.omg.org/spec/BPMN/20100524/MODEL";  //$NON-NLS-1$
	protected static final String BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI"; //$NON-NLS-1$
	protected static final String DC = "http://www.omg.org/spec/DD/20100524/DC"; //$NON-NLS-1$
	protected static final String DI = "http://www.omg.org/spec/DD/20100524/DI"; //$NON-NLS-1$

	protected static final String EXTENSION_SIGNAVIO = "http://www.signavio.com"; //$NON-NLS-1$

	private TreeMap<String, ElementRef<BaseElement>> elements = new TreeMap<String, ElementRef<BaseElement>>(); 

	private JDesktopPane desktop = null;

	private LogFrame logFrame = new LogFrame(); 

	private TokenAnimator tokenAnimator = new TokenAnimator();

	public Model(JDesktopPane desktop) {
		super();
		this.desktop = desktop;
	}

	public TokenAnimator getAnimator() {
		return tokenAnimator;
	}

	protected void addElementToContainer(BaseElement element) {
		registerElementRef(element.getId(), element);
	}

	protected void addElementToContainer(BaseElement element, Collaboration collaboration) {
		registerElementRef(element.getId(), element);
		if (collaboration != null) {
			collaboration.addElement(element);
		}
	}

	protected void addElementToContainer(BaseElement element, ExpandedProcess process) {
		registerElementRef(element.getId(), element);
		if (process != null) {
			process.addElement(element);
		}
	}

	protected void addElementToContainer(LaneSet element, Pool pool) {
		registerElementRef(element.getId(), element);
		if (pool != null) {
			pool.addLaneSet(element);
		}
	}

	protected void addElementToContainer(Lane element, LaneSet laneSet) {
		registerElementRef(element.getId(), element);
		if (laneSet != null) {
			laneSet.addLane(element);
		}
	}

	protected void addElementToContainer(LaneSet element, Lane lane) {
		registerElementRef(element.getId(), element);
		if (lane != null) {
			lane.addLaneSet(element);
		}
	}

	protected void registerElementRef(final String id, BaseElement element) {
		if (elements.containsKey(id)) {
			ElementRef<BaseElement> elementRef = elements.get(id);
			if (elementRef.getElement() == null) {
				elementRef.setElement(element);
			} else {
				assert(elementRef.getElement() == element);
			}
		} else {
			elements.put(id, new ElementRef<BaseElement>(element));
		}
	}

	protected <TYPE extends BaseElement> ElementRef<TYPE> getElementRefById(final String id) {
		assert(id != null);
		if (!elements.containsKey(id)) {
			registerElementRef(id, null);
		}
		ElementRef<?> elementRef = elements.get(id); 
		return (ElementRef<TYPE>)elementRef;
	}

	protected <TYPE extends BaseElement> TYPE getElementById(final String id) {
		ElementRef<TYPE> elementRef = getElementRefById(id);
		return elementRef.getElement();
	}

	private static final void debugNode(final Node node) {
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
		logFrame.addError(MessageFormat.format(Messages.getString("Protocol.unknownElement"), node.getNodeName())); //$NON-NLS-1$
	}

	protected void showElementNotFound(final String id) {
		logFrame.addError(MessageFormat.format(Messages.getString("Protocol.elementNotFound"), id)); //$NON-NLS-1$
	}

	protected void showUnknowAttribute(final String name, final Node node) {
		logFrame.addWarning(MessageFormat.format(Messages.getString("Protocol.attributNotExist"), name, node.getNodeName())); //$NON-NLS-1$
	}

	protected static boolean isElementNode(Node node, final String namespace, final String name) {
		return ((node.getNodeType() == Node.ELEMENT_NODE)
				&& name.equals(node.getLocalName())
				&& namespace.equals(node.getNamespaceURI()));
	}

	/**
	 * Liefert ein Unterelement von einem Element und prüft dabei ob dieses nur einmal vorkommt
	 * @return Liefert null zurück wenn das Unterelement nicht gefunden wird. Bei mehreren wird das erste zurück gegeben.
	 */
	protected Node getSingleSubElement(final Node node, final String namespace, final String name) {
		Node subElement = null;
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
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

	protected String getAttributeString(final Node node, final String name, final boolean required) {
		Node attributeNode = node.getAttributes().getNamedItem(name);
		if (attributeNode == null) {
			if (required) {
				showUnknowAttribute(name, node);
			}
			return null;
		}
		return attributeNode.getNodeValue();
	}

	protected float getAttributeFloat(final Node node, final String name) {
		final String floatString = getAttributeString(node, name, true);
		float value = 0;
		try {
			value = Float.parseFloat(floatString);
		} catch (Exception e) {
		}
		return value;
	}

	protected boolean convertStringToBool(final String string, final boolean defaultValue) {
		if ((string == null) || string.isEmpty()) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(string);
		}
	}

	protected boolean getAttributeBoolean(final Node node, final String name,
			final boolean required, final boolean defaultValue) {
		final String boolString = getAttributeString(node, name, required);
		return convertStringToBool(boolString, defaultValue);
	}

	protected BaseElement getAttributeElement(Node node, final String name) {
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

	protected <TYPE extends BaseElement> ElementRef<TYPE> getAttributeElementRef(Node node, final String name) {
		final String elementId = getAttributeString(node, name, true);
		ElementRef<TYPE> element = null;
		if (elementId != null) {
			element = getElementRefById(elementId);
		}
		return element;
	}

	protected static Color convertStringToColor(final String value) {
		StyleSheet stylesheet = new StyleSheet();
		return stylesheet.stringToColor(value);
	}

	protected boolean isDocumentationNode(final Node node) {
		return isElementNode(node, BPMN, "documentation");
	}

	protected void readDefinitions(Node node) {
		if (isElementNode(node, BPMN, "definitions")) { //$NON-NLS-1$
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				Node childNode = childNodes.item(i);
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

	protected BaseElement getBPMNElementAttribute(Node node) {
		return getAttributeElement(node, "bpmnElement"); //$NON-NLS-1$
	}

	protected void readDiagram(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMNDI, "BPMNPlane")) { //$NON-NLS-1$
				final BaseElement planeElement = getBPMNElementAttribute(childNode);
				if (planeElement != null) {
					if ((planeElement instanceof ExpandedProcess) || (planeElement instanceof Collaboration)) {
						readDiagramPlaneElements(childNode, planeElement);
						DiagramFrame diagramFrame = new DiagramFrame(planeElement);
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

	protected Point getPointAttribute(Node node) throws NumberFormatException {
		return new Point((int)getAttributeFloat(node, "x"), (int)getAttributeFloat(node, "y")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected Rectangle getRectangleAttribute(Node node) throws NumberFormatException {
		final int width = (int)getAttributeFloat(node, "width"); //$NON-NLS-1$
		final int height = (int)getAttributeFloat(node, "height"); //$NON-NLS-1$
		return new Rectangle(getPointAttribute(node), new Dimension(width, height));
	}

	protected boolean getIsExpandedAttribute(Node node) {
		return getAttributeBoolean(node, "isExpanded", false, true); //$NON-NLS-1$
	}

	protected void readDiagramPlaneElementBounds(Node node, BaseElement element) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, DC, "Bounds")) { //$NON-NLS-1$
				element.setInnerBounds(getRectangleAttribute(childNode));
			}
		}
	}

	protected void readDiagramPlaneElementWaypoints(Node node, ConnectingElement element) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, DI, "waypoint")) { //$NON-NLS-1$
				element.addWaypoint(getPointAttribute(childNode));
			}
		}
	}

	protected void readDiagramPlaneElementLabel(Node node, JComponent planeElement, BaseElement element) {
		Label label = element.createElementLabel();
		if (label != null) {
			planeElement.add(label, 0);
		}
	}

	protected void readDiagramPlaneElements(Node node, JComponent planeElement) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			BaseElement element = getBPMNElementAttribute(childNode);
			if (element != null) {
				if (isElementNode(childNode, BPMNDI, "BPMNShape")) { //$NON-NLS-1$
					if (element instanceof ExpandedProcess) {
						ExpandedProcess expandedProcess = (ExpandedProcess)element;
						if (!getIsExpandedAttribute(childNode)) {
							element = expandedProcess.createCollapsed();
						}
					}
					if (element instanceof TitledFlowElement) {
						TitledFlowElement titledElementContainer = (TitledFlowElement)element;
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
			}
		}
	}

	protected String getIdAttribute(Node node) {
		return getAttributeString(node, "id", true); //$NON-NLS-1$
	}

	protected String getNameAttribute(Node node) {
		return getNameAttribute(node, true);
	}

	protected String getNameAttribute(Node node, final boolean required) {
		return getAttributeString(node, "name", required); //$NON-NLS-1$
	}

	protected ElementRef<FlowElement> getSourceRefAttribute(Node node) {
		return getAttributeElementRef(node, "sourceRef"); //$NON-NLS-1$
	}

	protected ElementRef<FlowElement> getTargetRefAttribute(Node node) {
		return getAttributeElementRef(node, "targetRef"); //$NON-NLS-1$
	}

	protected boolean getIsHorizontalAttribute(Node node) {
		return getAttributeBoolean(node, "isHorizontal", true, false); //$NON-NLS-1$
	}

	protected Pool readParticipant(final Node node) {
		final String id = getIdAttribute(node);
		final String name = getNameAttribute(node);
		ElementRef<ExpandedProcess> processRef = getAttributeElementRef(node, "processRef"); //$NON-NLS-1$
		Pool pool = new Pool(id, name, processRef);
		readExtensionElements(node, pool);
		return pool;
	}

	protected Collaboration readCollaboration(final Node node) {
		final String id = getIdAttribute(node);
		Collaboration collaboration = new Collaboration(id);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "participant")) { //$NON-NLS-1$
				addElementToContainer(readParticipant(childNode), collaboration);
			} else if (isElementNode(childNode, BPMN, "messageFlow")) { //$NON-NLS-1$
				MessageFlow messageFlow = new MessageFlow(getIdAttribute(childNode),
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

	protected ExpandedProcess readProcess(Node node) {
		final boolean isSubProcess = isElementNode(node, BPMN, "subProcess");  //$NON-NLS-1$
		final String name = isSubProcess ? getNameAttribute(node) : "Process"; //$NON-NLS-1$
		final String id = getIdAttribute(node);
		ExpandedProcess process = new ExpandedProcess(id, name);
		readProcessElements(node, process);
		addElementToContainer(process);
		return process;
	}

	protected void readExtensionElementsPropertySignavio(Node node, BaseElement element) {
		final String keyNode = getAttributeString(node, "metaKey", true); //$NON-NLS-1$
		final String valueNode = getAttributeString(node, "metaValue", true); //$NON-NLS-1$
		if ((keyNode != null) && ((valueNode != null) && !valueNode.isEmpty())) {
			if (keyNode.equals("bgcolor")) { //$NON-NLS-1$
				Color color = convertStringToColor(valueNode);
				if (color != null) {
					element.setBackground(color);
				}
			}
		}
	}

	protected void readExtensionElementsProperties(Node node, BaseElement element) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, EXTENSION_SIGNAVIO, "signavioMetaData")) { //$NON-NLS-1$
				readExtensionElementsPropertySignavio(childNode, element);
			} else {
				logFrame.addWarning(MessageFormat.format(Messages.getString("Protocol.unknownExtensionProperty"), childNode.getNodeName())); //$NON-NLS-1$
			}
		}
	}

	protected void readExtensionElements(Node node, BaseElement element) {
		Node extensionElementsNode = getSingleSubElement(node, BPMN, "extensionElements"); //$NON-NLS-1$
		if (extensionElementsNode != null) {
			readExtensionElementsProperties(extensionElementsNode, element);
		}
	}

	protected String getTextElement(Node node) {
		String text = ""; //$NON-NLS-1$
		Node textNode = getSingleSubElement(node, BPMN, "text"); //$NON-NLS-1$
		if (textNode != null) {
			text = textNode.getTextContent();
		}
		return text;
	}

	protected void readConditionExpressionElement(Node node, SequenceFlow sequenceFlow) {
		Node conditionExpressionNode = getSingleSubElement(node, BPMN, "conditionExpression"); //$NON-NLS-1$
		if (conditionExpressionNode != null) {
			sequenceFlow.setConditionExpression(conditionExpressionNode.getTextContent());
		}
	}

	protected void readIncomingElements(Node node, FlowElement element) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "incoming")) { //$NON-NLS-1$
				final String elementId = childNode.getTextContent();
				ElementRef<SequenceFlow> elementRef = getElementRefById(elementId);
				element.addIncoming(elementRef);
			}
		}
	}

	protected void readOutgoingElements(Node node, FlowElement element) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "outgoing")) { //$NON-NLS-1$
				final String elementId = childNode.getTextContent();
				ElementRef<SequenceFlow> elementRef = getElementRefById(elementId);
				element.addOutgoing(elementRef);
			}
		}
	}

	protected void readDefaultSequenceFlowAttribute(Node node, ElementWithDefaultSequenceFlow element) {
		final String ATTRIBUTE_NAME = "default"; //$NON-NLS-1$
		if (node.getAttributes().getNamedItem(ATTRIBUTE_NAME) != null) {
			ElementRef<SequenceFlow> elementRef = getAttributeElementRef(node, ATTRIBUTE_NAME); 
			element.setDefaultSequenceFlowRef(elementRef);
		}
	}

	protected Lane readLane(Node node) {
		Lane lane = new Lane(getIdAttribute(node), getNameAttribute(node, false));
		readExtensionElements(node, lane);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "childLaneSet")) { //$NON-NLS-1$
				addElementToContainer(readLaneSet(childNode), lane);
			}
		}
		return lane;
	}

	protected LaneSet readLaneSet(Node node) {
		LaneSet laneSet = new LaneSet(getIdAttribute(node));
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			if (isElementNode(childNode, BPMN, "lane")) { //$NON-NLS-1$
				addElementToContainer(readLane(childNode), laneSet);
			}
		}
		return laneSet;
	}

	protected void readEndEventDefinitions(Node node, EndEvent event) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMN, "terminateEventDefinition")) { //$NON-NLS-1$
				event.setTermination(true);
			}
		}
	}

	protected void readProcessElements(Node node, ExpandedProcess process) {
		readIncomingElements(node, process);
		readOutgoingElements(node, process);
		readDefaultSequenceFlowAttribute(node, process);
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node childNode = childNodes.item(i);
			debugNode(childNode);
			if (isElementNode(childNode, BPMN, "subProcess")) { //$NON-NLS-1$
				addElementToContainer(readProcess(childNode), process);
			} else {
				if (isDocumentationNode(childNode)) {
					// ignored
				} else if (isElementNode(childNode, BPMN, "incoming")
						|| isElementNode(childNode, BPMN, "outgoing")) {
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
							StartEvent element = new StartEvent(id, name, getAnimator().getInstanceController());
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "endEvent")) { //$NON-NLS-1$
							EndEvent element = new EndEvent(id, name, getAnimator().getInstanceController());
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readEndEventDefinitions(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "manualTask")) { //$NON-NLS-1$
							ManuallTask element = new ManuallTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "userTask")) { //$NON-NLS-1$
							UserTask element = new UserTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "businessRuleTask")) { //$NON-NLS-1$
							BusinessRuleTask element = new BusinessRuleTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "scriptTask")) { //$NON-NLS-1$
							ScriptTask element = new ScriptTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "serviceTask")) { //$NON-NLS-1$
							ServiceTask element = new ServiceTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "sendTask")) { //$NON-NLS-1$
							SendTask element = new SendTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "receiveTask")) { //$NON-NLS-1$
							ReceiveTask element = new ReceiveTask(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "task")) { //$NON-NLS-1$
							Task element = new Task(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readDefaultSequenceFlowAttribute(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "parallelGateway")) { //$NON-NLS-1$
							ParallelGateway element = new ParallelGateway(id, name);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "inclusiveGateway")) { //$NON-NLS-1$
							InclusiveGateway element = new InclusiveGateway(id, name);
							readDefaultSequenceFlowAttribute(childNode, element);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "exclusiveGateway")) { //$NON-NLS-1$
							ExclusiveGateway element = new ExclusiveGateway(id, name);
							readDefaultSequenceFlowAttribute(childNode, element);
							readIncomingElements(childNode, element);
							readOutgoingElements(childNode, element);
							readExtensionElements(childNode, element);
							addElementToContainer(element, process);
						} else if (isElementNode(childNode, BPMN, "sequenceFlow")) { //$NON-NLS-1$
							SequenceFlow sequenceFlow = new SequenceFlow(id, name,
									getSourceRefAttribute(childNode),
									getTargetRefAttribute(childNode));
							readExtensionElements(childNode, sequenceFlow);
							addElementToContainer(sequenceFlow, process);
							readConditionExpressionElement(childNode, sequenceFlow);
							// Es ist möglich des der Modeller keine Incoming/Outgoing-Elemente
							// für FlowElemente exportiert (z.B. BonitaStudio).
							// Deshalb werden diese jetzt noch einmal anhand des ConnectingElement
							// hinzugefügt.
							assignFlowElementsToConnectingElement(sequenceFlow);
//							process.setComponentZOrder(sequenceFlow, 0);
						} else if (isElementNode(childNode, BPMN, "association")) { //$NON-NLS-1$
							Association association = new Association(id, name,
									getSourceRefAttribute(childNode), getTargetRefAttribute(childNode));
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
		URL resource = getClass().getResource("xsd/BPMN20.xsd");   //$NON-NLS-1$
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);  
		return factory.newSchema(resource);  
	}

	public void load(File file) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setSchema(loadSchema());
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setCoalescing(true);
			documentBuilderFactory.setValidating(false);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder(); 
			documentBuilder.setErrorHandler(this);
			Document document = documentBuilder.parse(file);
			readDefinitions(document.getDocumentElement());
		} catch (ParserConfigurationException | SAXException | IOException e) {
			logFrame.addException(e);
		}
	}

	protected void showSAXParseException(SAXParseException exception) {
		logFrame.addError("[" + exception.getLineNumber() + ":" + exception.getColumnNumber() + "] " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				exception.getLocalizedMessage());
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException {
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
		Vector<StartEvent> startEvents = new Vector<StartEvent>(); 
		for (ElementRef<BaseElement> element : elements.values()) {
			if (element.getElement() instanceof StartEvent) {
				StartEvent startEvent = (StartEvent)element.getElement();
				if (startEvent.canStartManuell()) {
					startEvents.add(startEvent);
				}
			}
		}
		return startEvents;
	}

}
