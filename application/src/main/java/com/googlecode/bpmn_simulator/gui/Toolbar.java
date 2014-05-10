/*
 * Copyright (C) 2014 Stefan Schweitzer
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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.googlecode.bpmn_simulator.animation.execution.AnimationListener;
import com.googlecode.bpmn_simulator.animation.execution.Animator;
import com.googlecode.bpmn_simulator.gui.log.LogFrame;

@SuppressWarnings("serial")
public class Toolbar
		extends JToolBar
		implements AnimationListener {

	private final LogFrame logFrame;

	private Animator animator;

	private JButton buttonOpen;

	private StartButton buttonStart;
	private JButton buttonReset;

	private JButton buttonPauseContinue;
	private JButton buttonStep;

	private JLabel labelSpeed;
	private SpeedSpinner spinnerSpeed;

	private JButton buttonMessages;

	public Toolbar(final LogFrame logFrame) {
		super();

		this.logFrame = logFrame;

		create();
	}

	public void setAnimator(final Animator animator) {
		if (this.animator != null) {
			this.animator.removeAnimationListener(this);
		}
		this.animator = animator;
		if (this.animator != null) {
			this.animator.addAnimationListener(this);
		}
	}

	public JButton getOpenButton() {
		return buttonOpen;
	}

	protected void create() {

		buttonOpen = new JButton(Theme.ICON_OPEN);
		buttonOpen.setToolTipText(Messages.getString("Toolbar.open")); //$NON-NLS-1$
		add(buttonOpen);

		addSeparator(new Dimension(24, 32));

		buttonStart = new StartButton(Theme.ICON_START);
		buttonStart.setToolTipText(Messages.getString("Toolbar.start")); //$NON-NLS-1$
		add(buttonStart);

		buttonReset = new JButton(Theme.ICON_RESET);
		buttonReset.setToolTipText(Messages.getString("Toolbar.reset")); //$NON-NLS-1$
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				animator.reset();
			}
		});
		add(buttonReset);

		addSeparator(new Dimension(24, 32));

		buttonPauseContinue = new JButton();
		buttonPauseContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				if (animator.isPaused()) {
					animator.play();
				} else {
					animator.pause();
				}
			}
		});
		add(buttonPauseContinue);

		buttonStep = new JButton(Theme.ICON_STEP);
		buttonStep.setToolTipText(Messages.getString("Toolbar.step")); //$NON-NLS-1$
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				animator.step(3);
			}
		});
		add(buttonStep);

		addSeparator(new Dimension(32, 32));

		labelSpeed = new JLabel(Theme.ICON_SPEED);
		labelSpeed.setToolTipText(Messages.getString("Toolbar.frameRate")); //$NON-NLS-1$
		add(labelSpeed);

		spinnerSpeed = new SpeedSpinner();
		spinnerSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent event) {
				animator.setSpeed(((SpeedSpinner)event.getSource()).getSpeedFactor());
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
		buttonMessages.setVisible(logFrame.hasMessages());
		if (logFrame.hasErrors()) {
			buttonMessages.setIcon(Theme.ICON_MESSAGESERROR);
			buttonMessages.setToolTipText(Messages.getString("Toolbar.hintErrors")); //$NON-NLS-1$
		} else {
			buttonMessages.setIcon(Theme.ICON_MESSAGES);
			buttonMessages.setToolTipText(Messages.getString("Toolbar.hintMessages")); //$NON-NLS-1$
		}
	}

	protected void updateControls() {
		final boolean isPaused = (animator != null) && animator.isPaused();

		buttonStart.setEnabled(animator != null);
		buttonReset.setEnabled(animator != null);

		buttonPauseContinue.setEnabled(animator != null);
		buttonStep.setEnabled(isPaused);
		spinnerSpeed.setEnabled(animator != null);
		labelSpeed.setEnabled(animator != null);

		buttonPauseContinue.setIcon(isPaused ? Theme.ICON_PLAY : Theme.ICON_PAUSE);
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
