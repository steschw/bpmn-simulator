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
package bpmn.model;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.text.html.StyleSheet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bpmn.exception.StructureException;
import bpmn.exception.StructureExceptionListener;

public abstract class AbstractXmlModel
	implements Model, ErrorHandler {

	private final Collection<StructureExceptionListener> structureExceptionListeners
		= new ArrayList<StructureExceptionListener>();

	public void addStructureExceptionListener(final StructureExceptionListener listener) {
		synchronized (structureExceptionListeners) {
			structureExceptionListeners.add(listener);
		}
	}

	protected void notifyStructureExceptionListeners(final StructureException exception) {
		synchronized (structureExceptionListeners) {
			for (StructureExceptionListener listener : structureExceptionListeners) {
				listener.onStructureException(exception);
			}
		}
	}

	protected void showSAXParseException(final SAXParseException exception) {
		final StringBuilder message = new StringBuilder('[');
		message.append(exception.getLineNumber());
		message.append(':');
		message.append(exception.getColumnNumber());
		message.append("] ");
		message.append(exception.getLocalizedMessage());
		notifyStructureExceptionListeners(new StructureException(this, message.toString()));
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

	protected static boolean isElementNode(final Node node,
			final String namespace, final String name) {
		return (node.getNodeType() == Node.ELEMENT_NODE)
				&& name.equals(node.getLocalName())
				&& namespace.equals(node.getNamespaceURI());
	}

	protected static String getAttributeString(final Node node, final String name) {
		final Node attributeNode = node.getAttributes().getNamedItem(name);
		if (attributeNode == null) {
			return null;
		}
		return attributeNode.getNodeValue();
	}

	protected static float getAttributeFloat(final Node node, final String name) {
		try {
			return Float.parseFloat(getAttributeString(node, name));
		} catch (NumberFormatException exception) {
			return 0;
		}
	}

	private static boolean convertStringToBool(final String string,
			final boolean defaultValue) {
		if ((string == null) || string.isEmpty()) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(string);
		}
	}

	protected static boolean getAttributeBoolean(final Node node,
			final String name, final boolean defaultValue) {
		return convertStringToBool(getAttributeString(node, name), defaultValue);
	}

	protected static Color convertStringToColor(final String value) {
		final StyleSheet stylesheet = new StyleSheet();
		return stylesheet.stringToColor(value);
	}

	protected abstract Schema loadSchema() throws SAXException;

	protected abstract void loadData(final Node node);

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
			loadData(document.getDocumentElement());
		} catch (IOException e) {
			notifyStructureExceptionListeners(new StructureException(this, e));
		} catch (ParserConfigurationException e) {
			notifyStructureExceptionListeners(new StructureException(this, e));
		} catch (SAXException e) {
			notifyStructureExceptionListeners(new StructureException(this, e));
		}
	}

}
