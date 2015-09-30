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
package com.googlecode.bpmn_simulator.gui.preferences;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.jdesktop.swingx.JXColorSelectionButton;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;

import com.googlecode.bpmn_simulator.animation.element.logical.LogicalElement;
import com.googlecode.bpmn_simulator.animation.element.visual.VisualElement;
import com.googlecode.bpmn_simulator.animation.module.Module;
import com.googlecode.bpmn_simulator.animation.module.ModuleRegistry;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance;
import com.googlecode.bpmn_simulator.bpmn.swing.di.Appearance.ElementAppearance;
import com.googlecode.bpmn_simulator.bpmn.swing.model.choreography.activities.ChoreographyTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.choreography.activities.SubChoreographyShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.MessageFlowEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.collaboration.ParticipantShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.SequenceFlowEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.AssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.GroupShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.artifacts.TextAnnotationShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.BoundaryEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.EndEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateCatchEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.IntermediateThrowEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.events.StartEventShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ComplexGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.EventBasedGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ExclusiveGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.InclusiveGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.core.common.gateways.ParallelGatewayShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.LaneShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.AdHocSubProcessShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.CallActivityShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.SubProcessShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.TransactionShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.BusinessRuleTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ManualTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ReceiveTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ScriptTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.SendTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.ServiceTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.TaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.activities.task.UserTaskShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataAssociationEdge;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataInputShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataObjectReferenceShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataOutputShape;
import com.googlecode.bpmn_simulator.bpmn.swing.model.process.data.DataStoreReferenceShape;
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

	private final JCheckBox checkIgnoreModelerColors
			= new JCheckBox(Messages.getString("Preferences.ignoreModelerColors")); //$NON-NLS-1$

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

	private static JComponent createModuleElementsConfig(final Module module) {
		final JPanel panel = new JPanel(new GridBagLayout());
		int y = 0;
		final GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.gridx = 0;
		titleConstraints.gridwidth = 6;
		titleConstraints.anchor = GridBagConstraints.BASELINE_LEADING;
		titleConstraints.insets = new Insets(4, 4, 2, 2);
		final Map<Class<? extends LogicalElement>, Set<Class<? extends VisualElement>>> elements = module.getElements();
		for (final Class<? extends LogicalElement> logicalElement : elements.keySet()) {
			titleConstraints.gridy = ++y;
			final JLabel label = new JLabel(logicalElement.getSimpleName());
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			panel.add(label, titleConstraints);

			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			panel.add(new JLabel("Dauer"), constraints);
			constraints.gridx = 1;
			panel.add(new JTextField("3"), constraints);
			for (final Class<? extends VisualElement> visualElement : elements.get(logicalElement)) {
				constraints.gridy = ++y;
				constraints.gridx = 2;
				panel.add(new JLabel("Background"), constraints);
				constraints.gridx = 3;
				panel.add(new JXColorSelectionButton(), constraints);
				constraints.gridx = 4;
				panel.add(new JLabel("Foreground"), constraints);
				constraints.gridx = 5;
				panel.add(new JXColorSelectionButton(), constraints);
				constraints.gridy = ++y;
			}
		}
		return new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}

	protected JComponent createElementsPanel() {
		final JTabbedPane pane = new JTabbedPane();
		for (final Module module : ModuleRegistry.getDefault().getModules()) {
			pane.addTab(module.getDescription(), createModuleElementsConfig(module));
		}
		return pane;
/*
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(createGapBorder());

		checkIgnoreModelerColors.setBorder(BorderFactory.createEmptyBorder(0, 0, GAP, 0));
		panel.add(checkIgnoreModelerColors, BorderLayout.PAGE_START);
		final JScrollPane elementsDefaultsScrollPane = new JScrollPane(createElementsDefaultsPanel(),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		elementsDefaultsScrollPane.setPreferredSize(new Dimension(400, 400));
		panel.add(elementsDefaultsScrollPane, BorderLayout.CENTER);
		return panel;
*/
	}

	private void addElementConfig(
			final JComponent component,
			final GridBagConstraints constraints,
			final Class<? extends VisualElement> clazz, final String name) {
		addElementConfig(component, constraints, clazz, name, true, true);
	}

	private void addElementConfig(
			final JComponent component,
			final GridBagConstraints constraints,
			final Class<? extends VisualElement> clazz, final String name,
			final boolean background, final boolean foreground) {
		++constraints.gridy;
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.LINE_START;
		component.add(new JLabel(name), constraints);
		try {
			Class.forName(clazz.getCanonicalName());
		} catch (ClassNotFoundException e) {
		}
		constraints.anchor = GridBagConstraints.CENTER;
		final ElementAppearance elementAppearance = Appearance.getDefault().getForElement(clazz);
		if (foreground) {
			constraints.gridx = 1;
			final JXColorSelectionButton colorSelector = new JXColorSelectionButton(elementAppearance.getForeground());
			component.add(colorSelector, constraints);
		}
		if (background) {
			constraints.gridx = 2;
			final JXColorSelectionButton colorSelector = new JXColorSelectionButton(elementAppearance.getBackground());
			component.add(colorSelector, constraints);
		}
	}

	protected JPanel createElementsDefaultsPanel() {
		final JPanel panel = new JPanel(new GridBagLayout());

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 1;
		constraints.gridx = 1;
		panel.add(new JLabel("Foreground"), constraints);
		constraints.gridx = 2;
		panel.add(new JLabel("Background"), constraints);

//		addElementConfig(panel, constraints, ProcessPlane.class, "Process");

		addElementConfig(panel, constraints, LaneShape.class, "Lane");

		addElementConfig(panel, constraints, AssociationEdge.class, "Association", false, true);
		addElementConfig(panel, constraints, GroupShape.class, "Group");
		addElementConfig(panel, constraints, TextAnnotationShape.class, "TextAnnotation");

		addElementConfig(panel, constraints, StartEventShape.class, "StartEvent");
		addElementConfig(panel, constraints, EndEventShape.class, "EndEvent");
		addElementConfig(panel, constraints, BoundaryEventShape.class, "BoundaryEvent");
		addElementConfig(panel, constraints, IntermediateCatchEventShape.class, "IntermediateCatchEvent");
		addElementConfig(panel, constraints, IntermediateThrowEventShape.class, "IntermediateThrowEvent");

		addElementConfig(panel, constraints, ExclusiveGatewayShape.class, "ExclusiveGateway");
		addElementConfig(panel, constraints, InclusiveGatewayShape.class, "InclusiveGateway");
		addElementConfig(panel, constraints, ParallelGatewayShape.class, "ParallelGateway");
		addElementConfig(panel, constraints, EventBasedGatewayShape.class, "EventBasedGateway");
		addElementConfig(panel, constraints, ComplexGatewayShape.class, "ComplexGateway");

		addElementConfig(panel, constraints, SequenceFlowEdge.class, "SequenceFlow", false, true);
		addElementConfig(panel, constraints, MessageFlowEdge.class, "MessageFlow", false, true);
		addElementConfig(panel, constraints, DataAssociationEdge.class, "DataAssociation", false, true);

		addElementConfig(panel, constraints, DataObjectReferenceShape.class, "DataObjectReference");
		addElementConfig(panel, constraints, DataStoreReferenceShape.class, "DataStoreReference");
		addElementConfig(panel, constraints, DataInputShape.class, "DataInput");
		addElementConfig(panel, constraints, DataOutputShape.class, "DataOutput");

		addElementConfig(panel, constraints, TaskShape.class, "Task");
		addElementConfig(panel, constraints, BusinessRuleTaskShape.class, "BusinessRuleTask");
		addElementConfig(panel, constraints, ManualTaskShape.class, "ManualTask");
		addElementConfig(panel, constraints, ReceiveTaskShape.class, "ReceiveTask");
		addElementConfig(panel, constraints, ScriptTaskShape.class, "ScriptTask");
		addElementConfig(panel, constraints, SendTaskShape.class, "SendTask");
		addElementConfig(panel, constraints, ServiceTaskShape.class, "ServiceTask");
		addElementConfig(panel, constraints, UserTaskShape.class, "UserTask");

		addElementConfig(panel, constraints, CallActivityShape.class, "CallActivity");

		addElementConfig(panel, constraints, SubProcessShape.class, "SubProcess");
		addElementConfig(panel, constraints, AdHocSubProcessShape.class, "AdHocSubProcess");
		addElementConfig(panel, constraints, TransactionShape.class, "Transaction");

		addElementConfig(panel, constraints, SubChoreographyShape.class, "SubChoreographyShape");
		addElementConfig(panel, constraints, ChoreographyTaskShape.class, "ChoreographyTask");
		addElementConfig(panel, constraints, ParticipantShape.class, "ParticipantShape");

		return panel;
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

		final Appearance appearance = Appearance.getDefault();
		appearance.setIgnoreExplicitColors(checkIgnoreModelerColors.isSelected());

		config.setBonitaHome(editBonitaHome.getText());

		config.store();
	}

	@Override
	protected void loadData() {
		final Config config = Config.getInstance();

		selectLanguage.setSelectedItem(config.getLocale());
		editExternalEditor.setText(config.getExternalEditor());

		final Appearance appearance = Appearance.getDefault();
		checkIgnoreModelerColors.setSelected(appearance.getIgnoreExplicitColors());

		editBonitaHome.setText(config.getBonitaHome());
	}

}
