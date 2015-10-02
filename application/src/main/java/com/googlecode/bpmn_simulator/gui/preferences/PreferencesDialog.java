/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.googlecode.bpmn_simulator.gui.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElements;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualElement;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualElements;
import com.googlecode.bpmn_simulator.animation.module.Module;
import com.googlecode.bpmn_simulator.animation.module.ModuleRegistry;
import com.googlecode.bpmn_simulator.gui.AbstractDialog;
import com.googlecode.bpmn_simulator.gui.Messages;

@SuppressWarnings("serial")
public class PreferencesDialog
		extends AbstractDialog {

	private static final URI BONITA_HOME_INFO = URI.create("http://documentation.bonitasoft.com/bonita-home");

	private final LocaleComboBox selectLanguage = new LocaleComboBox();

	private final JTextField editExternalEditor = new JTextField();

	private final JCheckBox checkKeepEvents
			= new JCheckBox(Messages.getString("Preferences.keepEvents")); //$NON-NLS-1$

	private final JCheckBox checkShowExclusiveSymbol
			= new JCheckBox(Messages.getString("Preferences.showSymbolInExclusiveGateway"));  //$NON-NLS-1$
	private final JCheckBox checkAntialiasing
			= new JCheckBox(Messages.getString("Preferences.enableAntialiasing"));  //$NON-NLS-1$

	private final JTextField editBonitaHome = new JTextField();

	public PreferencesDialog(final JFrame parent) {
		super(parent, Messages.getString("Preferences.preferences")); //$NON-NLS-1$
	}

	@Override
	protected JComponent createContent() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addTab(Messages.getString("Preferences.general"), createGeneralPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.behavior"), createBehaviorPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.display"), createDisplayPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.elementDefaults"), createElementsPanel()); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("Preferences.engine"), createEnginePanel()); //$NON-NLS-1$
		return tabbedPane;
	}

	@Override
	protected JPanel createButtonPanel() {
		final JPanel panel = super.createButtonPanel();
		panel.add(createCancelButton());
		panel.add(createOkButton());
		return panel;
	}

	protected JPanel createGeneralPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(createGapBorder());
		final GridBagConstraints c = new GridBagConstraints();
		c.insets = INSETS;

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
		c.weightx = 1;
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

	private static List<Class<? extends LogicalElement>> orderElements(final Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> elements) {
		final List<Class<? extends LogicalElement>> logicalElements = new ArrayList<>(elements.keySet());
		Collections.sort(logicalElements, new Comparator<Class<? extends LogicalElement>>() {
			@Override
			public int compare(final Class<? extends LogicalElement> o1, final Class<? extends LogicalElement> o2) {
				return LogicalElements.getName(o1).compareTo(LogicalElements.getName(o2));
			}
		});
		return logicalElements;
	}

	private static JComponent createModuleElementsConfig(final Module module) {
		final JPanel panel = new JPanel(new GridBagLayout());
		final Insets insets = new Insets(4, 4, 2, 2);

		final GridBagConstraints headerConstraints = new GridBagConstraints();
		headerConstraints.insets = insets;
		headerConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
		headerConstraints.fill = GridBagConstraints.HORIZONTAL;
		headerConstraints.gridy = 0;
		headerConstraints.gridx = 0;
		panel.add(new JLabel("Steps"), headerConstraints);
		headerConstraints.gridx = 1;
		panel.add(new JLabel("Background"), headerConstraints);
		headerConstraints.gridx = 2;
		panel.add(new JLabel("Foreground"), headerConstraints);

		final GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.insets = insets;
		titleConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
		titleConstraints.fill = GridBagConstraints.HORIZONTAL;
		titleConstraints.gridx = 0;
		titleConstraints.gridwidth = 3;
		int y = 1;
		final Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> elements = module.getElements();
		for (final Class<? extends LogicalElement> logicalElement : orderElements(elements)) {
			titleConstraints.gridy = ++y;
			final JLabel label = new JLabel(LogicalElements.getName(logicalElement));
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			panel.add(label, titleConstraints);

			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.weightx = 1.;
			constraints.gridx = 0;
			panel.add(new StepsSpinner(logicalElement), constraints);
			for (final Class<? extends VisualElement> visualElement : elements.get(logicalElement)) {
				constraints.gridy = ++y;
				constraints.gridx = 1;
				final JXColorSelectionButton backgroundColorSelection = new JXColorSelectionButton(new Color(VisualElements.getDefaultBackgroundColor(visualElement)));
				backgroundColorSelection.getChooser().getSelectionModel().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(final ChangeEvent e) {
						VisualElements.setDefaultBackgroundColor(visualElement, backgroundColorSelection.getChooser().getColor().getRGB());
					}
				});
				panel.add(backgroundColorSelection, constraints);
				constraints.gridx = 2;
				final JXColorSelectionButton foregroundColorSelection = new JXColorSelectionButton(new Color(VisualElements.getDefaultForegroundColor(visualElement)));
				foregroundColorSelection.getChooser().getSelectionModel().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(final ChangeEvent e) {
						VisualElements.setDefaultForegroundColor(visualElement, foregroundColorSelection.getChooser().getColor().getRGB());
					}
				});
				panel.add(foregroundColorSelection, constraints);
				constraints.gridy = ++y;
			}
		}
		final JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		return scrollPane;
	}

	protected JComponent createElementsPanel() {
		final JTabbedPane pane = new JTabbedPane();
		for (final Module module : ModuleRegistry.getDefault().getModules()) {
			pane.addTab(module.getDescription(), createModuleElementsConfig(module));
		}
		return pane;
	}

	private JPanel createEngineBonitaPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Bonita"));

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.LAST_LINE_START;
		constraints.insets = INSETS;
		constraints.gridy = 0;

		constraints.gridx = 0;
		panel.add(new JLabel("Home:"), constraints);

		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.;
		panel.add(createDirectoryEdit(editBonitaHome), constraints);
		constraints.weightx = 0.;

		constraints.gridx = 2;
		final HyperlinkAction actionInfo = HyperlinkAction.createHyperlinkAction(BONITA_HOME_INFO);
		actionInfo.setName(Messages.getString("info")); //$NON-NLS-1$
		actionInfo.setShortDescription(BONITA_HOME_INFO.toString());
		panel.add(new JXHyperlink(actionInfo), constraints);

		return panel;
	}

	private JPanel createEnginePanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(createGapBorder());
		panel.add(createEngineBonitaPanel());
		panel.add(Box.createVerticalGlue());
		return panel;
	}

	@Override
	protected void storeData() {
		final Config config = Config.getInstance();

		config.setLocale((Locale) selectLanguage.getSelectedItem());
		config.setExternalEditor(editExternalEditor.getText());

		config.setBonitaHome(editBonitaHome.getText());

		config.store();
	}

	@Override
	protected void loadData() {
		final Config config = Config.getInstance();

		selectLanguage.setSelectedItem(config.getLocale());
		editExternalEditor.setText(config.getExternalEditor());

		editBonitaHome.setText(config.getBonitaHome());
	}

	public static class StepsSpinner
			extends JSpinner {

		private final SpinnerNumberModel model = new SpinnerNumberModel(LogicalElements.Info.DEFAULT_STEP_COUNT, 0, Integer.MAX_VALUE, 1);

		public StepsSpinner(final Class<? extends LogicalElement> element) {
			super();
			model.setValue(LogicalElements.getDefaultStepCount(element));
			model.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					LogicalElements.setDefaultStepCount(element, model.getNumber().intValue());
				}
			});
			setModel(model);
		}

	}

}
