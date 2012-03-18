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

		private class ProtocolTableCellRenderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = 1L;

			public void setValue(Object value) {
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
			DefaultTableModel model = (DefaultTableModel)getModel();
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
		public boolean isCellEditable(int row, int column) {
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

			private Icon icon = null;
			private String message = null;

			public LogMessage(Icon icon, String message) {
				this.icon = icon;
				this.message = message;
			}

			@Override
			public void run() {
				DefaultTableModel model = (DefaultTableModel)getModel();
				model.addRow(new Object[] {icon, message});
			}

		}

		protected void addMessage(Icon icon, final String message) {
			// swing componenten sind nicht thread-safe
			// deshalb muss SwingUtilities.invokeLater verwenden werden
			SwingUtilities.invokeLater(new LogMessage(icon, message));
		}

	}

	private static final Icon iconError = loadResizedIcon(UIManager.getIcon("OptionPane.errorIcon"));
	private static final Icon iconWarning = loadResizedIcon(UIManager.getIcon("OptionPane.warningIcon")); 

	protected static final Icon loadResizedIcon(final Icon icon) {
		if ((icon != null) && (icon instanceof ImageIcon)) {
			Image image = ((ImageIcon)icon).getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
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
		getContent().addMessage(iconError, message);
	}

	public void addWarning(final String message) {
		getContent().addMessage(iconWarning, message);
	}

}