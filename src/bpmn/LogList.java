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

public class LogList extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private static class LogListContent extends JTable {

		private static final long serialVersionUID = 1L;

		private static final String COLUMN_TYPE = "Type";
		private static final String COLUMN_MESSAGE = "Nachricht";

		private static class ProtocolTableCellRenderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = 1L;

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

		public LogListContent() {
			super();

			initColumns();

			setShowGrid(false);
			setFillsViewportHeight(true);
			setTableHeader(null);
			setRowHeight(18);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setFocusable(false);
		}

		protected void initColumns() {
			final DefaultTableModel model = (DefaultTableModel)getModel();
			model.addColumn(COLUMN_TYPE);
			model.addColumn(COLUMN_MESSAGE);

			ProtocolTableCellRenderer cellRenderer = null;
			TableColumn column = null;

			column = getColumn(COLUMN_TYPE);
			column.setResizable(false);
			column.setWidth(20);
			column.setMinWidth(20);
			column.setMaxWidth(20);
			cellRenderer = new ProtocolTableCellRenderer();
			cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			cellRenderer.setVerticalAlignment(DefaultTableCellRenderer.CENTER);
			column.setCellRenderer(cellRenderer);
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return false;
		}

/*
		public synchronized void clear() {
			((DefaultTableModel)getModel()).getDataVector().removeAllElements();
			updateUI();
		}

		public void scrollToEnd() {
			scrollRectToVisible(getCellRect(getRowCount() - 1, 0, true));
		}
*/

		private class LogMessage implements Runnable {

			private final Icon icon;
			private final String message;

			public LogMessage(final Icon icon, final String message) {
				this.icon = icon;
				this.message = message;
			}

			@Override
			public void run() {
				final DefaultTableModel model = (DefaultTableModel)getModel();
				model.addRow(new Object[] {icon, message});
			}

		}

		protected void addMessage(final Icon icon, final String message) {
			// swing componenten sind nicht thread-safe
			// deshalb muss SwingUtilities.invokeLater verwenden werden
			SwingUtilities.invokeLater(new LogMessage(icon, message));
		}

	}

	private static final Icon ICON_ERROR = loadResizedIcon(UIManager.getIcon("OptionPane.errorIcon"));
	private static final Icon ICON_WARNING = loadResizedIcon(UIManager.getIcon("OptionPane.warningIcon")); 

	protected static final Icon loadResizedIcon(final Icon icon) {
		if ((icon != null) && (icon instanceof ImageIcon)) {
			final Image image = ((ImageIcon)icon).getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
			if (image != null) {
				return new ImageIcon(image);
			}
		}
		return null;
	}

	public LogList() {
		super(new LogListContent());
		setBorder(BorderFactory.createLoweredBevelBorder());
		setPreferredSize(new Dimension(100, 80));
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

}
