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

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
class PropertiesTable
		extends JTable {

	private static final KeyStroke COPY_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

	private TableColumn nameColumn = new TableColumn(0);
	private TableColumn valueColumn = new TableColumn(1);

	public PropertiesTable() {
		super();

		setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

		nameColumn.setHeaderValue("Name");
		addColumn(nameColumn);
		valueColumn.setHeaderValue("Value");
		addColumn(valueColumn);

		getTableHeader().setReorderingAllowed(false);

		setFillsViewportHeight(true);
		setAutoCreateRowSorter(true);
		setAutoCreateColumnsFromModel(false);

		registerKeyboardAction(new CopyActionListener(), "Copy", COPY_KEYSTROKE, WHEN_FOCUSED);
	}

	protected String propertiesAsString() {
		final StringBuilder builder = new StringBuilder();
		final Properties properties = getProperties();
		for (final String name : properties.stringPropertyNames()) {
			builder.append(name);
			builder.append('=');
			builder.append(properties.getProperty(name));
			builder.append(System.lineSeparator());
		}
		return builder.toString();
	}

	public Properties getProperties() {
		final Properties properties = new Properties();
		final TableModel model = getModel();
		for (final int selectedRow : getSelectedRows()) {
			final int row = convertRowIndexToModel(selectedRow);
			properties.setProperty(
					model.getValueAt(row, nameColumn.getModelIndex()).toString(),
					model.getValueAt(row, valueColumn.getModelIndex()).toString());
		}
		return properties;
	}

	public void setProperties(final Properties properties) {
		final DefaultTableModel tableModel = new DefaultTableModel(0, 2);
		int nameWidth = 0;
		final FontMetrics fontMetrics = getFontMetrics(getFont());
		for (final String name : properties.stringPropertyNames()) {
			nameWidth = Math.max(nameWidth, fontMetrics.stringWidth(name));
			tableModel.addRow(new Object[] { name, properties.getProperty(name) });
		}
		setModel(tableModel);
		final Dimension preferredSize = new Dimension(nameWidth * 2, Math.min(getRowCount(), 10) * getRowHeight());
		setPreferredScrollableViewportSize(preferredSize);
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	private class CopyActionListener
			implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(propertiesAsString()), null);
		}

	}

}