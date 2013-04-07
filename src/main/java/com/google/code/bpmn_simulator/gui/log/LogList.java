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
package com.google.code.bpmn_simulator.gui.log;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

@SuppressWarnings("serial")
public class LogList
		extends JScrollPane {

	private static final Icon ICON_ERROR
			= loadResizedIcon(UIManager.getIcon("OptionPane.errorIcon")); //$NON-NLS-1$
	private static final Icon ICON_WARNING
			= loadResizedIcon(UIManager.getIcon("OptionPane.warningIcon")); //$NON-NLS-1$

	public LogList() {
		super(new LogListContent());
		setBorder(BorderFactory.createLoweredBevelBorder());
		setPreferredSize(new Dimension(100, 80));
	}

	protected static final Icon loadResizedIcon(final Icon icon) {
		if ((icon != null) && (icon instanceof ImageIcon)) {
			final Image image
					= ((ImageIcon)icon).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
			if (image != null) {
				return new ImageIcon(image);
			}
		}
		return null;
	}

	protected LogListContent getContent() {
		return (LogListContent)this.getViewport().getView();
	}

	public void addError(final String message) {
		getContent().addMessage(ICON_ERROR, message);
	}

	public void addWarning(final String message) {
		getContent().addMessage(ICON_WARNING, message);
	}

	public void clear() {
		getContent().clear();
	}

	private static class LogListContent
			extends JTable {

		private static final String TYPE_COLUMN = "Type";
		private static final String MESSAGE_COLUMN = "Nachricht";

		private static final int ROW_HEIGHT = 18;
		private static final int TYPE_COLUMN_WIDTH = 20;

		public LogListContent() {
			super();

			initColumns();

			setShowGrid(false);
			setFillsViewportHeight(true);
			setTableHeader(null);
			setRowHeight(ROW_HEIGHT);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setFocusable(false);
		}

		protected void initColumns() {
			final DefaultTableModel model = (DefaultTableModel) getModel();
			model.addColumn(TYPE_COLUMN);
			model.addColumn(MESSAGE_COLUMN);

			ProtocolTableCellRenderer cellRenderer = null;
			TableColumn column = null;

			column = getColumn(TYPE_COLUMN);
			column.setResizable(false);
			column.setWidth(TYPE_COLUMN_WIDTH);
			column.setMinWidth(TYPE_COLUMN_WIDTH);
			column.setMaxWidth(TYPE_COLUMN_WIDTH);
			cellRenderer = new ProtocolTableCellRenderer();
			cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			cellRenderer.setVerticalAlignment(DefaultTableCellRenderer.CENTER);
			column.setCellRenderer(cellRenderer);
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return false;
		}

		public synchronized void clear() {
			((DefaultTableModel)getModel()).getDataVector().removeAllElements();
			updateUI();
		}

		/*
		public void scrollToEnd() {
			scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
		}
		*/

		protected void addMessage(final Icon icon, final String message) {
			// swing componenten sind nicht thread-safe
			// deshalb muss SwingUtilities.invokeLater verwenden werden
			SwingUtilities.invokeLater(new LogMessage(icon, message));
		}

		private class LogMessage
				implements Runnable {

			private final Icon icon;
			private final String message;

			public LogMessage(final Icon icon, final String message) {
				this.icon = icon;
				this.message = message;
			}

			@Override
			public void run() {
				final DefaultTableModel model = (DefaultTableModel)getModel();
				model.addRow(new Object[] {
						icon,
						message,
						});
			}

		}

		private static class ProtocolTableCellRenderer
				extends DefaultTableCellRenderer {

			@Override
			public void setValue(final Object value) {
				if (value instanceof Icon) {
					setIcon((Icon)value);
					setText("");
				} else {
					setIcon(null);
					super.setValue(value);
				}
			}

		}

	}

}
