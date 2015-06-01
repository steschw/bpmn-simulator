/*
 * Copyright (C) 2015 Stefan Schweitzer
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
package com.googlecode.bpmn_simulator.tokenimport.bonita;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.bonitasoft.engine.identity.User;

@SuppressWarnings("serial")
class ConfigurationDialog
		extends JDialog {

	private static final int GAP = 4;

	private static Border EMPTY_BORDER = BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP);

	private final JCheckBox failedActivitiesCheck = new JCheckBox("Import also failed activity instances");

	private final JComboBox<UserFilter> userFilterComboBox = new JComboBox<>(UserFilter.values());

	private final JTable userTable = new JTable();
	private final ListTableModel<User> userModel;

	private boolean ok = false;

	public ConfigurationDialog(final List<User> users) {
		super((Frame) null, "Token import configuration", true);

		userModel = new ListTableModel<User>(users, "First name", "Last name", "Username") {

			@Override
			public Class<?> getColumnClass(final int columnIndex) {
				return String.class;
			}

			@Override
			protected Object getValue(final User value, final int columnIndex) {
				switch (columnIndex) {
					case 0:
						return value.getFirstName();
					case 1:
						return value.getLastName();
					case 2:
						return value.getUserName();
				}
				return null;
			}

		};
		userTable.setModel(userModel);

		create();

		setAlwaysOnTop(true);

		setSize(400, 400);

		setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		List<User> users = new ArrayList<>();
		ConfigurationDialog dialog = new ConfigurationDialog(users);
		dialog.setVisible(true);
		dialog.dispose();
	}

	private void create() {
		getContentPane().setLayout(new BorderLayout(GAP, GAP));
		getContentPane().add(createContentComponent(), BorderLayout.CENTER);
		getContentPane().add(createActionComponent(), BorderLayout.PAGE_END);
	}

	private static Border createTitleBorder(final String title) {
		return BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(title),
				EMPTY_BORDER);
	}

	protected Component createContentComponent() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(createConfigComponent(), BorderLayout.PAGE_START);
		panel.add(createFilterComponent(), BorderLayout.CENTER);
		return panel;
	}

	protected Component createConfigComponent() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(EMPTY_BORDER);
		panel.add(failedActivitiesCheck);
		return panel;
	}

	private void updateUserFilter() {
		Object selectedItem = userFilterComboBox.getSelectedItem();
		final boolean tableEnabled = (selectedItem != UserFilter.NONE);
		userTable.setEnabled(tableEnabled);
		userTable.getTableHeader().setEnabled(tableEnabled);
		if (!tableEnabled) {
			userTable.clearSelection();
		}
	}

	protected Component createUserFilterComponent() {
		final JPanel panel = new JPanel(new BorderLayout(GAP, GAP));
		panel.setBorder(createTitleBorder("by user"));
		final Component userSelectionComponent = createSelectionTable(userTable);
		userFilterComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUserFilter();
			}
		});
		panel.add(userFilterComboBox, BorderLayout.PAGE_START);
		userTable.setAutoCreateColumnsFromModel(true);
		userTable.setAutoCreateRowSorter(true);
		panel.add(userSelectionComponent, BorderLayout.CENTER);
		updateUserFilter();
		return panel;
	}

	protected Component createFilterComponent() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(createTitleBorder("Filter"));
		panel.add(createUserFilterComponent(), BorderLayout.CENTER);
		return panel;
	}

	protected static Component createSelectionTable(final JTable table) {
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.selectAll();
		table.setFillsViewportHeight(true);
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		final JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		final JButton selectAllButton = new JButton("Select all");
		selectAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				table.selectAll();
			}
		});
		actionPanel.add(selectAllButton);
		final JButton selectNoneButton = new JButton("Select none");
		selectNoneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				table.clearSelection();
			}
		});
		actionPanel.add(selectNoneButton);
		panel.add(actionPanel, BorderLayout.PAGE_END);
		return panel;
	}

	private Component createActionComponent() {
		final JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = false;
				setVisible(false);
			}
		});
		panel.add(buttonCancel);
		final JButton buttonOk = new JButton("OK");
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok = true;
				setVisible(false);
			}
		});
		panel.add(buttonOk);
		getRootPane().setDefaultButton(buttonOk);
		return panel;
	}

	public UserFilter getUserFilter() {
		return (UserFilter) userFilterComboBox.getSelectedItem();
	}

	public Collection<Long> getUserIds() {
		final Collection<Long> ids = new ArrayList<>();
		for (final int rowIndex : userTable.getSelectedRows()) {
			final int index = userTable.convertRowIndexToModel(rowIndex);
			ids.add(userModel.getRowData(index).getId());
		}
		return ids;
	}

	public void setImportFailed(boolean importFailed) {
		this.failedActivitiesCheck.setSelected(importFailed);
	}

	public boolean getImportFailedActivities() {
		return failedActivitiesCheck.isSelected();
	}

	public boolean showDialog() {
		setVisible(true);
		return ok;
	}

}
