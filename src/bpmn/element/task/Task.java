package bpmn.element.task;

import java.awt.Point;
import java.net.URL;

import javax.swing.ImageIcon;

import bpmn.element.Activity;
import bpmn.element.Graphics;
import bpmn.element.Rectangle;


public class Task extends Activity {

	private static final long serialVersionUID = 1L;

	private ImageIcon typeIcon = null;

	protected static ImageIcon loadTypeIcon(final String filename) {
		final URL url = Task.class.getResource(filename);
		if (url != null) {
			return new ImageIcon(url);
		}
		return null;
	}

	public Task(final String id, final String name) {
		super(id, name);
	}

	protected Task(final String id, final String name, final ImageIcon icon) {
		this(id, name);
		this.typeIcon = icon;
	}

	@Override
	protected int getStepCount() {
		return 50;
	}

	@Override
	protected void paintBackground(Graphics g) {
		g.fillRoundRect(getElementInnerBounds(), 10, 10);
	}

	public void paintTypeIcon(Graphics g) {
		if (typeIcon != null) {
			final Rectangle innerBounds = getElementInnerBounds();
			final Point position = innerBounds.getLeftTop();
			position.translate(6, 6);
			g.drawIcon(typeIcon, position);
		}
	}

	@Override
	protected void paintElement(Graphics g) {
		g.drawRoundRect(getElementInnerBounds(), 10, 10);
		paintTypeIcon(g);
	}

}
