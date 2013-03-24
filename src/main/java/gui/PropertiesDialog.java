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
package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bpmn.model.Model;

@SuppressWarnings("serial")
public class PropertiesDialog
		extends AbstractDialog {

	private final Model model;

	public PropertiesDialog(final JFrame parent, final Model model) {
		super(parent, Messages.getString("Properties.properties")); //$NON-NLS-1$

		this.model = model;

		create();
	}

	@Override
	protected void create() {
		super.create();

		getContentPane().add(createPropertiesPanel(), BorderLayout.CENTER);
	}

	protected static JTextField createField(final String text, final int width) {
		final JTextField textField = new JTextField(text);
		textField.setBorder(null);
		setComponentWidth(textField, width);
		textField.setEditable(false);
		textField.setCaretPosition(0);
		return textField;
	}

	protected JPanel createPropertiesPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(createGapBorder());

		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.BASELINE_LEADING;

		c.gridy = 0;

		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(new JLabel(Messages.getString("Properties.encoding")), c); //$NON-NLS-1$

		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(createField(model.getEncoding(), 260), c);

		c.gridy = 1;

		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(new JLabel(Messages.getString("Properties.exporterName")), c); //$NON-NLS-1$

		c.gridx = 1;
		c.gridwidth = 2;
		panel.add(createField(model.getExporter(), 260), c);

		c.gridy = 2;

		c.gridx = 0;
		c.gridwidth = 1;
		panel.add(new JLabel(Messages.getString("Properties.exporterVersion")), c); //$NON-NLS-1$

		c.gridx = 2;
		c.gridwidth = 1;
		panel.add(createField(model.getExporterVersion(), 120), c);

		return panel;
	}

	@Override
	protected JPanel createButtonPanel() {
		final JPanel panel = super.createButtonPanel();

		final JButton buttonClose = new JButton(Messages.getString("close")); //$NON-NLS-1$
		setComponentWidth(buttonClose, DEFAULT_BUTTON_WIDTH);
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PropertiesDialog.this.dispose();
			}
		});
		panel.add(buttonClose, BorderLayout.LINE_END);

		getRootPane().setDefaultButton(buttonClose);

		return panel;
	}

}
