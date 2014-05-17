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
package com.googlecode.bpmn_simulator.animation.input;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.swing.text.html.StyleSheet;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.googlecode.bpmn_simulator.animation.element.visual.Diagram;

public abstract class AbstractXmlDefinition<E extends Diagram<?>>
		extends AbstractDefinition<E>
		implements ErrorHandler {

	private final String schema;

	private String encoding;

	public AbstractXmlDefinition(final String schema) {
		super();
		this.schema = schema;
	}

	@Override
	public String getEncoding() {
		return encoding;
	}

	protected void showSAXParseException(final SAXParseException exception) {
		final StringBuilder message = new StringBuilder('[');
		message.append(exception.getLineNumber());
		message.append(':');
		message.append(exception.getColumnNumber());
		message.append("] "); //$NON-NLS-1$
		message.append(exception.getLocalizedMessage());
		notifyError(message.toString(), exception);
	}

	@Override
	public void error(final SAXParseException exception)
			throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void fatalError(final SAXParseException exception)
			throws SAXException {
		showSAXParseException(exception);
	}

	@Override
	public void warning(final SAXParseException exception)
			throws SAXException {
		showSAXParseException(exception);
	}

	protected void showUnknowNode(final Node node) {
		final StringBuilder builder = new StringBuilder();
		Node parentNode = node;
		while (parentNode != null) {
			builder.append(parentNode.getNodeName());
			parentNode = parentNode.getParentNode();
			if (parentNode != null) {
				builder.append(" < ");
			}
		}
		notifyWarning(MessageFormat.format("Unknown element node {0}", builder.toString()));
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

	protected float getAttributeFloat(final Node node, final String name) {
		return getAttributeFloat(node, name, 0.f);
	}

	protected float getAttributeFloat(final Node node,
			final String name, final float defaultValue) {
		final String value = getAttributeString(node, name);
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException exception) {
		}
		notifyWarning(MessageFormat.format("Invalid value {0} for float. Using {1} instead.", value, defaultValue));
		return defaultValue;
	}

	private boolean convertStringToBool(final String string,
			final boolean defaultValue) {
		if ((string != null) && !string.isEmpty()) {
			return Boolean.parseBoolean(string);
		}
		return defaultValue;
	}

	protected boolean getAttributeBoolean(final Node node,
			final String name, final boolean defaultValue) {
		return convertStringToBool(getAttributeString(node, name), defaultValue);
	}

	protected MimeType getAttributeMimeType(final Node node, final String name) {
		final String value = getAttributeString(node, name);
		if ((value != null) && !value.isEmpty()) {
			try {
				return new MimeType(value);
			} catch (MimeTypeParseException e) {
				notifyError(null, e);
			}
		}
		return null;
	}

	public static String getNodeText(final Node node) {
		final String text = node.getTextContent();
		assert text != null && !text.isEmpty();
		return text;
	}

	protected static Color convertStringToColor(final String value) {
		final StyleSheet stylesheet = new StyleSheet();
		return stylesheet.stringToColor(value);
	}

	protected Schema loadSchema()
			throws SAXException {
		if ((schema == null) || schema.isEmpty()) {
			return null;
		}
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		if (classLoader != null) {
			final URL url = classLoader.getResource(schema);
			if (url != null) {	
				final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				return factory.newSchema(url);
			}
		}
		notifyError(MessageFormat.format("Couldn''t load schema {0}", schema), null);
		return null;
	}

	protected abstract void loadData(final Node node);

	@Override
	public void load(final InputStream input) {
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
			final Document document = documentBuilder.parse(input);
			encoding = document.getInputEncoding();
			loadData(document.getDocumentElement());
		} catch (IOException e) {
			notifyError(null, e);
		} catch (ParserConfigurationException e) {
			notifyError(null, e);
		} catch (SAXException e) {
			notifyError(null, e);
		}
	}

}
