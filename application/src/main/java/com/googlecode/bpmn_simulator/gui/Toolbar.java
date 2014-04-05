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
package com.googlecode.bpmn_simulator.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.googlecode.bpmn_simulator.bpmn.model.AbstractBPMNModel;
import com.googlecode.bpmn_simulator.framework.execution.AbstractAnimator;
import com.googlecode.bpmn_simulator.framework.execution.AnimationListener;
import com.googlecode.bpmn_simulator.gui.log.LogFrame;



@SuppressWarnings("serial")
public class Toolbar
		extends JToolBar
		implements AnimationListener {

	private static final Icon ICON_OPEN = loadIcon("open.png"); //$NON-NLS-1$

	private static final Icon ICON_START = loadIcon("start.png"); //$NON-NLS-1$;
	private static final Icon ICON_RESET = loadIcon("stop.png"); //$NON-NLS-1$;

	private static final Icon ICON_PAUSE = loadIcon("pause.png"); //$NON-NLS-1$
	private static final Icon ICON_PLAY = loadIcon("play.png"); //$NON-NLS-1$
	private static final Icon ICON_STEP = loadIcon("step.png"); //$NON-NLS-1$
	private static final Icon ICON_SPEED = loadIcon("speed.png"); //$NON-NLS-1$
	private static final Icon ICON_MESSAGES = loadIcon("messages.png"); //$NON-NLS-1$
	private static final Icon ICON_MESSAGESERROR = loadIcon("messagesError.png"); //$NON-NLS-1$

	private JButton buttonOpen;

	private StartButton buttonStart;
	private JButton buttonReset;

	private JButton buttonPauseContinue;
	private JButton buttonStep;

	private JLabel labelSpeed;
	private SpeedSpinner spinnerSpeed;

	private JButton buttonMessages;

	private final LogFrame logFrame;

	private AbstractBPMNModel model;

	public Toolbar(final LogFrame logFrame) {
		super();

		this.logFrame = logFrame;

		create();
	}

	protected static ImageIcon loadIcon(final String filename) {
		final URL url = Toolbar.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	public void setModel(final AbstractBPMNModel model) {
		AbstractAnimator animator = getAnimator();
		if (animator != null) {
			animator.removeAnimationListener(this);
		}
		this.model = model;
		buttonStart.setModel(model);
		animator = getAnimator();
		if (animator != null) {
			animator.addAnimationListener(this);
			animator.setSpeed(spinnerSpeed.getSpeedFactor());
		}
		updateControls();
		updateMessages();
	}

	protected AbstractAnimator getAnimator() {
		return (model == null) ? null : model.getAnimator();
	}

	public JButton getOpenButton() {
		return buttonOpen;
	}

	protected void create() {

		buttonOpen = new JButton(ICON_OPEN);
		buttonOpen.setToolTipText(Messages.getString("Toolbar.open")); //$NON-NLS-1$
		add(buttonOpen);

		addSeparator(new Dimension(24, 32));

		buttonStart = new StartButton(ICON_START);
		buttonStart.setToolTipText(Messages.getString("Toolbar.start")); //$NON-NLS-1$
		add(buttonStart);

		buttonReset = new JButton(ICON_RESET);
		buttonReset.setToolTipText(Messages.getString("Toolbar.reset")); //$NON-NLS-1$
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				getAnimator().reset();
			}
		});
		add(buttonReset);

		addSeparator(new Dimension(24, 32));

		buttonPauseContinue = new JButton();
		buttonPauseContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				final AbstractAnimator animator = getAnimator();
				if (animator.isPaused()) {
					animator.play();
				} else {
					animator.pause();
				}
			}
		});
		add(buttonPauseContinue);

		buttonStep = new JButton(ICON_STEP);
		buttonStep.setToolTipText(Messages.getString("Toolbar.step")); //$NON-NLS-1$
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				getAnimator().step(3);
			}
		});
		add(buttonStep);

		addSeparator(new Dimension(32, 32));

		labelSpeed = new JLabel(ICON_SPEED);
		labelSpeed.setToolTipText(Messages.getString("Toolbar.frameRate")); //$NON-NLS-1$
		add(labelSpeed);

		spinnerSpeed = new SpeedSpinner();
		spinnerSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent event) {
				getAnimator().setSpeed(((SpeedSpinner)event.getSource()).getSpeedFactor());
			}
		});
		labelSpeed.setLabelFor(spinnerSpeed);
		add(spinnerSpeed);

		add(Box.createHorizontalGlue());

		buttonMessages = new JButton();
		buttonMessages.setBorderPainted(false);
		buttonMessages.setOpaque(false);
		buttonMessages.setFocusable(false);
		buttonMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				logFrame.setVisible(true);
			}
		});
		add(buttonMessages);

		updateControls();
		updateMessages();
	}

	protected void updateMessages() {
		if (model == null) {
			buttonMessages.setVisible(false);
			buttonMessages.setToolTipText(null);
		} else {
			buttonMessages.setVisible(logFrame.hasMessages());
			if (logFrame.hasErrors()) {
				buttonMessages.setIcon(ICON_MESSAGESERROR);
				buttonMessages.setToolTipText(Messages.getString("Toolbar.hintErrors")); //$NON-NLS-1$
			} else {
				buttonMessages.setIcon(ICON_MESSAGES);
				buttonMessages.setToolTipText(Messages.getString("Toolbar.hintMessages")); //$NON-NLS-1$
			}
		}
	}

	protected void updateControls() {
		final AbstractAnimator animator = getAnimator();

		final boolean isPaused = (animator != null) && animator.isPaused();

		buttonStart.setEnabled(animator != null);
		buttonReset.setEnabled(animator != null);

		buttonPauseContinue.setEnabled(animator != null);
		buttonStep.setEnabled(isPaused);
		spinnerSpeed.setEnabled(animator != null);
		labelSpeed.setEnabled(animator != null);

		buttonPauseContinue.setIcon(isPaused ? ICON_PLAY : ICON_PAUSE);
		buttonPauseContinue.setToolTipText(
				isPaused
						? Messages.getString("Toolbar.play") //$NON-NLS-1$
						: Messages.getString("Toolbar.pause")); //$NON-NLS-1$
	}

	@Override
	public void animationPlay() {
		updateControls();
	}

	@Override
	public void animationPause() {
		updateControls();
	}

	@Override
	public void animationReset() {
		updateControls();
	}

}
