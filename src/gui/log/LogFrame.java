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
package gui.log;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bpmn.exception.StructureException;
import bpmn.exception.StructureExceptionListener;

import gui.Messages;

@SuppressWarnings("serial")
public class LogFrame
		extends JFrame
		implements StructureExceptionListener {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 400;

	private final LogList listLog = new LogList();

	private int warningCount; 
	private int errorCount;

	public LogFrame() {
		super(Messages.getString("Log.messages")); //$NON-NLS-1$

		create();

		setLocationRelativeTo(null);
	}

	protected void create() {
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(listLog, BorderLayout.CENTER);

		final JPanel panel = new JPanel();
		final JButton buttonClose = new JButton(Messages.getString("Log.close")); //$NON-NLS-1$
		buttonClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				setVisible(false);
			}
		});
		panel.add(buttonClose);
		getContentPane().add(panel, BorderLayout.PAGE_END);

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setLocation(new Point(0, 0));
	}

	protected void addWarning(final String message) {
		listLog.addWarning(message);
		++warningCount;
	}

	protected void addError(final String message) {
		listLog.addError(message);
		++errorCount;
		setVisible(true);
		toFront();
	}

	protected void addException(final Exception exception) {
		addError(exception.toString());
	}

	public boolean hasMessages() {
		return (warningCount + errorCount) > 0;
	}

	public boolean hasErrors() {
		return errorCount > 0;
	}

	@Override
	public void onStructureException(final StructureException exception) {
		addError(exception.getMessage());
	}

	public void clear() {
		listLog.clear();
		warningCount = 0;
		errorCount = 0;
	}

}
