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
package gui.preferences;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import bpmn.model.core.common.AbstractFlowElement;
import bpmn.model.core.common.Behavior;
import bpmn.model.core.common.Visualization;

import gui.AbstractDialog;
import gui.Messages;

@SuppressWarnings("serial")
public class PreferencesDialog
		extends AbstractDialog {

	private final LocaleComboBox selectLanguage = new LocaleComboBox();

	private final JTextField editExternalEditor = new JTextField();

	private final JCheckBox checkKeepEvents
			= new JCheckBox(Messages.getString("Preferences.keepEvents")); //$NON-NLS-1$

	private final JCheckBox checkShowExclusiveSymbol
			= new JCheckBox(Messages.getString("Preferences.showSymbolInExclusiveGateway"));  //$NON-NLS-1$
	private final JCheckBox checkAntialiasing
			= new JCheckBox(Messages.getString("Preferences.enableAntialiasing"));  //$NON-NLS-1$

	private final JCheckBox checkIgnoreModelerColors
			= new JCheckBox(Messages.getString("Preferences.ignoreModelerColors")); //$NON-NLS-1$

	private final ColorSelector colorStartEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorIntermediateEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorEndEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorBoundaryEventBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorGatewayBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorTaskBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorProcessBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorDataObjectBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$
	private final ColorSelector colorDataStorageBackground
			= new ColorSelector(Messages.getString("Preferences.backgroundColor")); //$NON-NLS-1$

	public PreferencesDialog(final JFrame parent) {
		super(parent, Messages.getString("Preferences.preferences")); //$NON-NLS-1$

		create();

		load();
	}

	@Override
	protected void create() {
		super.create();

		getContentPane().add(createPreferencesPane(), BorderLayout.CENTER);
	}

	protected JTabbedPane createPreferencesPane() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab(Messages.getString("Preferences.general"), createGeneralPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.behavior"), createBehaviorPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.display"), createDisplayPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.elementDefaults"), createElementsPanel()); //$NON-NLS-1$
		return tabbedPane;
	}

	protected JPanel createGeneralPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(createGapBorder());
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(4, 4, 4, 4);

		c.gridy = 0;

		final StringBuilder textLanguage = new StringBuilder(Messages.getString("Preferences.language")); //$NON-NLS-1$
		textLanguage.append(": *"); //$NON-NLS-1$
		final JLabel labelLanguage = new JLabel(textLanguage.toString());
		labelLanguage.setLabelFor(selectLanguage);
		c.gridx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.BASELINE_LEADING;
		panel.add(labelLanguage, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		panel.add(selectLanguage, c);

		c.gridy = 1;

		c.gridx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		final StringBuilder textExternalEditor
				= new StringBuilder(Messages.getString("Preferences.externalEditor")); //$NON-NLS-1$
		textExternalEditor.append(':');
		final JLabel labelExternalEditor = new JLabel(textExternalEditor.toString());
		labelExternalEditor.setLabelFor(editExternalEditor);
		panel.add(labelExternalEditor, c);
		c.gridx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(editExternalEditor, c);

		c.gridy = 2;

		final StringBuilder textRestart = new StringBuilder("* "); //$NON-NLS-1$
		textRestart.append(Messages.getString("Preferences.requiresRestart")); //$NON-NLS-1$
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		panel.add(new JLabel(textRestart.toString()), c);

		return panel;
	}

	protected JPanel createBehaviorPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(createGapBorder());

		panel.add(checkKeepEvents);

		return panel;
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

		panel.add(new JLabel(Messages.getString("Preferences.intermediateEvent"))); //$NON-NLS-1$
		panel.add(colorIntermediateEventBackground);

		panel.add(new JLabel(Messages.getString("Preferences.endEvent"))); //$NON-NLS-1$
		panel.add(colorEndEventBackground);

		panel.add(new JLabel(Messages.getString("Preferences.boundaryEvent"))); //$NON-NLS-1$
		panel.add(colorBoundaryEventBackground);

		panel.add(new JLabel(Messages.getString("Preferences.gateway"))); //$NON-NLS-1$
		panel.add(colorGatewayBackground);

		panel.add(new JLabel(Messages.getString("Preferences.task"))); //$NON-NLS-1$
		panel.add(colorTaskBackground);

		panel.add(new JLabel(Messages.getString("Preferences.process"))); //$NON-NLS-1$
		panel.add(colorProcessBackground);

		panel.add(new JLabel(Messages.getString("Preferences.dataObject"))); //$NON-NLS-1$
		panel.add(colorDataObjectBackground);

		panel.add(new JLabel(Messages.getString("Preferences.dataStorage"))); //$NON-NLS-1$
		panel.add(colorDataStorageBackground);

		return panel;
	}

	@Override
	protected JPanel createButtonPanel() {
		final JPanel panel = super.createButtonPanel();

		final JButton buttonCancel = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
		setComponentWidth(buttonCancel, DEFAULT_BUTTON_WIDTH);
		buttonCancel.setMnemonic(KeyEvent.VK_C);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonCancel);

		panel.add(Box.createHorizontalStrut(10));

		final JButton buttonOk = new JButton(Messages.getString("ok"));  //$NON-NLS-1$
		setComponentWidth(buttonOk, DEFAULT_BUTTON_WIDTH);
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
		final Config config = Config.getInstance();

		config.setLocale((Locale)selectLanguage.getSelectedItem());
		config.setExternalEditor(editExternalEditor.getText());

		final Behavior behavior = AbstractFlowElement.getDefaultBehavior();
		behavior.setKeepTriggers(checkKeepEvents.isSelected());
		config.setBehavior(behavior);

		final Visualization visualization = AbstractFlowElement.getDefaultVisualization();
		visualization.setAntialiasing(checkAntialiasing.isSelected());
		visualization.setShowExclusiveGatewaySymbol(checkShowExclusiveSymbol.isSelected());
		visualization.setIgnoreColors(checkIgnoreModelerColors.isSelected());
		visualization.setBackground(Visualization.Element.EVENT_START,
				colorStartEventBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.EVENT_INTERMEDIATE,
				colorIntermediateEventBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.EVENT_END,
				colorEndEventBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.EVENT_BOUNDARY,
				colorBoundaryEventBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.GATEWAY,
				colorGatewayBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.TASK,
				colorTaskBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.PROCESS,
				colorProcessBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.DATA_OBJECT,
				colorDataObjectBackground.getSelectedColor());
		visualization.setBackground(Visualization.Element.DATA_STORAGE,
				colorDataStorageBackground.getSelectedColor());
		config.setVisualization(visualization);

		config.store();
	}

	protected void load() {
		final Config config = Config.getInstance();

		selectLanguage.setSelectedItem(config.getLocale());
		editExternalEditor.setText(config.getExternalEditor());

		final Behavior behavior = AbstractFlowElement.getDefaultBehavior();
		checkKeepEvents.setSelected(behavior.getKeepTriggers());

		final Visualization visualization = AbstractFlowElement.getDefaultVisualization();
		checkAntialiasing.setSelected(visualization.isAntialiasing());
		checkShowExclusiveSymbol.setSelected(visualization.getShowExclusiveGatewaySymbol());
		checkIgnoreModelerColors.setSelected(visualization.getIgnoreColors());
		colorStartEventBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.EVENT_START));
		colorIntermediateEventBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.EVENT_INTERMEDIATE));
		colorEndEventBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.EVENT_END));
		colorBoundaryEventBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.EVENT_BOUNDARY));
		colorGatewayBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.GATEWAY));
		colorTaskBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.TASK));
		colorProcessBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.PROCESS));
		colorDataObjectBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.DATA_OBJECT));
		colorDataStorageBackground.setSelectedColor(
				visualization.getBackground(Visualization.Element.DATA_STORAGE));
	}

}
