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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;

import bpmn.Model;
import bpmn.element.Graphics;
import bpmn.element.activity.task.Task;
import bpmn.element.event.EndEvent;
import bpmn.element.event.StartEvent;
import bpmn.element.gateway.ExclusiveGateway;
import bpmn.element.gateway.Gateway;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final int GAP = 10;

	private final JCheckBox checkShowExclusiveSymbol
			= new JCheckBox(Messages.getString("Preferences.showSymbolInExclusiveGateway"));  //$NON-NLS-1$
	private final JCheckBox checkAntialiasing
			= new JCheckBox(Messages.getString("Preferences.enableAntialiasing"));  //$NON-NLS-1$

	private final JCheckBox checkIgnoreModelerColors
			= new JCheckBox(Messages.getString("Preferences.ignoreModelerColors")); //$NON-NLS-1$

	private final ColorSelector colorStartEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorEndEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorGatewayBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorTaskBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$

	public PreferencesDialog() {
		super((Frame)null, Messages.getString("Preferences.preferences"), true); //$NON-NLS-1$

		create();

		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		load();

		pack();
	}

	private static void setButtonWidth(final JButton button) {
		setButtonWidth(button, 100);
	}

	private static void setButtonWidth(final JButton button, final int width) {
		final Dimension dimension = button.getPreferredSize();
		dimension.width = width;
		button.setPreferredSize(dimension);
	}

	protected void create() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createPreferencesPane(), BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.PAGE_END);
	}

	protected JTabbedPane createPreferencesPane() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(Messages.getString("Preferences.display"), createDisplayPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.elementDefaults"), createElementsPanel()); //$NON-NLS-1$
		return tabbedPane;
	}

	private static Border createGapBorder() {
		return BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP);
	}

	protected JPanel createDisplayPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(createGapBorder());

		panel.add(checkShowExclusiveSymbol);
		panel.add(checkAntialiasing);

		return panel;
	}

	protected JPanel createElementsPanel() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(createGapBorder());

		checkIgnoreModelerColors.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP, 0));
		panel.add(checkIgnoreModelerColors, BorderLayout.PAGE_START);
		panel.add(createElementsDefaultsPanel(), BorderLayout.CENTER);

		return panel;
	}

	protected JPanel createElementsDefaultsPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2, GAP, GAP));

		panel.add(new JLabel(Messages.getString("Preferences.startEvent"))); //$NON-NLS-1$
		panel.add(colorStartEventBackground);

		panel.add(new JLabel(Messages.getString("Preferences.endEvent"))); //$NON-NLS-1$
		panel.add(colorEndEventBackground);

		panel.add(new JLabel(Messages.getString("Preferences.gateway"))); //$NON-NLS-1$
		panel.add(colorGatewayBackground);

		panel.add(new JLabel(Messages.getString("Preferences.task"))); //$NON-NLS-1$
		panel.add(colorTaskBackground);

		return panel;
	}

	protected JPanel createButtonPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(createGapBorder());

		panel.add(Box.createHorizontalGlue());

		final JButton buttonCancel = new JButton(Messages.getString("Preferences.cancel")); //$NON-NLS-1$
		setButtonWidth(buttonCancel);
		buttonCancel.setMnemonic(KeyEvent.VK_C);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonCancel);

		panel.add(Box.createHorizontalStrut(10));

		final JButton buttonOk = new JButton(Messages.getString("Preferences.ok"));  //$NON-NLS-1$
		setButtonWidth(buttonOk);
		buttonOk.setMnemonic(KeyEvent.VK_O);
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				store();
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonOk);

		getRootPane().setDefaultButton(buttonOk);

		return panel;
	}

	protected void store() {
		Graphics.setAntialiasing(checkAntialiasing.isSelected());
		ExclusiveGateway.setShowSymbol(checkShowExclusiveSymbol.isSelected());

		Model.setIgnoreColors(checkIgnoreModelerColors.isSelected());

		StartEvent.setDefaultBackground(colorStartEventBackground.getSelectedColor());
		EndEvent.setDefaultBackground(colorEndEventBackground.getSelectedColor());
		Gateway.setDefaultBackground(colorGatewayBackground.getSelectedColor());
		Task.setDefaultBackground(colorTaskBackground.getSelectedColor());

		Config.getInstance().store();
	}

	protected void load() {
		checkAntialiasing.setSelected(Graphics.isAntialiasing());
		checkShowExclusiveSymbol.setSelected(ExclusiveGateway.getShowSymbol());

		checkIgnoreModelerColors.setSelected(Model.getIgnoreColors());

		colorStartEventBackground.setSelectedColor(StartEvent.getDefaultBackground());
		colorEndEventBackground.setSelectedColor(EndEvent.getDefaultBackground());
		colorGatewayBackground.setSelectedColor(Gateway.getDefaultBackground());
		colorTaskBackground.setSelectedColor(Task.getDefaultBackground());
	}

}
