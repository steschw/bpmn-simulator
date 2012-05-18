package bpmn.element.event;

import java.awt.Color;

import bpmn.element.Graphics;
import bpmn.element.Rectangle;
import bpmn.element.VisualConfig;

@SuppressWarnings("serial")
public abstract class IntermediateEvent extends AbstractEvent {

	private static final int CIRCLE_MARGIN = 4;

	public IntermediateEvent(final String id, final String name) {
		super(id, name, null);
	}

	@Override
	protected Color getElementDefaultBackground() {
		return getVisualConfig().getBackground(VisualConfig.Element.EVENT_INTERMEDIATE);
	}

	@Override
	protected void paintElement(final Graphics g) {
		super.paintElement(g);

		final Rectangle bounds = getElementInnerBounds();
		bounds.grow(-CIRCLE_MARGIN, -CIRCLE_MARGIN);
		g.drawOval(bounds);
	}

}
