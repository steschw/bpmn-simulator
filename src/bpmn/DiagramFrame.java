package bpmn;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;

import bpmn.element.BaseElement;
import bpmn.element.Collaboration;
import bpmn.element.ExpandedProcess;

public class DiagramFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	public DiagramFrame(final BaseElement container) {
		super(container.getName(), true, false, true);
		setContentPane(new JScrollPane(container));
		setFrameIcon(container);
	}

	protected static ImageIcon loadFrameIcon(final String filename) {
		final URL url = DiagramFrame.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	protected void setFrameIcon(final BaseElement container) {
		ImageIcon icon = null;
		if (container instanceof ExpandedProcess) {
			icon = loadFrameIcon("process.png");
		} else if (container instanceof Collaboration) {
			icon = loadFrameIcon("collaboration.png");
		}
		setFrameIcon(icon);
	}

	public void showFrame() {
		setLocation(0, 0);
		setVisible(true);
		pack();
	}

}
