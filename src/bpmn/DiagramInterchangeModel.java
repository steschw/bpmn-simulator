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

import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bpmn.element.Collaboration;
import bpmn.element.AbstractConnectingElement;
import bpmn.element.AbstractFlowElement;
import bpmn.element.Label;
import bpmn.element.Rectangle;
import bpmn.element.TitledFlowElement;
import bpmn.element.VisibleElement;
import bpmn.element.activity.Process;
import bpmn.exception.StructureException;

public class DiagramInterchangeModel extends Model {

	protected static final String BPMNDI = "http://www.omg.org/spec/BPMN/20100524/DI"; //$NON-NLS-1$
	protected static final String DC = "http://www.omg.org/spec/DD/20100524/DC"; //$NON-NLS-1$
	protected static final String DI = "http://www.omg.org/spec/DD/20100524/DI"; //$NON-NLS-1$

	private final JDesktopPane desktop;

	public DiagramInterchangeModel(final JDesktopPane desktop) {
		super();
		this.desktop = desktop;
	}

	@Override
	public void close() {
		super.close();
		desktop.removeAll();
		desktop.repaint();
	}

	@Override
	protected boolean readElementsForDefinitionsElement(final Node node) {
		return super.readElementsForDefinitionsElement(node)
				|| readElementBPMNDiagram(node);
	}

	@SuppressWarnings("unchecked")
	protected <E extends VisibleElement> E getBPMNElementAttribute(final Node node, final Class<E> type)
			throws StructureException {
		final VisibleElement element = getAttributeElement(node, "bpmnElement"); //$NON-NLS-1$
		if (!type.isAssignableFrom(element.getClass())) {
			throw new StructureException(this, type.getSimpleName() + " expected");
		}
		return (E)element;
	}

	private static boolean isValidPlaneElement(final AbstractFlowElement planeElement) {
		return (planeElement instanceof Process)
				|| (planeElement instanceof Collaboration);
	}

	protected boolean readElementBPMNShape(final Node node, final VisibleElement plane)
			throws StructureException {
		if (isElementNode(node, BPMNDI, "BPMNShape")) { //$NON-NLS-1$
			AbstractFlowElement element = getBPMNElementAttribute(node, AbstractFlowElement.class);
			if (element != null) {
				if (element instanceof Process) {
					final Process expandedProcess = (Process)element;
					if (!getIsExpandedAttribute(node)) {
						element = expandedProcess.createCollapsed();
					}
				}
				if (element instanceof TitledFlowElement) {
					final TitledFlowElement titledElementContainer = (TitledFlowElement)element;
					titledElementContainer.setHorizontal(getIsHorizontalAttribute(node));
				}
				plane.add(element, 0);
				readDiagramPlaneElementBounds(node, element);
				readDiagramPlaneElementLabel(node, plane, element);
				element.initSubElements();
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNEdge(final Node node, final VisibleElement plane)
			throws StructureException {
		if (isElementNode(node, BPMNDI, "BPMNEdge")) { //$NON-NLS-1$
			final AbstractConnectingElement<?> element = getBPMNElementAttribute(node, AbstractConnectingElement.class);
			if (element != null) {
				plane.add(element, 0);
				readDiagramPlaneElementWaypoints(node, element);
				readDiagramPlaneElementLabel(node, plane, element);
				element.initSubElements();
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNPlane(final Node node, final String name)
			throws StructureException {
		if (isElementNode(node, BPMNDI, "BPMNPlane")) { //$NON-NLS-1$
			final AbstractFlowElement planeElement = getBPMNElementAttribute(node, AbstractFlowElement.class);
			if (planeElement != null) {
				if (isValidPlaneElement(planeElement)) {
					final NodeList childNodes = node.getChildNodes();
					for (int i = 0; i < childNodes.getLength(); ++i) {
						final Node childNode = childNodes.item(i);
						try {
							if (!readElementBPMNShape(childNode, planeElement)
									&& !readElementBPMNEdge(childNode, planeElement)) {
								showUnknowNode(childNode);
							}
						} catch (StructureException exception) {
							notifyStructureExceptionListeners(exception);
						}
					}
					final DiagramFrame diagramFrame = new DiagramFrame(planeElement, name);
					desktop.add(diagramFrame);
					diagramFrame.showFrame();
				} else {
					final StructureException exception = new StructureException(planeElement,
						MessageFormat.format(
							Messages.getString("Protocol.invalidPlaneElement"), //$NON-NLS-1$
							planeElement));
					notifyStructureExceptionListeners(exception);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	protected boolean readElementBPMNDiagram(final Node node) {
		if (isElementNode(node, BPMNDI, "BPMNDiagram")) { //$NON-NLS-1$
			final String name = getNameAttribute(node);
			final NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); ++i) {
				final Node childNode = childNodes.item(i);
				try {
					if (!readElementBPMNPlane(childNode, name)) {
						showUnknowNode(childNode);
					}
				} catch (StructureException exception) {
					notifyStructureExceptionListeners(exception);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	protected Rectangle getBoundsElement(final Node node) {
		final Node boundsNode = getSingleSubElement(node, DC, "Bounds"); //$NON-NLS-1$
		if (boundsNode != null) {
			return getRectangleAttribute(boundsNode);
		}
		return null;
	}

	protected void readDiagramPlaneElementBounds(final Node node,
			final AbstractFlowElement element) {
		element.setInnerBounds(getBoundsElement(node));
	}

	protected void readDiagramPlaneElementWaypoints(final Node node,
			final AbstractConnectingElement<?> element) {
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

}
