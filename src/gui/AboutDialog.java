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
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final String LICENSE =
			"Copyright (C) 2012 Stefan Schweitzer\n" //$NON-NLS-1$
			+ "\n"  //$NON-NLS-1$
			+ "This software was created by Stefan Schweitzer as a student's project at\n" //$NON-NLS-1$
			+ "Fachhochschule Kaiserslautern (University of Applied Sciences).\n" //$NON-NLS-1$
			+ "Supervisor: Professor Dr. Thomas Allweyer. For more information please see\n" //$NON-NLS-1$
			+ "http://www.fh-kl.de/~allweyer\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "Licensed under the Apache License, Version 2.0 (the \"License\");\n" //$NON-NLS-1$
			+ "you may not use this Software except in compliance with the License.\n" //$NON-NLS-1$
			+ "You may obtain a copy of the License at\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "       http://www.apache.org/licenses/LICENSE-2.0\n" //$NON-NLS-1$
			+ "\n" //$NON-NLS-1$
			+ "Unless required by applicable law or agreed to in writing, software\n" //$NON-NLS-1$
			+ "distributed under the License is distributed on an \"AS IS\" BASIS,\n" //$NON-NLS-1$
			+ "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" //$NON-NLS-1$
			+ "See the License for the specific language governing permissions and\n" //$NON-NLS-1$
			+ "limitations under the License."; //$NON-NLS-1$

	private static final String NAME = "BPMN Simulator"; 
	private static final String URL = "http://code.google.com/p/bpmn-simulator/"; 

	public AboutDialog() {
		super((Frame)null, Messages.getString("About.about"), true); //$NON-NLS-1$

		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setSize(400, 300);

		create();
	}

	protected JPanel createTabInfo() {
		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		panel.add(Box.createVerticalStrut(20));

		final StringBuffer applicationInfo = new StringBuffer(NAME);
		final String version = getClass().getPackage().getImplementationVersion();
		if (version != null) {
			applicationInfo.append(' ');
			applicationInfo.append(version);
		}
		final JLabel labelInfo = new JLabel(applicationInfo.toString());
		labelInfo.setFont(labelInfo.getFont().deriveFont(Font.BOLD, 20));
		labelInfo.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(labelInfo);

		panel.add(Box.createVerticalStrut(20));

		try {
			final Hyperlink hyperlink = new Hyperlink(new URI(URL)); //$NON-NLS-1$
			hyperlink.setAlignmentX(CENTER_ALIGNMENT);
			panel.add(hyperlink);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		panel.add(Box.createVerticalStrut(40));

		final StringBuffer javaInfo = new StringBuffer("Java: "); //$NON-NLS-1$
		javaInfo.append(System.getProperty("java.vendor")); //$NON-NLS-1$
		javaInfo.append(' '); //$NON-NLS-1$
		javaInfo.append(System.getProperty("java.version")); //$NON-NLS-1$
		javaInfo.append(" ("); //$NON-NLS-1$
		javaInfo.append(System.getProperty("java.home")); //$NON-NLS-1$
		javaInfo.append(')'); //$NON-NLS-1$
		final JLabel labelJava = new JLabel(javaInfo.toString()); 
		labelJava.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(labelJava);

		panel.add(Box.createVerticalStrut(20));

		final StringBuffer systemInfo = new StringBuffer("System: "); //$NON-NLS-1$
		systemInfo.append(System.getProperty("os.name")); //$NON-NLS-1$
		systemInfo.append(' '); //$NON-NLS-1$
		systemInfo.append(System.getProperty("os.version")); //$NON-NLS-1$
		systemInfo.append(' '); //$NON-NLS-1$
		systemInfo.append(System.getProperty("os.arch")); //$NON-NLS-1$
		final JLabel labelSystem = new JLabel(systemInfo.toString()); 
		labelSystem.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(labelSystem);

		return panel;
	}

	protected JPanel createTabLicence() {
		final JPanel panel = new JPanel(new BorderLayout());
		final JTextArea textArea = new JTextArea(LICENSE);
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 11)); //$NON-NLS-1$
		panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
		return panel;
	}

	protected JTabbedPane createTabbedPane() {
		final JTabbedPane pane = new JTabbedPane();
		pane.addTab(Messages.getString("About.info"), createTabInfo()); //$NON-NLS-1$
		pane.addTab(Messages.getString("About.licence"), createTabLicence()); //$NON-NLS-1$
		return pane;
	}

	protected void create() {
		setLayout(new BorderLayout(10, 10));

		getContentPane().add(createTabbedPane(), BorderLayout.CENTER);

		final JButton buttonClose = new JButton(Messages.getString("About.close")); //$NON-NLS-1$
		buttonClose.setMnemonic(KeyEvent.VK_C);
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				AboutDialog.this.dispose();
			}
		});
		getContentPane().add(buttonClose, BorderLayout.PAGE_END);
	}

}
