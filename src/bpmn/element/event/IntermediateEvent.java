package bpmn.element.event;

import java.awt.Color;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;

@SuppressWarnings("serial")
public abstract class IntermediateEvent extends Event {

	private static final int CIRCLE_MARGIN = 4;

	private static Color defaultBackground;

	public static final void setDefaultBackground(final Color color) {
		defaultBackground = color;
	}

	public static final Color getDefaultBackground() {
		return defaultBackground;
	}

	public IntermediateEvent(final String id, final String name) {
		super(id, name, null);
		setBackground(getDefaultBackground());
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		final Rectangle bounds = getElementInnerBounds();
		bounds.grow(-CIRCLE_MARGIN, -CIRCLE_MARGIN);
		g.drawOval(bounds);
	}

}
