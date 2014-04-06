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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.googlecode.bpmn_simulator.bpmn.di.BPMNDiagram;
import com.googlecode.bpmn_simulator.bpmn.di.DiagramInterchangeModel;
import com.googlecode.bpmn_simulator.gui.instances.InstancesFrame;
import com.googlecode.bpmn_simulator.gui.log.LogFrame;
import com.googlecode.bpmn_simulator.gui.mdi.MdiFrame;
import com.googlecode.bpmn_simulator.gui.mdi.ScrollDesktop.ScrollDesktopPane;
import com.googlecode.bpmn_simulator.gui.preferences.Config;
import com.googlecode.bpmn_simulator.gui.preferences.PreferencesDialog;

@SuppressWarnings("serial")
public class BPMNSimulatorFrame
		extends MdiFrame {

	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;

	private static final String BPMN_DESCRIPTION = "BPMN 2.0 XML"; //$NON-NLS-1$
	private static final String[] BPMN_EXTENSIONS = {"bpmn", "xml"}; //$NON-NLS-1$ //$NON-NLS-2$

	private final LogFrame logFrame = new LogFrame();

	private final Toolbar toolbar = new Toolbar(logFrame);

	private DiagramInterchangeModel model;

	private File file;

	private final InstancesFrame frameInstances = new InstancesFrame();

	public BPMNSimulatorFrame() {
		super();

		updateFrameTitle();

		create();

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	protected void updateFrameTitle() {
		final StringBuilder title = new StringBuilder(BPMNSimulatorApplication.NAME);
		if (file != null) {
			title.append(" - "); //$NON-NLS-1$
			title.append(file.getAbsolutePath());
		}
		setTitle(title.toString());
	}

	protected void showPropertiesDialog() {
		final ModelPropertiesDialog dialog = new ModelPropertiesDialog(this, model);
		dialog.showDialog();
	}

	protected void showPreferencesDialog() {
		final PreferencesDialog dialog = new PreferencesDialog(this);
		dialog.showDialog();
	}

	private void create() {
		setJMenuBar(createMenuBar());

		getContentPane().add(createToolbar(), BorderLayout.PAGE_START);
	}

	protected JMenu createMenuFile() {
		final JMenu menuFile = new JMenu(Messages.getString("Menu.file")); //$NON-NLS-1$

		final JMenuItem menuFileOpen = new JMenuItem(Messages.getString("Menu.fileOpen")); //$NON-NLS-1$
		menuFileOpen.setMnemonic(KeyEvent.VK_O);
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK));
		menuFileOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openFile();
			}
		});
		menuFile.add(menuFileOpen);

		final JMenuItem menuFileReload = new JMenuItem(Messages.getString("Menu.fileReload")); //$NON-NLS-1$
		menuFileReload.setMnemonic(KeyEvent.VK_R);
		menuFileReload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK));
		menuFileReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				reloadModel();
			}
		});
		menuFile.add(menuFileReload);

		final JMenuItem menuFileClose = new JMenuItem(Messages.getString("Menu.fileClose")); //$NON-NLS-1$
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

		final JMenuItem menuFileProperties = new JMenuItem(Messages.getString("Menu.properties")); //$NON-NLS-1$
		menuFileProperties.setMnemonic(KeyEvent.VK_P);
		menuFileProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.ALT_MASK));
		menuFileProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showPropertiesDialog();
			}
		});
		menuFile.add(menuFileProperties);

		menuFile.addSeparator();

		final JMenuItem menuFilePreferences = new JMenuItem(Messages.getString("Menu.preferences")); //$NON-NLS-1$
		menuFilePreferences.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showPreferencesDialog();
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

		menuFile.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				menuFileReload.setEnabled(isModelOpen());
				menuFileClose.setEnabled(isModelOpen());
				menuFileProperties.setEnabled(isModelOpen());
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		return menuFile;
	}

	protected JMenu createMenuExtra() {
		final JMenu menuExtra = new JMenu(Messages.getString("Menu.extra")); //$NON-NLS-1$

		final JMenuItem menuExtraInstances = new JMenuItem(Messages.getString("Menu.instances")); //$NON-NLS-1$
		menuExtraInstances.setMnemonic(KeyEvent.VK_I);
		menuExtraInstances.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.ALT_MASK));
		menuExtraInstances.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				frameInstances.setVisible(true);
			}
		});
		menuExtra.add(menuExtraInstances);

		final JMenuItem menuExtraOpenExternalEditor
				= new JMenuItem(Messages.getString("Menu.openExternal")); //$NON-NLS-1$
		menuExtraOpenExternalEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				if (file != null) {
					final String externalEditor = Config.getInstance().getExternalEditor();
					if ((externalEditor == null) || externalEditor.isEmpty()) {
						showPreferencesDialog();
					} else {
						try {
							Runtime.getRuntime().exec(new String[] {externalEditor, file.getAbsolutePath()});
						} catch (final IOException exception) {
							JOptionPane.showMessageDialog(BPMNSimulatorFrame.this,
									exception.getLocalizedMessage(),
									Messages.getString("error"), //$NON-NLS-1$
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		menuExtra.add(menuExtraOpenExternalEditor);

		menuExtra.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				menuExtraOpenExternalEditor.setEnabled(isModelOpen());
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		return menuExtra;
	}

	protected void showAboutDialog() {
		final AboutDialog frame = new AboutDialog(this);
		frame.showDialog();
	}

	protected JMenu createMenuHelp() {
		final JMenu menuHelp = new JMenu(Messages.getString("Menu.help")); //$NON-NLS-1$

		final JMenuItem menuHelpAbout = new JMenuItem(Messages.getString("Menu.about")); //$NON-NLS-1$
		menuHelpAbout.setMnemonic(KeyEvent.VK_A);
		menuHelpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.ALT_MASK));
		menuHelpAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showAboutDialog();
			}
		});
		menuHelp.add(menuHelpAbout);

		return menuHelp;
	}

	protected JMenuBar createMenuBar() {
		final JMenuBar menubar = new JMenuBar();

		menubar.add(createMenuFile());

		menubar.add(createMenuExtra());

		menubar.add(createWindowMenu());

		menubar.add(createMenuHelp());

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
		final JFileChooser fileChoser = new JFileChooser();
		fileChoser.setFileFilter(new FileNameExtensionFilter(BPMN_DESCRIPTION, BPMN_EXTENSIONS));
		fileChoser.setCurrentDirectory(new File(config.getLastDirectory()));
		if (fileChoser.showOpenDialog(this) == 	JFileChooser.APPROVE_OPTION) {
			config.setLastDirectory(fileChoser.getCurrentDirectory().getAbsolutePath());
			closeFile();
			file = fileChoser.getSelectedFile();
			updateFrameTitle();
			createModel();
		}
	}

	private void createModel() {
		if (file != null) {
			model = new DiagramInterchangeModel();
			model.addStructureExceptionListener(logFrame);
			model.load(file);
			frameInstances.setInstanceManager(model.getInstanceManager());
			toolbar.setModel(model);
			final Collection<BPMNDiagram> diagrams = model.getDiagrams();
			if (diagrams.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						Messages.getString("containsNoDiagrams"), //$NON-NLS-1$
						Messages.getString("information"), //$NON-NLS-1$
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				final ScrollDesktopPane desktop = getDesktop();
				for (final BPMNDiagram diagram : diagrams) {
					final DiagramFrame frame = new DiagramFrame(diagram);
					desktop.add(frame);
					frame.showFrame();
				}
				desktop.arrangeFrames();
			}
		}
	}

	private void closeModel() {
		if (model != null) {
			getDesktop().removeAll();
			toolbar.setModel(null);
			frameInstances.setInstanceManager(null);
			model.close();
			model = null;
			logFrame.clear();
		}
	}

	private void closeFile() {
		closeModel();
		if (file != null) {
			file = null;
			updateFrameTitle();
		}
	}

	private void reloadModel() {
		closeModel();
		createModel();
	}

	private boolean isModelOpen() {
		return model != null;
	}

}
