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
package com.googlecode.bpmn_simulator.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.googlecode.bpmn_simulator.animation.input.DefinitionListener;

@SuppressWarnings("serial")
public class LoadingDialog
		extends JDialog
		implements DefinitionListener, CallbackRunnable.Callback {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 180;

	private JLabel infoLabel = new JLabel();

	public LoadingDialog(final Window window) {
		super(window, "Loading...", ModalityType.DOCUMENT_MODAL);
		create();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(window);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	private void create() {
		getContentPane().setLayout(new BorderLayout());
		infoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		getContentPane().add(infoLabel, BorderLayout.PAGE_END);
	}

	public void run(final Runnable runnable) {
		final Thread thread = new Thread(new CallbackRunnable(runnable, this));
		thread.start();
		setVisible(true);
	}

	@Override
	public void onFinish() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(false);
			}
		});
	}

	@Override
	public void info(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				infoLabel.setText(message);
			}
		});
	}

	@Override
	public void warning(final String message) {
	}

	@Override
	public void error(final String message, final Throwable throwable) {
	}


}
