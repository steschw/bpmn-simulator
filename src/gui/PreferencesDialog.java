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
import javax.swing.JPanel;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JCheckBox checkShowExclusiveSymbol = new JCheckBox(Messages.getString("Preferences.showSymbolInExclusiveGateway"));  //$NON-NLS-1$
	private final JCheckBox checkAntialiasing = new JCheckBox(Messages.getString("Preferences.enableAntialiasing"));  //$NON-NLS-1$

	public PreferencesDialog() {
		super((Frame)null, Messages.getString("Preferences.preferences"), true); //$NON-NLS-1$

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createOptionsPanel(), BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.PAGE_END);

		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		updateFromConfig();

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

	protected JPanel createOptionsPanel() {
		final JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));

		panel.add(checkShowExclusiveSymbol);
		panel.add(checkAntialiasing);

		return panel;
	}

	protected JPanel createButtonPanel() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
				updateToConfig();
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonOk);

		getRootPane().setDefaultButton(buttonOk);

		return panel;
	}

	protected void updateFromConfig() {
		final Config config = Config.getInstance();
		checkShowExclusiveSymbol.setSelected(config.isShowExclusiveGatewaySymbol());
		checkAntialiasing.setSelected(config.isAntialiasing());
	}

	protected void updateToConfig() {
		final Config config = Config.getInstance();
		config.setShowExclusiveGatewaySymbol(checkShowExclusiveSymbol.isSelected());
		config.setAntialiasing(checkAntialiasing.isSelected());
		config.store();
	}

}
