package gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

public class ScrollDesktop extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private class ScrollDesktopPane extends JDesktopPane {

		private static final long serialVersionUID = 1L;

		private class ScrollDesktopManager extends DefaultDesktopManager {

			private static final long serialVersionUID = 1L;

			private ScrollDesktopPane desktopPane = null;

			public ScrollDesktopManager(final ScrollDesktopPane desktopPane) {
				super();
				this.desktopPane = desktopPane;
			}

			protected ScrollDesktopPane getPane() {
				return desktopPane; 
			}

			@Override
			public void dragFrame(JComponent f, int newX, int newY) {
				super.dragFrame(f, newX, newY);
			}

			@Override
			public void resizeFrame(JComponent f, int newX, int newY,
					int newWidth, int newHeight) {
				super.resizeFrame(f, newX, newY, newWidth, newHeight);
			}

		}

		private ScrollDesktop scrollDesktop = null;

		public ScrollDesktopPane(final ScrollDesktop scrollDesktop) {
			super();
			this.scrollDesktop = scrollDesktop;
			setDesktopManager(new ScrollDesktopManager(this));
			setBackground(Color.LIGHT_GRAY);
		}

		protected ScrollDesktop getDesktop() {
			return scrollDesktop;
		}

		public void arrangeFrames() {
			int x = 0;
			int y = 0;
			int maxHeight = 0;
			final Dimension desktopSize = getSize();
			for (JInternalFrame frame : getAllFrames()) {
				final Dimension frameSize = frame.getSize();
				final int width = (int)frameSize.getWidth();
				final int height = (int)frameSize.getHeight(); 
				frame.setLocation(x, y);
				if ((x + width) > desktopSize.getWidth()) {
					x = 0;
					y += maxHeight;
					maxHeight = height;
				} else {
					x += width;
					maxHeight = Math.max(maxHeight, height);
				}
			}
		}

		public void setFixedSize(final Dimension dimension) {
			setMinimumSize(dimension);
			setMaximumSize(dimension);
			setPreferredSize(dimension);
		}

	}

	private ScrollDesktopPane desktopPane = new ScrollDesktopPane(this); 

	public ScrollDesktop() {
		super();
		getViewport().add(desktopPane);
	}

	public ScrollDesktopPane getDesktopPane() {
		return desktopPane;
	}

	public void arrangeFrames() {
		getDesktopPane().arrangeFrames();
	}

}
