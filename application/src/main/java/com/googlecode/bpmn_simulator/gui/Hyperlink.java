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
package com.googlecode.bpmn_simulator.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class Hyperlink
		extends JButton
		implements ActionListener {

	private URI uri;

	public Hyperlink(final URI uri) {
		this(uri, null);
	}

	public Hyperlink(final URI uri, final String text) {
		super(text);

		setURI(uri);

		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);
		setForeground(Color.BLUE);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		addActionListener(this);
		addMouseListener(new HoverAdapter());
	}

	public void setURI(final URI uri) {
		this.uri = uri;
		setToolTipText(uri.toString());
	}

	public URI getURI() {
		return uri;
	}

	@Override
	public String getText() {
		final String text = super.getText();
		if ((text == null) || text.isEmpty()) {
			return getURIText();
		}
		return text;
	}

	protected String getURIText() {
		final URI uri = getURI();
		if (uri == null) {
			return null;
		}
		return uri.toString();
	}

	protected void openURI() {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(getURI());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		openURI();
	}

	private static class HoverAdapter
			extends MouseAdapter {

		private static final Map<TextAttribute, Integer> HOVER_ATTRIBUTES
				= new HashMap<TextAttribute, Integer>();

		private Font defaultFont;

		static {
			HOVER_ATTRIBUTES.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			defaultFont = e.getComponent().getFont();
			e.getComponent().setFont(defaultFont.deriveFont(HOVER_ATTRIBUTES));
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			e.getComponent().setFont(defaultFont);
		}

	}

}
