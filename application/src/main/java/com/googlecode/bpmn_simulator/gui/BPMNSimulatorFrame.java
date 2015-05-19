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
package com.googlecode.bpmn_simulator.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXLoginPane.Status;
import org.jdesktop.swingx.auth.DefaultUserNameStore;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.UserNameStore;
import org.jdesktop.swingx.error.ErrorInfo;

import com.googlecode.bpmn_simulator.animation.element.visual.swing.AbstractSwingDiagram;
import com.googlecode.bpmn_simulator.animation.execution.Animator;
import com.googlecode.bpmn_simulator.animation.token.RootInstances;
import com.googlecode.bpmn_simulator.bpmn.swing.di.SwingBPMNDiagram;
import com.googlecode.bpmn_simulator.bpmn.swing.di.SwingDIDefinition;
import com.googlecode.bpmn_simulator.gui.dialogs.ImageExportChooser;
import com.googlecode.bpmn_simulator.gui.dialogs.WorkingDialog;
import com.googlecode.bpmn_simulator.gui.instances.InstancesFrame;
import com.googlecode.bpmn_simulator.gui.log.LogFrame;
import com.googlecode.bpmn_simulator.gui.mdi.MdiFrame;
import com.googlecode.bpmn_simulator.gui.mdi.ScrollDesktop.ScrollDesktopPane;
import com.googlecode.bpmn_simulator.gui.preferences.Config;
import com.googlecode.bpmn_simulator.gui.preferences.PreferencesDialog;
import com.googlecode.bpmn_simulator.tokenimport.TokenImportException;
import com.googlecode.bpmn_simulator.tokenimport.TokenImporter;
import com.googlecode.bpmn_simulator.tokenimport.bonita.BonitaTokenImporter;

@SuppressWarnings("serial")
public class BPMNSimulatorFrame
		extends MdiFrame {

	private static final Logger LOG = LogManager.getLogger("application");

	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;

	private final RootInstances instances = new RootInstances();

	private final Animator animator = new Animator(instances);

	private final JToolBar definitionToolbar = new JToolBar();
	private final InstancesToolbar instancesToolbar = new InstancesToolbar(instances);
	private final AnimationToolbar animationToolbar = new AnimationToolbar(animator);

	private final LogFrame logFrame = new LogFrame();

	private final JButton messagesButton = new JButton();

	private final InstancesFrame frameInstances = new InstancesFrame(instances);

	private SwingDIDefinition currentDefinition;

	private File currentFile;

	public BPMNSimulatorFrame() {
		super();

		updateFrameTitle();

		create();

		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void updateFrameTitle() {
		final StringBuilder title = new StringBuilder(ApplicationInfo.getName());
		if (currentFile != null) {
			title.append(" - "); //$NON-NLS-1$
			title.append(currentFile.getAbsolutePath());
		}
		setTitle(title.toString());
	}

	public void showPropertiesDialog() {
		final ModelPropertiesDialog dialog = new ModelPropertiesDialog(this, currentDefinition);
		dialog.showDialog();
	}

	public void showPreferencesDialog() {
		final PreferencesDialog dialog = new PreferencesDialog(this);
		dialog.showDialog();
	}

	public void showInstancesFrame() {
		frameInstances.setVisible(true);
	}

	public void showExternalEditor() {
		if (currentFile != null) {
			final String externalEditor = Config.getInstance().getExternalEditor();
			if ((externalEditor == null) || externalEditor.isEmpty()) {
				showPreferencesDialog();
			} else {
				try {
					Runtime.getRuntime().exec(new String[] {externalEditor, currentFile.getAbsolutePath()});
				} catch (final IOException e) {
					showException(e);
				}
			}
		}
	}

	public void showAboutDialog() {
		final AboutDialog frame = new AboutDialog(this);
		frame.showDialog();
	}

	private void create() {
		setJMenuBar(createMenuBar());
		getContentPane().add(createToolBars(), BorderLayout.PAGE_START);
	}

	private JComponent createToolBars() {
		final JPanel panel = new JPanel(new BorderLayout());

		final JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		toolbarPanel.add(createDefinitionToolbar());
		toolbarPanel.add(instancesToolbar);
		toolbarPanel.add(animationToolbar);
		panel.add(toolbarPanel, BorderLayout.CENTER);

		messagesButton.setContentAreaFilled(false);
		messagesButton.setFocusable(false);
		messagesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				logFrame.setVisible(true);
			}
		});
		panel.add(messagesButton, BorderLayout.LINE_END);
		updateMessagesInfo();

		return panel;
	}

	private void updateMessagesInfo() {
		messagesButton.setVisible(logFrame.hasMessages());
		if (logFrame.hasErrors()) {
			messagesButton.setIcon(Theme.ICON_MESSAGESERROR);
			messagesButton.setToolTipText(Messages.getString("Toolbar.hintErrors")); //$NON-NLS-1$
		} else {
			messagesButton.setIcon(Theme.ICON_MESSAGES);
			messagesButton.setToolTipText(Messages.getString("Toolbar.hintMessages")); //$NON-NLS-1$
		}
	}

	private void saveImage(final RenderedImage image, final File file, final String formatName) {
		if (file.exists()
				&& JOptionPane.showConfirmDialog(this,
						MessageFormat.format("File ''{0}'' already exists.\nDo you want to overwrite this file?", file.getName()),
						"File exists",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			ImageIO.write(image, formatName, file);
		} catch (IOException e) {
			showException(e);
		}
	}

	private void exportAsImage() {
		final JInternalFrame frame = getDesktop().getSelectedFrame();
		if (frame instanceof DiagramFrame) {
			final DiagramFrame diagramFrame = (DiagramFrame) frame;
			final ImageExportChooser fileChooser = new ImageExportChooser();
			if (fileChooser.showExportDialog(diagramFrame)) {
				saveImage(diagramFrame.createImage(), fileChooser.getSelectedFile(), fileChooser.getSelectedImageFormat());
			}
		}
	}

	private JMenu createMenuFileExport() {
		final JMenu menuExport = new JMenu("Export as");

		final JMenuItem asImageItem = new JMenuItem("Image");
		asImageItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				exportAsImage();
			}
		});
		menuExport.add(asImageItem);

		return menuExport;
	}

	private JMenu createMenuFile() {
		final JMenu menuFile = new JMenu(Messages.getString("Menu.file")); //$NON-NLS-1$

		final JMenuItem menuFileOpen = new JMenuItem(Messages.getString("Menu.fileOpen")); //$NON-NLS-1$
		menuFileOpen.setMnemonic(KeyEvent.VK_O);
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
		menuFileOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openFile();
			}
		});
		menuFile.add(menuFileOpen);

		final JMenuItem menuFileReload = new JMenuItem(Messages.getString("Menu.fileReload")); //$NON-NLS-1$
		menuFileReload.setMnemonic(KeyEvent.VK_R);
		menuFileReload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK));
		menuFileReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				reloadModel();
			}
		});
		menuFile.add(menuFileReload);

		final JMenuItem menuFileClose = new JMenuItem(Messages.getString("Menu.fileClose")); //$NON-NLS-1$
		menuFileClose.setMnemonic(KeyEvent.VK_C);
		menuFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
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
		menuFileProperties.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK));
		menuFileProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showPropertiesDialog();
			}
		});
		menuFile.add(menuFileProperties);

		menuFile.addSeparator();

		final JMenuItem menuFileExport = createMenuFileExport();
		menuFile.add(menuFileExport);

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
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.ALT_MASK));
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
				menuFileExport.setEnabled(isModelOpen());
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

	private JMenu createMenuExtra() {
		final JMenu menuExtra = new JMenu(Messages.getString("Menu.extra")); //$NON-NLS-1$

		final JMenuItem menuExtraInstances = new JMenuItem(Messages.getString("Menu.instances")); //$NON-NLS-1$
		menuExtraInstances.setMnemonic(KeyEvent.VK_I);
		menuExtraInstances.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK));
		menuExtraInstances.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showInstancesFrame();
			}
		});
		menuExtra.add(menuExtraInstances);

		final JMenuItem menuExtraOpenExternalEditor
				= new JMenuItem(Messages.getString("Menu.openExternal")); //$NON-NLS-1$
		menuExtraOpenExternalEditor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				showExternalEditor();
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

	private JMenu createMenuHelp() {
		final JMenu menuHelp = new JMenu(Messages.getString("Menu.help")); //$NON-NLS-1$

		final JMenuItem menuHelpAbout = new JMenuItem(Messages.getString("Menu.about")); //$NON-NLS-1$
		menuHelpAbout.setMnemonic(KeyEvent.VK_A);
		menuHelpAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
		menuHelpAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showAboutDialog();
			}
		});
		menuHelp.add(menuHelpAbout);

		return menuHelp;
	}

	private JMenuBar createMenuBar() {
		final JMenuBar menubar = new JMenuBar();

		menubar.add(createMenuFile());

		menubar.add(createMenuExtra());

		menubar.add(createWindowMenu());

		menubar.add(createMenuHelp());

		return menubar;
	}

	private JToolBar createDefinitionToolbar() {
		final JButton openButton = new JButton(Theme.ICON_OPEN);
		openButton.setToolTipText(Messages.getString("Toolbar.open")); //$NON-NLS-1$
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				openFile();
			}
		});
		definitionToolbar.add(openButton);

		final JPopupMenu importMenu = new JPopupMenu(Messages.getString("Toolbar.import")); //$NON-NLS-1$
		final JMenuItem importBonitaItem = new JMenuItem("Bonita");
		importBonitaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				importBonita();
			}
		});
		importMenu.add(importBonitaItem);
		final JButton importButton = new JButton(Theme.ICON_IMPORT);
		importButton.setToolTipText(Messages.getString("Toolbar.import")); //$NON-NLS-1$
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				importMenu.show(importButton, 0, importButton.getHeight());
			}
		});
		definitionToolbar.add(importButton);

		return definitionToolbar;
	}

	private void importBonita() {
		final String bonitaHome = Config.getInstance().getBonitaHome();
		if ((bonitaHome == null) || bonitaHome.isEmpty()) {
			showPreferencesDialog();
		} else {
			System.setProperty("bonita.home", bonitaHome);
			final BonitaTokenImporter tokenImporter = new BonitaTokenImporter();
			importTokenSnapshot(tokenImporter);
		}
	}

	private void importTokenSnapshot(final TokenImporter tokenImporter) {
		animator.pause();
		final UserNameStore usernameStore = new DefaultUserNameStore();
		usernameStore.loadUserNames();
		final JXLoginPane pane = new JXLoginPane(new LoginService() {
			@Override
			public boolean authenticate(final String name, final char[] password, final String server)
					throws Exception {
				tokenImporter.login(name, new String(password));
				return true;
			}
		}, null, usernameStore);
		tokenImporter.setDefinition(currentDefinition);
		tokenImporter.setInstances(instances);
		if (JXLoginPane.showLoginDialog(this, pane) == Status.SUCCEEDED) {
			usernameStore.saveUserNames();
			try {
				tokenImporter.importTokens();
			} catch (TokenImportException e) {
				showException(e);
			}
		}
	}

	private void showException(final Throwable throwable) {
		LOG.catching(throwable);
		final ErrorInfo errorInfo = new ErrorInfo(Messages.getString("error"), throwable.getLocalizedMessage(),
				null, null, throwable, null, null);
		JXErrorPane.showDialog(this, errorInfo);
	}

	private void openFile() {
		final Config config = Config.getInstance();
		final JFileChooser fileChoser = new JFileChooser();
		fileChoser.setFileFilter(new FileNameExtensionFilter(SwingDIDefinition.FILE_DESCRIPTION, SwingDIDefinition.FILE_EXTENSIONS));
		fileChoser.setCurrentDirectory(new File(config.getLastDirectory()));
		if (fileChoser.showOpenDialog(this) == 	JFileChooser.APPROVE_OPTION) {
			config.setLastDirectory(fileChoser.getCurrentDirectory().getAbsolutePath());
			openFile(fileChoser.getSelectedFile());
		}
	}

	public void openFile(final File file) {
		closeFile();
		currentFile = file;
		updateFrameTitle();
		createModel();
	}

	private void closeFile() {
		closeModel();
		if (isFileOpen()) {
			currentFile = null;
			updateFrameTitle();
		}
	}

	private boolean isFileOpen() {
		return currentFile != null;
	}

	private void createModel() {
		if (isFileOpen()) {
			final WorkingDialog loadingDialog = new WorkingDialog(this, "Loading");
			currentDefinition = new SwingDIDefinition();
			loadingDialog.run(new Runnable() {
				@Override
				public void run() {
					try (final InputStream input = new FileInputStream(currentFile)) {
						currentDefinition.load(input);
					} catch (IOException e) {
						showException(e);
					}
				}
			});
			final Collection<SwingBPMNDiagram> diagrams = currentDefinition.getDiagrams();
			if (diagrams.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						Messages.getString("containsNoDiagrams"), //$NON-NLS-1$
						Messages.getString("information"), //$NON-NLS-1$
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				final ScrollDesktopPane desktop = getDesktop();
				for (final AbstractSwingDiagram diagram : diagrams) {
					final DiagramFrame frame = new DiagramFrame(diagram);
					desktop.add(frame);
					frame.showFrame();
				}
				desktop.arrangeFrames();
			}
			instancesToolbar.setDefinition(currentDefinition);
			loadingDialog.setVisible(false);
			updateMessagesInfo();
		}
	}

	private boolean isModelOpen() {
		return currentDefinition != null;
	}

	private void closeModel() {
		if (isModelOpen()) {
			getDesktop().removeAll();
			currentDefinition = null;
			logFrame.clear();
		}
	}

	private void reloadModel() {
		closeModel();
		createModel();
	}

}
