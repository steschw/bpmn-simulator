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

	private ScrollDesktop desktop = null;

	private WindowMenu menuWindow = new WindowMenu();

	private Toolbar toolbar = new Toolbar();

	private JFileChooser fileChoser = new JFileChooser();

	private Model model = null;

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
		JMenuBar menubar = new JMenuBar();

		JMenu menuFile = new JMenu(Messages.getString("Menu.file")); //$NON-NLS-1$

		JMenuItem menuFileOpen = new JMenuItem(Messages.getString("Menu.open")); //$NON-NLS-1$
		menuFileOpen.setMnemonic(KeyEvent.VK_O);
		menuFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.ALT_MASK));
		menuFileOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		menuFile.add(menuFileOpen);

		JMenuItem menuFileClose = new JMenuItem(Messages.getString("Menu.close")); //$NON-NLS-1$
		menuFileClose.setMnemonic(KeyEvent.VK_C);
		menuFileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
		menuFileClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeFile();
			}
		});
		menuFile.add(menuFileClose);

		menuFile.addSeparator();

		JMenuItem menuFileConfig = new JMenuItem(Messages.getString("Menu.preferences")); //$NON-NLS-1$
		menuFileConfig.setMnemonic(KeyEvent.VK_C);
		menuFileConfig.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.ALT_MASK));
		menuFileConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PreferencesDialog frame = new PreferencesDialog();
				frame.setLocationRelativeTo(BPMNSimulatorFrame.this);
				frame.setVisible(true);
			}
		});
		menuFile.add(menuFileConfig);

		menuFile.addSeparator();

		JMenuItem menuFileExit = new JMenuItem(Messages.getString("Menu.exit"));  //$NON-NLS-1$
		menuFileExit.setMnemonic(KeyEvent.VK_E);
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK));
		menuFileExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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

		return menubar;
	}

	public Toolbar createToolbar() {
		toolbar.getOpenButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
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
			File file = fileChoser.getSelectedFile();
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

	public static void main(String[] args) {
		initLookAndFeel();
		new BPMNSimulatorFrame();
	}

}
