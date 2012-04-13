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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import bpmn.Model;

public class BPMNSimulatorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private ScrollDesktop desktop;

	private final WindowMenu menuWindow = new WindowMenu();

	private final Toolbar toolbar = new Toolbar();

	private final JFileChooser fileChoser = new JFileChooser();

	private Model model;

	public BPMNSimulatorFrame() {
		super();

		updateFrameTitle(null);

		create();

		Config.getInstance().load();

		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	protected void updateFrameTitle(final String filename) {
		String title = "BPMN Simulator"; //$NON-NLS-1$
		if ((filename != null) && !filename.isEmpty()) {
			title += " - " + filename; //$NON-NLS-1$
		}
		setTitle(title);
	}

	private void create() {
		setLayout(new BorderLayout());

		setJMenuBar(createMenuBar());

		add(createToolbar(), BorderLayout.PAGE_START);

		desktop = new ScrollDesktop();
		add(desktop, BorderLayout.CENTER);
	}

	private JMenuBar createMenuBar() {
		final JMenuBar menubar = new JMenuBar();

		final JMenu menuFile = new JMenu(Messages.getString("Menu.file")); //$NON-NLS-1$

		final JMenuItem menuFileOpen = new JMenuItem(Messages.getString("Menu.open")); //$NON-NLS-1$
		menuFileOpen.setMnemonic(KeyEvent.VK_O);
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK));
		menuFileOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openFile();
			}
		});
		menuFile.add(menuFileOpen);

		final JMenuItem menuFileClose = new JMenuItem(Messages.getString("Menu.close")); //$NON-NLS-1$
		menuFileClose.setMnemonic(KeyEvent.VK_C);
		menuFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
		menuFileClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				closeFile();
			}
		});
		menuFile.add(menuFileClose);

		menuFile.addSeparator();

		final JMenuItem menuFilePreferences = new JMenuItem(Messages.getString("Menu.preferences")); //$NON-NLS-1$
		menuFilePreferences.setMnemonic(KeyEvent.VK_P);
		menuFilePreferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK));
		menuFilePreferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final PreferencesDialog frame = new PreferencesDialog();
				frame.setLocationRelativeTo(BPMNSimulatorFrame.this);
				frame.setVisible(true);
			}
		});
		menuFile.add(menuFilePreferences);

		menuFile.addSeparator();

		final JMenuItem menuFileExit = new JMenuItem(Messages.getString("Menu.exit"));  //$NON-NLS-1$
		menuFileExit.setMnemonic(KeyEvent.VK_E);
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK));
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				for (Frame frame : getFrames()) {
					if (frame.isActive()) {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				}
			}
		});
		menuFile.add(menuFileExit);

		menubar.add(menuFile);

		menubar.add(menuWindow);

		final JMenu menuHelp = new JMenu(Messages.getString("Menu.help")); //$NON-NLS-1$

		final JMenuItem menuHelpAbout = new JMenuItem(Messages.getString("Menu.about")); //$NON-NLS-1$
		menuHelpAbout.setMnemonic(KeyEvent.VK_A);
		menuHelpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK));
		menuHelpAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final AboutDialog frame = new AboutDialog();
				frame.setLocationRelativeTo(BPMNSimulatorFrame.this);
				frame.setVisible(true);
			}
		});
		menuHelp.add(menuHelpAbout);

		menubar.add(menuHelp); 

		return menubar;
	}

	public Toolbar createToolbar() {
		toolbar.getOpenButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				openFile();
			}
		});
		return toolbar;		
	}

	private void openFile() {
		final Config config = Config.getInstance();
		fileChoser.setFileFilter(new FileNameExtensionFilter("BPMN 2.0 XML", "bpmn")); //$NON-NLS-1$ //$NON-NLS-2$
		fileChoser.setCurrentDirectory(new File(config.getLastDirectory()));
		if (fileChoser.showOpenDialog(this) == 	JFileChooser.APPROVE_OPTION) {
			config.setLastDirectory(fileChoser.getCurrentDirectory().getAbsolutePath());
			config.store();
			closeFile();
			model = new Model(desktop.getDesktopPane());
			final File file = fileChoser.getSelectedFile();
			model.load(file);
			menuWindow.setDesktopPane(desktop.getDesktopPane());
			toolbar.setModel(model);
			updateFrameTitle(file.getAbsolutePath());
			if (model.hasErrorMessages()) {
				model.showMessages();
			}
			desktop.arrangeFrames();
		}
	}

	private void closeFile() {
		if (model != null) {
			toolbar.setModel(null);
			menuWindow.setDesktopPane(null);
			model.close();
			model = null;
			updateFrameTitle(null);
		}
	}

	private static void initLookAndFeel() {
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//JFrame.setDefaultLookAndFeelDecorated(true);
	}

	public static void main(final String[] args) {
		initLookAndFeel();
		new BPMNSimulatorFrame();
	}

}
