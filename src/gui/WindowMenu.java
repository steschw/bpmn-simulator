package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class WindowMenu extends JMenu implements MenuListener {

	private static final long serialVersionUID = 1L;

	private JDesktopPane desktop = null;

	private class WindowMenuItem extends JRadioButtonMenuItem implements ActionListener {

		private static final long serialVersionUID = 1L;

		private JInternalFrame frame = null; 

		public WindowMenuItem(final JInternalFrame frame) {
			super(frame.getTitle(), frame.isSelected());
			setIcon(frame.getFrameIcon());
			this.frame = frame;
			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			frame.moveToFront();
			try {
				frame.setSelected(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

	}

	public WindowMenu() {
		super(Messages.getString("Menu.windows")); //$NON-NLS-1$
		addMenuListener(this);
	}

	public void setDesktopPane(final JDesktopPane desktop) {
		this.desktop = desktop;
	}

	protected JDesktopPane getDesktopPane() {
		return desktop;
	}

	@Override
	public void menuCanceled(MenuEvent event) {
	}

	@Override
	public void menuDeselected(MenuEvent event) {
		removeAll();
	}

	@Override
	public void menuSelected(MenuEvent event) {
		createWindowMenuItems();
	}

	protected List<JInternalFrame> getFrames() {
		ArrayList<JInternalFrame> frames =
				new ArrayList<JInternalFrame>(Arrays.asList(getDesktopPane().getAllFrames())); 
		Collections.sort(frames, new Comparator<JInternalFrame>() {
			@Override
			public int compare(JInternalFrame frame1, JInternalFrame frame2) {
				return frame1.getTitle().compareTo(frame2.getTitle());
			}
		});
		return frames;
	}

	protected void createWindowMenuItems() {
		final JDesktopPane desktopPane = getDesktopPane();
		if (desktopPane != null) {
			for (JInternalFrame frame : getFrames()) {
				add(new WindowMenuItem(frame));
			}
		}
	}

}
