package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JCheckBox checkShowExclusiveSymbol = new JCheckBox(Messages.getString("Preferences.showSymbolInExclusiveGateway"));  //$NON-NLS-1$
	private JCheckBox checkAntialiasing = new JCheckBox(Messages.getString("Preferences.enableAntialiasing"));  //$NON-NLS-1$

	public PreferencesDialog() {
		super((Frame)null, Messages.getString("Preferences.preferences"), true); //$NON-NLS-1$

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createButtonPanel(), BorderLayout.PAGE_END);
		getContentPane().add(createOptionsPanel(), BorderLayout.CENTER);

		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		updateFromConfig();

		pack();
	}

	private static void setButtonWidth(JButton button, final int width) {
		Dimension dimension = button.getPreferredSize();
		dimension.width = width;
		button.setPreferredSize(dimension);
	}

	protected JPanel createOptionsPanel() {
		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(0, 1));

		panel.add(checkShowExclusiveSymbol);
		panel.add(checkAntialiasing);

		return panel;
	}

	protected JPanel createButtonPanel() {
		final int MARGIN = 10;
		final int BUTTON_WIDTH = 100;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));

		panel.add(Box.createHorizontalGlue());

		JButton buttonCancel = new JButton(Messages.getString("Preferences.cancel")); //$NON-NLS-1$
		setButtonWidth(buttonCancel, BUTTON_WIDTH);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonCancel);

		panel.add(Box.createHorizontalStrut(10));

		JButton buttonOk = new JButton(Messages.getString("Preferences.ok"));  //$NON-NLS-1$
		setButtonWidth(buttonOk, BUTTON_WIDTH);
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateToConfig();
				PreferencesDialog.this.dispose();
			}
		});
		panel.add(buttonOk);

		getRootPane().setDefaultButton(buttonOk);

		return panel;
	}

	protected void updateFromConfig() {
		Config config = Config.getInstance();
		checkShowExclusiveSymbol.setSelected(config.getShowExclusiveGatewaySymbol());
		checkAntialiasing.setSelected(config.getAntialiasing());
	}

	protected void updateToConfig() {
		Config config = Config.getInstance();
		config.setShowExclusiveGatewaySymbol(checkShowExclusiveSymbol.isSelected());
		config.setAntialiasing(checkAntialiasing.isSelected());
		config.store();
	}

}
