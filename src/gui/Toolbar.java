package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bpmn.Model;
import bpmn.token.AnimationListener;
import bpmn.token.Animator;


public class Toolbar extends JToolBar implements AnimationListener {

	private static final long serialVersionUID = 1L;

	private Icon iconOpen = null;

	private Icon iconStart = null;
	private Icon iconReset = null;

	private Icon iconPause = null;
	private Icon iconPlay = null;
	private Icon iconStep = null;
	private Icon iconSpeed = null;
	private Icon iconMessages = null;
	private Icon iconMessagesError = null;

	private JButton buttonOpen = null;

	private StartButton buttonStart = null;
	private JButton buttonReset = null;

	private JButton buttonPauseContinue = null;
	private JButton buttonStep = null;

	private JLabel labelSpeed = null; 
	private SpeedSpinner spinnerSpeed = new SpeedSpinner(); 

	private JButton buttonMessages = null;

	private Model model = null;

	public Toolbar() {
		super();

		create();
	}

	public void setModel(final Model model) {
		Animator animator = getAnimator();
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

	protected Animator getAnimator() {
		if (model != null) {
			return model.getAnimator();
		}
		return null;
	}

	public JButton getOpenButton() {
		return buttonOpen;
	}

	protected void create() {

		loadIcons();

		buttonOpen = new JButton(iconOpen);
		buttonOpen.setToolTipText(Messages.getString("Toolbar.open")); //$NON-NLS-1$
		add(buttonOpen);

		addSeparator();

		buttonStart = new StartButton(iconStart);
		buttonStart.setToolTipText(Messages.getString("Toolbar.start")); //$NON-NLS-1$
		buttonStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
			}
		});
		add(buttonStart);

		buttonReset = new JButton(iconReset);
		buttonReset.setToolTipText(Messages.getString("Toolbar.reset")); //$NON-NLS-1$
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				getAnimator().reset();
			}
		});
		add(buttonReset);

		addSeparator(new Dimension(32, 32));

		buttonPauseContinue = new JButton();
		buttonPauseContinue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				final Animator animator = getAnimator();
				if (animator.isPaused()) {
					animator.play();
				} else {
					animator.pause();
				}
			}
		});
		add(buttonPauseContinue);

		buttonStep = new JButton(iconStep);
		buttonStep.setToolTipText(Messages.getString("Toolbar.step")); //$NON-NLS-1$
		buttonStep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				getAnimator().step(3);
			}
		});
		add(buttonStep);

		addSeparator();

		labelSpeed = new JLabel(iconSpeed);
		labelSpeed.setToolTipText(Messages.getString("Toolbar.frameRate")); //$NON-NLS-1$
		labelSpeed.setLabelFor(spinnerSpeed);
		add(labelSpeed);
		add(Box.createHorizontalStrut(6));
		spinnerSpeed.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event) {
				getAnimator().setSpeed(((SpeedSpinner)event.getSource()).getSpeedFactor());
			}
		});
		add(spinnerSpeed);

		add(Box.createHorizontalGlue());

		buttonMessages = new JButton();
		buttonMessages.setBorderPainted(false);
		buttonMessages.setOpaque(false);
		buttonMessages.setFocusable(false);
		buttonMessages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.showMessages();
			}
		});
		add(buttonMessages);

		updateControls();
		updateMessages();
	}

	private void loadIcons() {
		iconOpen = new ImageIcon(getClass().getResource("open.png")); //$NON-NLS-1$

		iconStart = new ImageIcon(getClass().getResource("start.png")); //$NON-NLS-1$
		iconReset = new ImageIcon(getClass().getResource("stop.png")); //$NON-NLS-1$

		iconPause = new ImageIcon(getClass().getResource("pause.png")); //$NON-NLS-1$
		iconPlay = new ImageIcon(getClass().getResource("play.png")); //$NON-NLS-1$
		iconStep = new ImageIcon(getClass().getResource("step.png")); //$NON-NLS-1$
		iconSpeed = new ImageIcon(getClass().getResource("speed.png")); //$NON-NLS-1$
		iconMessages = new ImageIcon(getClass().getResource("messages.png")); //$NON-NLS-1$
		iconMessagesError = new ImageIcon(getClass().getResource("messagesError.png")); //$NON-NLS-1$
	}

	protected void updateMessages() {
		if (model != null) {
			buttonMessages.setVisible(model.hasMessages());
			if (model.hasErrorMessages()) {
				buttonMessages.setIcon(iconMessagesError);
				buttonMessages.setToolTipText(Messages.getString("Toolbar.hintErrors")); //$NON-NLS-1$
			} else {
				buttonMessages.setIcon(iconMessages);
				buttonMessages.setToolTipText(Messages.getString("Toolbar.hintMessages")); //$NON-NLS-1$
			}
		} else {
			buttonMessages.setVisible(false);
			buttonMessages.setToolTipText(null);
		}
	}

	protected void updateControls() {
		final Animator animator = getAnimator();

		final boolean isPaused = ((animator != null) && animator.isPaused());

		buttonStart.setEnabled(animator != null);
		buttonReset.setEnabled(animator != null);

		buttonPauseContinue.setEnabled(animator != null);
		buttonStep.setEnabled(isPaused);
		spinnerSpeed.setEnabled(animator != null);
		labelSpeed.setEnabled(animator != null);

		buttonPauseContinue.setIcon(isPaused ? iconPlay : iconPause);
		buttonPauseContinue.setToolTipText(isPaused ? Messages.getString("Toolbar.play") : Messages.getString("Toolbar.pause")); //$NON-NLS-1$ //$NON-NLS-2$
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
